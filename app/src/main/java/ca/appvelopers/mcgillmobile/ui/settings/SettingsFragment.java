/*
 * Copyright 2014-2015 Appvelopers
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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.instabug.library.Instabug;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.model.Language;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.main.MainActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.storage.Save;

/**
 * Allows the user to change the app settings
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class SettingsFragment extends BaseFragment {
    /**
     * The help page icon
     */
    @Bind(R.id.help_icon)
    TextView mHelpIcon;
    /**
     * The about page icon
     */
    @Bind(R.id.about_icon)
    TextView mAboutIcon;
    /**
     * The Report a Bug icon
     */
    @Bind(R.id.bug_icon)
    TextView mBugIcon;
    /**
     * The language spinner
     */
    @Bind(R.id.settings_language)
    Spinner mLanguageSpinner;
    /**
     * The homepage spinner
     */
    @Bind(R.id.settings_homepage)
    Spinner mHomepageSpinner;
    /**
     * The statistics switch
     */
    @Bind(R.id.settings_statistics)
    Switch mStatistics;
    /**
     * The version number
     */
    @Bind(R.id.settings_version)
    TextView mVersion;
    /**
     * The adapter used for the homepage spinner
     */
    private HomepageAdapter mHomepageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        lockPortraitMode();
        Analytics.getInstance().sendScreen("Settings");
        mActivity.setTitle(getString(R.string.title_settings));

        //Icons
        mHelpIcon.setTypeface(App.getIconFont());
        mAboutIcon.setTypeface(App.getIconFont());
        mBugIcon.setTypeface(App.getIconFont());

        //Language
        ArrayAdapter<String> languageAdapter =
                new ArrayAdapter<>(mActivity, R.layout.spinner_item, Language.getStrings());
        languageAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        mLanguageSpinner.setAdapter(languageAdapter);
        mLanguageSpinner.setSelection(App.getLanguage().ordinal());

        //Homepage
        mHomepageAdapter = new HomepageAdapter();
        mHomepageSpinner.setAdapter(mHomepageAdapter);
        mHomepageSpinner.setSelection(mHomepageAdapter.getPosition(App.getHomePage()));

        //Statistics
        mStatistics.setChecked(Load.statistics());

        //Version Number
        mVersion.setText(getString(R.string.settings_version, Help.getVersionName()));

        //Hide the loading indicator
        hideLoadingIndicator();

        return view;
    }

    @OnClick(R.id.settings_help)
    public void help(){
        startActivity(new Intent(mActivity, HelpActivity.class));
    }

    @OnClick(R.id.settings_about)
    public void about(){
        startActivity(new Intent(mActivity, AboutActivity.class));
    }

    @OnClick(R.id.settings_bug)
    public void reportBug(){
        Analytics.getInstance().sendEvent("About", "Report a Bug", null);
        Instabug.getInstance().invokeFeedbackSender();
    }

    @OnItemSelected(R.id.settings_language)
    public void chooseLanguage(int position){
        //Get the chosen language
        Language chosenLanguage = Language.values()[position];

        Analytics.getInstance().sendEvent("Settings", "Language", chosenLanguage.toString());

        //If it's different than the previously selected language, update it and reload
        if(App.getLanguage() != chosenLanguage){
            App.setLanguage(chosenLanguage);
            mActivity.updateLocale();

            //Reload MainActivity
            Intent intent = new Intent(mActivity, MainActivity.class)
                    .putExtra(Constants.HOMEPAGE, DrawerItem.SETTINGS);
            startActivity(intent);
            mActivity.finish();
        }
    }

    @OnItemSelected(R.id.settings_homepage)
    public void chooseHomepage(int position){
        //Get the chosen homepage
        DrawerItem chosenHomePage = mHomepageAdapter.getItem(position);

        Analytics.getInstance().sendEvent("Settings", "Homepage", chosenHomePage.toString());

        //Update it in App
        App.setHomePage(chosenHomePage);
    }

    @OnCheckedChanged(R.id.settings_statistics)
    public void enableStatistics(boolean enabled){
        Save.saveStatistics(mActivity, enabled);
    }
}