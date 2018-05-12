/*
 * Copyright 2014-2018 Julien Guerinet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guerinet.mymartlet.ui.settings

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.widget.CompoundButton
import com.guerinet.morf.Morf
import com.guerinet.morf.TextViewItem
import com.guerinet.morf.util.Position
import com.guerinet.mymartlet.BuildConfig
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.AppUpdate
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.ui.dialog.list.HomepagesAdapter
import com.guerinet.mymartlet.ui.settings.about.AboutActivity
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.dagger.prefs.UsernamePref
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.suitcase.dialog.singleListDialog
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.util.Device
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_settings.*
import okio.Okio
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.util.*

/**
 * Allows the user to change the app settings
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SettingsActivity : DrawerActivity() {

    private val statsPref by inject<BooleanPref>(Prefs.STATS)

    private val twentyFourHourPref by inject<BooleanPref>(Prefs.SCHEDULE_24HR)

    private val usernamePref by inject<UsernamePref>()

    override val currentPage = HomepageManager.HomePage.SETTINGS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.settings_version, BuildConfig.VERSION_NAME)
        ga.sendScreen("Settings")

        val morf = Morf.bind(container)

        // 24 hour time preference
        morf.aSwitch {
            text(R.string.settings_twentyfourhours)
            icon(Position.START, R.drawable.ic_clock)
            checked(twentyFourHourPref.value)
            onCheckChanged(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                twentyFourHourPref.value = isChecked
            })
        }

        // Homepage choice
        morf.text {
            text(homePageManager.titleString)
            icon(Position.START, R.drawable.ic_phone_android)
            onClick({ item: TextViewItem ->
                singleListDialog(R.string.settings_homepage_title, object : HomepagesAdapter() {

                    override fun onHomePageSelected(homePage: HomepageManager.HomePage) {
                        // Update the instance
                        homePageManager.homePage = homePage

                        ga.sendEvent("Settings", "HomepageManager", homePageManager.title)

                        // Update the TextView
                        item.text(homePageManager.titleString)
                    }
                })
            })
        }

        // Statistics
        morf.aSwitch {
            text(R.string.settings_statistics)
            icon(Position.START, R.drawable.ic_trending_up)
            checked(statsPref.value)
            onCheckChanged(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                statsPref.value = isChecked
            })
        }

        // Help
        morf.text {
            text(R.string.title_help)
            icon(Position.START, R.drawable.ic_help)
            onClick { startActivity<HelpActivity>() }
        }

        // About
        morf.text {
            text(R.string.title_about)
            icon(Position.START, R.drawable.ic_info)
            onClick { startActivity<AboutActivity>() }
        }

        // Bug Report
        morf.text {
            text(R.string.title_report_bug)
            icon(Position.START, R.drawable.ic_bug_report)
            onClick {
                ga.sendEvent("About", "Report a Bug")

                val intent = Intent(Intent.ACTION_SEND)

                // Recipient
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("julien.guerinet@mail.mcgill.ca"))

                // Title
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report))

                // TODO Replace with Suitcase
                // Content
                val device = "Device: ${Device.model()}"
                val sdkVersion = "SDK Version: ${Build.VERSION.SDK_INT}"
                val appVersion = "App Version: ${BuildConfig.VERSION_NAME}"
                val language = "Language: ${Locale.getDefault().language}"

                val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val info = manager.activeNetworkInfo

                var connection = "Connection Type: N/A"
                if (info != null) {
                    connection = "Connection Type: " + info.typeName + " " +
                            info.subtypeName
                }

                val content = "===============" +
                        "\nDebug Info" +
                        "\n===============" +
                        "\n" + device +
                        "\n" + sdkVersion +
                        "\n" + appVersion +
                        "\n" + language +
                        "\n" + connection +
                        "\n===============\n\n"
                intent.putExtra(Intent.EXTRA_TEXT, content)

                // Log everything before printing the logs so it's included
                Timber.i(content)

                val uriList = ArrayList<Uri>()
                // Logs (attachment)
                // TODO
                /*
                try {
                    File file = new File(getExternalFilesDir(null), "logs.txt");
                    Utils.getLogs(file);
                    uriList.add(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID +
                            ".fileProvider", file));
                } catch (IOException e) {
                    Timber.e(new Exception("Error getting logs", e));
                }
                */

                // Update logs (attachment)
                try {
                    val appUpdates = SQLite.select()
                            .from(AppUpdate::class)
                            .queryList()

                    // Create the file that will hold the update logs
                    val file = File(getExternalFilesDir(null), "update_logs.log")

                    // Create the Okio bugger to write the logs
                    val sink = Okio.buffer(Okio.sink(file))

                    for (update in appUpdates) {
                        // Write the updates to the file
                        sink.writeUtf8("Version: ${update.version}\nDate: " +
                                update.timestamp.toString() + "\n\n")
                    }
                    sink.flush()

                    uriList.add(FileProvider.getUriForFile(this@SettingsActivity,
                            BuildConfig.APPLICATION_ID + ".fileProvider", file))
                } catch (e: Exception) {
                    Timber.e(e, "Error attaching update logs to feedback/bug report email")
                }

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)

                // Code (Email)
                intent.type = "message/rfc822"
                startActivity(Intent.createChooser(intent, null))
            }
        }
    }
}