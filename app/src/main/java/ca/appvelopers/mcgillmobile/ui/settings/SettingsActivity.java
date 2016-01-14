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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.formgenerator.TextViewFormItem;
import com.instabug.library.Instabug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Language;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;
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
        setTitle(getString(R.string.settings_version, Help.getVersionName()));
        Analytics.get().sendScreen("Settings");

        //Set up the FormGenerator
        FormGenerator fg = FormGenerator.get()
                .setDefaultIconColorId(R.color.red)
                .setDefaultBackground(R.drawable.transparent_redpressed)
                .setDefaultPaddingSize(R.dimen.padding_small)
                .bind(this, mContainer);

        //Language
        fg.text(Language.getString(App.getLanguage()))
                .leftIcon(R.drawable.ic_language)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Get the languages, display them alphabetically
                        List<String> languages = new ArrayList<>();
                        languages.add(Language.getString(Language.ENGLISH));
                        languages.add(Language.getString(Language.FRENCH));
                        Collections.sort(languages);

                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle(R.string.settings_language)
                                .setSingleChoiceItems(
                                        languages.toArray(new CharSequence[languages.size()]),
                                        App.getLanguage(), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Get the chosen language
                                                @Language.Type int language = which;

                                                Analytics.get().sendEvent("Settings", "Language",
                                                        Language.getCode(language));

                                                //If it's different than the previously selected
                                                //  language, update it and reload
                                                if (App.getLanguage() != language) {
                                                    App.setLanguage(language);

                                                    //Reload this activity
                                                    startActivity(new Intent(SettingsActivity.this,
                                                            SettingsActivity.class));
                                                    finish();
                                                }
                                            }
                                        })
                                .show();
                    }
                });

        //Homepage
        final TextViewFormItem homepage = fg.text(getString(R.string.settings_homepage,
                App.getHomepage().toString()));
        homepage.leftIcon(R.drawable.ic_phone_android)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Get the homepages and sort them alphabetically
                        final Homepage[] homepages = Homepage.values();
                        Arrays.sort(homepages, new Comparator<Homepage>() {
                            @Override
                            public int compare(Homepage a, Homepage b) {
                                return a.toString().compareToIgnoreCase(b.toString());
                            }
                        });

                        //Set up the titles and find the current homepage
                        CharSequence[] homepageTitles = new CharSequence[homepages.length];
                        int currentHomepage = -1;
                        for (int i = 0; i < homepages.length; i++) {
                            Homepage homepage = homepages[i];
                            homepageTitles[i] = homepage.toString();
                            //Set the current homepage
                            if (App.getHomepage() == homepage) {
                                currentHomepage = i;
                            }
                        }

                        new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle(R.string.settings_homepage_title)
                                .setSingleChoiceItems(homepageTitles, currentHomepage,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Get the chosen homepage
                                                Homepage newHomepage = homepages[which];

                                                Analytics.get().sendEvent("Settings",
                                                        "Homepage", newHomepage.toString());

                                                //Update it in App
                                                App.setHomepage(newHomepage);

                                                //Update the TextView
                                                homepage.view().setText(
                                                        getString(R.string.settings_homepage,
                                                                newHomepage.toString()));

                                                dialog.dismiss();
                                            }
                                        })
                                .show();
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
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SettingsActivity.this, HelpActivity.class));
                    }
                })
                .leftIcon(R.drawable.ic_help);

        //About
        fg.text(R.string.title_about)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
                    }
                })
                .leftIcon(R.drawable.ic_info);

        //Bug Report
        fg.text(R.string.title_report_bug)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Analytics.get().sendEvent("About", "Report a Bug", null);
                        Instabug.getInstance().invokeFeedbackSender();
                    }
                })
                .leftIcon(R.drawable.ic_bug_report);
    }
}