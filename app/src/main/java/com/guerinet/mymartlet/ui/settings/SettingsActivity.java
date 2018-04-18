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

package com.guerinet.mymartlet.ui.settings;

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

import com.guerinet.morf.Morf;
import com.guerinet.morf.util.Position;
import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.BuildConfig;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.AppUpdate;
import com.guerinet.mymartlet.ui.DrawerActivity;
import com.guerinet.mymartlet.ui.dialog.list.HomepagesAdapter;
import com.guerinet.mymartlet.util.dagger.prefs.PrefsModuleKt;
import com.guerinet.mymartlet.util.dagger.prefs.UsernamePref;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.suitcase.dialog.DialogUtils;
import com.guerinet.suitcase.prefs.BooleanPref;
import com.guerinet.suitcase.util.Device;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
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
     * The {@link Morf} container
     */
    @BindView(R.id.container)
    LinearLayout container;
    /**
     * Statistics BooleanPref
     */
    @Inject
    @Named(PrefsModuleKt.STATS)
    BooleanPref statsPref;
    /**
     * 24 hour time BooleanPref
     */
    @Inject
    @Named(PrefsModuleKt.SCHEDULE_24HR)
    BooleanPref twentyFourHourPref;

    @Inject
    UsernamePref usernamePref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        App.Companion.component(this).inject(this);
        setTitle(getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        getGa().sendScreen("Settings");

        Morf morf = Morf.Companion.bind(container);

        // 24 hour time preference
        morf.aSwitch()
                .text(R.string.settings_twentyfourhours)
                .icon(Position.START, R.drawable.ic_clock)
                .checked(twentyFourHourPref.get())
                .onCheckChanged((buttonView, isChecked) -> twentyFourHourPref.set(isChecked))
                .build();

        // Homepage choice
        morf.text()
                .text(homepageManager.getTitleString())
                .icon(Position.START, R.drawable.ic_phone_android)
                .onClick(item -> {
                    DialogUtils.singleList(this, R.string.settings_homepage_title,
                            new HomepagesAdapter(this) {

                                @Override
                                public void onHomepageSelected(@HomepageManager.Homepage int
                                        choice) {
                                    // Update the instance
                                    homepageManager.set(choice);

                                    getGa().sendEvent("Settings", "HomepageManager",
                                            homepageManager.getString());

                                    // Update the TextView
                                    item.text(homepageManager.getTitleString());
                                }
                            });
                    return Unit.INSTANCE;
                })
                .build();

        // Statistics
        morf.aSwitch()
                .text(R.string.settings_statistics)
                .icon(Position.START, R.drawable.ic_trending_up)
                .checked(statsPref.get())
                .onCheckChanged((buttonView, isChecked) -> statsPref.set(isChecked))
                .build();

        // Help
        morf.text()
                .text(R.string.title_help)
                .icon(Position.START, R.drawable.ic_help)
                .onClick(item -> {
                    startActivity(new Intent(this, HelpActivity.class));
                    return Unit.INSTANCE;
                })
                .build();

        // About
        morf.text()
                .text(R.string.title_about)
                .icon(Position.START, R.drawable.ic_info)
                .onClick(item -> {
                    startActivity(new Intent(this, AboutActivity.class));
                    return Unit.INSTANCE;
                })
                .build();

        // Bug Report
        morf.text()
                .text(R.string.title_report_bug)
                .icon(Position.START, R.drawable.ic_bug_report)
                .onClick(item -> {
                    getGa().sendEvent("About", "Report a Bug");

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    // Recipient
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]
                            {"julien.guerinet@mail.mcgill.ca"});

                    // Title
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report));

                    // Content
                    String device = "Device: " + Device.model();
                    String sdkVersion = "SDK Version: " + Build.VERSION.SDK_INT;
                    String appVersion = "App Version: " + BuildConfig.VERSION_NAME;
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
                    return Unit.INSTANCE;
                })
                .build();
    }

    @HomepageManager.Homepage
    @Override
    protected int getCurrentPage() {
        return HomepageManager.SETTINGS;
    }
}