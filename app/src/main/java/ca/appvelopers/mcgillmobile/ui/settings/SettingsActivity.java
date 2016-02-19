/*
 * Copyright 2014-2016 Appvelopers
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
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.formgenerator.TextViewFormItem;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;
import com.guerinet.utils.prefs.BooleanPreference;
import com.instabug.library.Instabug;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.list.HomepageListAdapter;
import ca.appvelopers.mcgillmobile.ui.dialog.list.LanguageListAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.LanguageManager;

/**
 * Allows the user to change the app settings
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SettingsActivity extends DrawerActivity {
    /**
     * The {@link FormGenerator} container
     */
    @Bind(R.id.container)
    protected LinearLayout container;
    /**
     * Statistics {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.STATISTICS)
    protected BooleanPreference statsPrefs;

    /**
     * SCHEDULE_24HR {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.SCHEDULE_24HR)
    protected BooleanPreference TwentyFourHourPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        setTitle(getString(R.string.settings_version, Utils.versionName(this)));
        Analytics.get().sendScreen("Settings");

        FormGenerator fg = FormGenerator.bind(this, container);
        final Context context = this;

        //Language
        fg.text(languageManager.getString())
                .leftIcon(R.drawable.ic_language)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

                                        Analytics.get().sendEvent("Settings", "Language",
                                                languageManager.getCode());

                                        //Reload this activity
                                        startActivity(new Intent(context, SettingsActivity.class));
                                        finish();
                                    }
                                });
                    }
                });

        //HomepageManager
        final TextViewFormItem homepageView = fg.text(homepageManager.getTitleString());
        homepageView.leftIcon(R.drawable.ic_phone_android)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.list(context, R.string.settings_homepage_title,
                                new HomepageListAdapter(SettingsActivity.this) {
                                    @Override
                                    public void onHomepageSelected(@HomepageManager.Homepage int choice) {
                                        //Update the instance
                                        homepageManager.set(choice);

                                        Analytics.get().sendEvent("Settings", "HomepageManager",
                                                homepageManager.getString());

                                        //Update the TextView
                                        homepageView.view().setText(homepageManager.getTitleString());
                                    }
                                });
                    }
                });

        //Statistics
        fg.aSwitch(R.string.settings_statistics)
                .leftIcon(R.drawable.ic_trending_up)
                .checked(statsPrefs.get())
                .onCheckChanged(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        statsPrefs.set(isChecked);
                    }
                });

        //Help
        fg.text(R.string.title_help)
                .leftIcon(R.drawable.ic_help)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, HelpActivity.class));
                    }
                });

        //About
        fg.text(R.string.title_about)
                .leftIcon(R.drawable.ic_info)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, AboutActivity.class));
                    }
                });

        //Bug Report
        fg.text(R.string.title_report_bug)
                .leftIcon(R.drawable.ic_bug_report)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Analytics.get().sendEvent("About", "Report a Bug", null);
                        Instabug.getInstance().invokeFeedbackSender();
                    }
                });

        //24hrSchedule
        fg.aSwitch(R.string.settings_twentyfourhours)
                .leftIcon(R.drawable.ic_clock)
                .checked(TwentyFourHourPrefs.get())
                .onCheckChanged(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        TwentyFourHourPrefs.set(isChecked);
                    }
                });
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.SETTINGS;
    }
}