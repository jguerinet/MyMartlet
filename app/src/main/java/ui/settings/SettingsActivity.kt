/*
 * Copyright 2014-2019 Julien Guerinet
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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import com.guerinet.morf.Morf
import com.guerinet.morf.TextViewItem
import com.guerinet.morf.morf
import com.guerinet.morf.util.Position
import com.guerinet.mymartlet.BuildConfig
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.ui.settings.about.AboutActivity
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.room.UpdateDao
import com.guerinet.suitcase.coroutines.bgDispatcher
import com.guerinet.suitcase.coroutines.uiDispatcher
import com.guerinet.suitcase.dialog.singleListDialog
import com.guerinet.suitcase.io.getFileUri
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.util.Utils
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Allows the user to change the app settings
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SettingsActivity : DrawerActivity() {

    private val statsPref by inject<BooleanPref>(Prefs.STATS)

    private val twentyFourHourPref by inject<BooleanPref>(Prefs.SCHEDULE_24HR)

    private val usernamePref by inject<UsernamePref>()

    private val updateDao by inject<UpdateDao>()

    override val currentPage = HomepageManager.HomePage.SETTINGS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.settings_version, BuildConfig.VERSION_NAME)
        ga.sendScreen("Settings")

        container.morf {

            val morf = Morf.bind(container)

            // 24 hour time preference
            aSwitch {
                textId = R.string.settings_twentyfourhours
                icon(Position.START, R.drawable.ic_clock)
                isChecked = twentyFourHourPref.value
                onCheckChanged { _, isChecked ->
                    twentyFourHourPref.value = isChecked
                }
            }

            // Home Page
            text {
                text = homePageManager.titleString
                icon(Position.START, R.drawable.ic_phone_android)
                onClick { item: TextViewItem ->
                    // Get the list of home pages in alphabetical order for the current language
                    val homePages = HomepageManager.HomePage.values()
                        .map { Pair(it, homePageManager.getTitle(it)) }
                        .sortedWith(Comparator { o1, o2 -> o1.second.compareTo(o2.second) })

                    val currentChoice =
                        homePages.indexOfFirst { it.first == homePageManager.homePage }

                    val choices = homePages.map { it.second }.toTypedArray()

                    singleListDialog(choices, R.string.settings_homepage_title, currentChoice) {
                        // Update the instance
                        homePageManager.homePage = homePages[it].first

                        // Update the TextView
                        item.text = homePageManager.titleString

                        ga.sendEvent("Settings", "HomePageManager", homePageManager.title)
                    }
                }
            }

            // Statistics
            aSwitch {
                textId = R.string.settings_statistics
                icon(Position.START, R.drawable.ic_trending_up)
                isChecked = statsPref.value
                onCheckChanged { _, isChecked ->
                    statsPref.value = isChecked
                }
            }

            // Help
            text {
                textId = R.string.title_help
                icon(Position.START, R.drawable.ic_help)
                onClick { startActivity<HelpActivity>() }
            }

            // About
            text {
                textId = R.string.title_about
                icon(Position.START, R.drawable.ic_info)
                onClick { startActivity<AboutActivity>() }
            }

            // Bug Report
            morf.text {
                textId = R.string.title_report_bug
                icon(Position.START, R.drawable.ic_bug_report)
                onClick {
                    ga.sendEvent("About", "Report a Bug")

                    val intent = Intent(Intent.ACTION_SEND).apply {

                        // Code (Email)
                        type = "message/rfc822"

                        // Recipient
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("julien.guerinet@mail.mcgill.ca"))

                        // Title
                        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report))

                        // Content
                        val content =
                            Utils.getDebugInfo(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

                        // Log everything in the logs
                        Timber.i(content)

                        putExtra(Intent.EXTRA_TEXT, content)
                    }

                    launch(uiDispatcher) {

                        val uriList = ArrayList<Uri>()

                        withContext(bgDispatcher) {
                            // Update logs (attachment)
                            try {
                                val appUpdates = updateDao.getAll()

                                // Create the file that will hold the update logs
                                val file = File(getExternalFilesDir(null), "update_logs.log")

                                // Create the Okio bugger to write the logs
                                val sink = file.sink().buffer()

                                appUpdates.forEach { update ->
                                    // Write the updates to the file
                                    sink.writeUtf8(
                                        "Version: ${update.version}\nDate: ${update.timestamp}\n\n"
                                    )
                                }
                                sink.flush()

                                uriList.add(getFileUri(BuildConfig.APPLICATION_ID, file))
                            } catch (e: Exception) {
                                Timber.e(e, "Error attaching update logs to bug report email")
                            }

                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList)
                        }
                    }

                    startActivity(Intent.createChooser(intent, null))
                }
            }
        }
    }
}