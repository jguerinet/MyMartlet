/*
 * Copyright 2014-2016 Julien Guerinet
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.formgenerator.TextViewFormItem;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;
import com.guerinet.utils.prefs.BooleanPreference;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.list.HomepageListAdapter;
import ca.appvelopers.mcgillmobile.ui.dialog.list.LanguageListAdapter;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.LanguageManager;
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
    protected LinearLayout container;
    /**
     * Statistics {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.STATS)
    protected BooleanPreference statsPrefs;

    /**
     * SCHEDULE_24HR {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.SCHEDULE_24HR)
    protected BooleanPreference twentyFourHourPrefs;
    /**
     * {@link UsernamePreference} instance
     */
    @Inject
    protected UsernamePreference usernamePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        setTitle(getString(R.string.settings_version, Utils.versionName(this)));
        analytics.sendScreen("Settings");

        FormGenerator fg = FormGenerator.bind(this, container);
        final Context context = this;

        //Language
        fg.text(languageManager.getString())
                .leftIcon(R.drawable.ic_language)
                .onClick(new TextViewFormItem.OnClickListener() {
                    @Override
                    public void onClick(TextViewFormItem item) {
                        DialogUtils.list(context, R.string.settings_language,
                                new LanguageListAdapter(SettingsActivity.this) {
                                    @Override
                                    public void onLanguageSelected(
                                            @LanguageManager.Language int language) {
                                        //Don't continue if it's the selected language
                                        if (language == languageManager.get()) {
                                            return;
                                        }

                                        languageManager.set(language);

                                        analytics.sendEvent("Settings", "Language",
                                                languageManager.getCode());

                                        //Reload this activity
                                        startActivity(new Intent(context, SettingsActivity.class));
                                        finish();
                                    }
                                });
                    }
                })
                .build();

        //24hrSchedule
        fg.aSwitch(R.string.settings_twentyfourhours)
                .leftIcon(R.drawable.ic_clock)
                .checked(twentyFourHourPrefs.get())
                .onCheckChanged(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        twentyFourHourPrefs.set(isChecked);
                    }
                })
                .build();

        //HomepageManager
        fg.text(homepageManager.getTitleString())
                .leftIcon(R.drawable.ic_phone_android)
                .onClick(new TextViewFormItem.OnClickListener() {
                    @Override
                    public void onClick(final TextViewFormItem item) {
                        DialogUtils.list(context, R.string.settings_homepage_title,
                                new HomepageListAdapter(SettingsActivity.this) {
                                    @Override
                                    public void onHomepageSelected(
                                            @HomepageManager.Homepage int choice) {
                                        //Update the instance
                                        homepageManager.set(choice);

                                        analytics.sendEvent("Settings", "HomepageManager",
                                                homepageManager.getString());

                                        //Update the TextView
                                        item.view().setText(homepageManager.getTitleString());
                                    }
                                });
                    }
                })
                .build();

        //Statistics
        fg.aSwitch(R.string.settings_statistics)
                .leftIcon(R.drawable.ic_trending_up)
                .checked(statsPrefs.get())
                .onCheckChanged(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        statsPrefs.set(isChecked);
                    }
                })
                .build();

        //Help
        fg.text(R.string.title_help)
                .leftIcon(R.drawable.ic_help)
                .onClick(new TextViewFormItem.OnClickListener() {
                    @Override
                    public void onClick(TextViewFormItem item) {
                        startActivity(new Intent(context, HelpActivity.class));
                    }
                })
                .build();

        //About
        fg.text(R.string.title_about)
                .leftIcon(R.drawable.ic_info)
                .onClick(new TextViewFormItem.OnClickListener() {
                    @Override
                    public void onClick(TextViewFormItem item) {
                        startActivity(new Intent(context, AboutActivity.class));
                    }
                })
                .build();

        //Bug Report
        fg.text(R.string.title_report_bug)
                .leftIcon(R.drawable.ic_bug_report)
                .onClick(new TextViewFormItem.OnClickListener() {
                    @Override
                    public void onClick(TextViewFormItem item) {
                        analytics.sendEvent("About", "Report a Bug");

                        Intent email = new Intent(Intent.ACTION_SEND);

                        // Recipient
                        email.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{"julien.guerinet@mail.mcgill.ca"});

                        // Title
                        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report));

                        // Content
                        String device = "Device: " + Utils.device();
                        String sdkVersion = "SDK Version: " + Build.VERSION.SDK_INT;
                        String appVersion = "App Version: " +
                                Utils.versionName(SettingsActivity.this);
                        String language = "Language: " + languageManager.getCode();

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
                        email.putExtra(Intent.EXTRA_TEXT, content);

                        // Log everything before printing the logs so it's included
                        Timber.i(device);
                        Timber.i(sdkVersion);
                        Timber.i(appVersion);
                        Timber.i(language);
                        Timber.i(connection);

                        // Logs (attachment)
                        try {
                            File file = new File(getExternalFilesDir(null), "logs.txt");
                            Utils.getLogs(file);
                            email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        } catch (IOException e) {
                            Timber.e(new Exception("Error getting logs", e));
                        }

                        // Code (Email)
                        email.setType("message/rfc822");
                        startActivity(Intent.createChooser(email, null));
                    }
                })
                .build();
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.SETTINGS;
    }
}