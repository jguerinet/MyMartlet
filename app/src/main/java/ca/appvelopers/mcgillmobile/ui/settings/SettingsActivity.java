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
import com.guerinet.utils.Util;
import com.guerinet.utils.dialog.DialogHelper;
import com.instabug.library.Instabug;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Language;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.list.HomepageListAdapter;
import ca.appvelopers.mcgillmobile.ui.dialog.list.LanguageListAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.storage.Save;

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
    protected LinearLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setTitle(getString(R.string.settings_version, Util.versionName(this)));
        Analytics.get().sendScreen("Settings");

        FormGenerator fg = FormGenerator.bind(this, mContainer);
        final Context context = this;

        //Language
        fg.text(Language.getString(App.getLanguage()))
                .leftIcon(R.drawable.ic_language)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.list(context, R.string.settings_language,
                                new LanguageListAdapter() {
                                    @Override
                                    public void onLanguageSelected(@Language.Type int language) {
                                        Analytics.get().sendEvent("Settings", "Language",
                                                Language.getCode(language));

                                        //If it's different than the previously selected language,
                                        //  update it and reload
                                        if (App.getLanguage() != language) {
                                            App.setLanguage(language);

                                            //Reload this activity
                                            startActivity(
                                                    new Intent(context, SettingsActivity.class));
                                            finish();
                                        }
                                    }
                                });
                    }
                });

        //Homepage
        final TextViewFormItem homepageView = fg.text(Homepage.getTitleString(App.getHomepage()));
        homepageView.leftIcon(R.drawable.ic_phone_android)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogHelper.list(context, R.string.settings_homepage_title,
                                new HomepageListAdapter() {
                                    @Override
                                    public void onHomepageSelected(@Homepage.Type int homepage) {
                                        Analytics.get().sendEvent("Settings", "Homepage",
                                                Homepage.getString(homepage));

                                        //Update it in App
                                        App.setHomepage(homepage);

                                        //Update the TextView
                                        homepageView.view().setText(
                                                Homepage.getTitleString(homepage));
                                    }
                                });
                    }
                });

        //Statistics
        fg.aSwitch(R.string.settings_statistics)
                .leftIcon(R.drawable.ic_trending_up)
                .checked(Load.statistics())
                .onCheckChanged(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Save.statistics(isChecked);
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
    }

    @Override
    protected @Homepage.Type int getCurrentPage() {
        return Homepage.SETTINGS;
    }
}