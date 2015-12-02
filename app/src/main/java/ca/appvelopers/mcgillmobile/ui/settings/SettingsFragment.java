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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
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
 * @version 2.0.1
 * @since 1.0.0
 */
public class SettingsFragment extends BaseFragment {
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

        //Language
        List<String> languages = new ArrayList<>();
        languages.add(Language.getString(Language.ENGLISH));
        languages.add(Language.getString(Language.FRENCH));
        Collections.sort(languages);
        ArrayAdapter<String> languageAdapter =
                new ArrayAdapter<>(mActivity, R.layout.spinner_item, languages);
        languageAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        mLanguageSpinner.setAdapter(languageAdapter);
        mLanguageSpinner.setSelection(App.getLanguage());

        //Homepage
        mHomepageAdapter = new HomepageAdapter();
        mHomepageSpinner.setAdapter(mHomepageAdapter);
        mHomepageSpinner.setSelection(mHomepageAdapter.getPosition(App.getHomepage()));

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
        @Language.Type int chosenLanguage = position;

        Analytics.getInstance().sendEvent("Settings", "Language", Language.getCode(position));

        //If it's different than the previously selected language, update it and reload
        if(App.getLanguage() != chosenLanguage){
            App.setLanguage(chosenLanguage);

            //Reload MainActivity
            Intent intent = new Intent(mActivity, MainActivity.class)
                    .putExtra(Constants.HOMEPAGE, Homepage.SETTINGS);
            startActivity(intent);
            mActivity.finish();
        }
    }

    @OnItemSelected(R.id.settings_homepage)
    public void chooseHomepage(int position){
        //Get the chosen homepage
        Homepage chosenHomePage = mHomepageAdapter.getItem(position);

        Analytics.getInstance().sendEvent("Settings", "Homepage", chosenHomePage.toString());

        //Update it in App
        App.setHomepage(chosenHomePage);
    }

    @OnCheckedChanged(R.id.settings_statistics)
    public void enableStatistics(boolean enabled){
        Save.statistics(enabled);
    }
}