/*
 * Copyright 2014-2017 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.LinearLayout;

import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;
import com.guerinet.utils.prefs.BooleanPreference;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.BuildConfig;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.AppUpdate;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.list.HomepagesAdapter;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import okio.BufferedSink;
import okio.Okio;
import timber.log.Timber;

/**
 * Allows the user to change the app settings
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SettingsActivity extends DrawerActivity {
    /**
     * The {@link FormGenerator} container
     */
    @BindView(R.id.container)
    LinearLayout container;
    /**
     * Statistics BooleanPreference
     */
    @Inject
    @Named(PrefsModule.STATS)
    BooleanPreference statsPref;
    /**
     * 24 hour time BooleanPreference
     */
    @Inject
    @Named(PrefsModule.SCHEDULE_24HR)
    BooleanPreference twentyFourHourPref;
    /**
     * {@link UsernamePreference} instance
     */
    @Inject
    UsernamePreference usernamePref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        setTitle(getString(R.string.settings_version, Utils.versionName(this)));
        analytics.sendScreen("Settings");

        FormGenerator fg = FormGenerator.bind(container);

        // 24 hour time preference
        fg.aSwitch()
                .text(R.string.settings_twentyfourhours)
                .leftIcon(R.drawable.ic_clock)
                .checked(twentyFourHourPref.get())
                .onCheckChanged((buttonView, isChecked) -> twentyFourHourPref.set(isChecked))
                .build();

        // Homepage choice
        fg.text()
                .text(homepageManager.getTitleString())
                .leftIcon(R.drawable.ic_phone_android)
                .onClick(item -> DialogUtils.list(this, R.string.settings_homepage_title,
                        new HomepagesAdapter(this) {
                            @Override
                            public void onHomepageSelected(@HomepageManager.Homepage int choice) {
                                // Update the instance
                                homepageManager.set(choice);

                                analytics.sendEvent("Settings", "HomepageManager",
                                        homepageManager.getString());

                                // Update the TextView
                                item.view().setText(homepageManager.getTitleString());
                            }
                        }))
                .build();

        // Statistics
        fg.aSwitch()
                .text(R.string.settings_statistics)
                .leftIcon(R.drawable.ic_trending_up)
                .checked(statsPref.get())
                .onCheckChanged((buttonView, isChecked) -> statsPref.set(isChecked))
                .build();

        // Help
        fg.text()
                .text(R.string.title_help)
                .leftIcon(R.drawable.ic_help)
                .onClick(item -> startActivity(new Intent(this, HelpActivity.class)))
                .build();

        // About
        fg.text()
                .text(R.string.title_about)
                .leftIcon(R.drawable.ic_info)
                .onClick(item -> startActivity(new Intent(this, AboutActivity.class)))
                .build();

        // Bug Report
        fg.text()
                .text(R.string.title_report_bug)
                .leftIcon(R.drawable.ic_bug_report)
                .onClick(item -> {
                    analytics.sendEvent("About", "Report a Bug");

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    // Recipient
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]
                            {"julien.guerinet@mail.mcgill.ca"});

                    // Title
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report));

                    // Content
                    String device = "Device: " + Utils.device();
                    String sdkVersion = "SDK Version: " + Build.VERSION.SDK_INT;
                    String appVersion = "App Version: " + Utils.versionName(this);
                    String language = "Language: " + Locale.getDefault().getLanguage();

                    ConnectivityManager manager = (ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = manager.getActiveNetworkInfo();

                    String connection = "Connection Type: N/A";
                    if (info != null) {
                        connection = "Connection Type: " + info.getTypeName() + " " +
                                info.getSubtypeName();
                    }

                    String content = "===============" +
                            "\nDebug Info" +
                            "\n===============" +
                            "\n" + device +
                            "\n" + sdkVersion +
                            "\n" + appVersion +
                            "\n" + language +
                            "\n" + connection +
                            "\n===============\n\n";
                    intent.putExtra(Intent.EXTRA_TEXT, content);

                    // Log everything before printing the logs so it's included
                    Timber.i(device);
                    Timber.i(sdkVersion);
                    Timber.i(appVersion);
                    Timber.i(language);
                    Timber.i(connection);

                    ArrayList<Uri> uriList = new ArrayList<>();

                    // Logs (attachment)
                    try {
                        File file = new File(getExternalFilesDir(null), "logs.txt");
                        Utils.getLogs(file);
                        uriList.add(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID +
                                ".fileProvider", file));
                    } catch (IOException e) {
                        Timber.e(new Exception("Error getting logs", e));
                    }

                    // Update logs (attachment)
                    try {
                        List<AppUpdate> appUpdates = SQLite.select()
                                .from(AppUpdate.class)
                                .queryList();

                        // Create the file that will hold the update logs
                        File file = new File(getExternalFilesDir(null), "update_logs.log");

                        // Create the Okio bugger to write the logs
                        BufferedSink sink = Okio.buffer(Okio.sink(file));

                        for (AppUpdate update : appUpdates) {
                            // Write the updates to the file
                            sink.writeUtf8("Version: " + update.getVersion() + "\nDate: " +
                                    update.getTimestamp().toString() + "\n\n");
                        }
                        sink.flush();

                        uriList.add(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID +
                                ".fileProvider", file));
                    } catch (Exception e) {
                        Timber.e(e, "Error attaching update logs to feedback/bug report email");
                    }

                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);

                    // Code (Email)
                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, null));
                })
                .build();
    }

    @HomepageManager.Homepage
    @Override
    protected int getCurrentPage() {
        return HomepageManager.SETTINGS;
    }
}