package ca.appvelopers.mcgillmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.HomePage;
import ca.appvelopers.mcgillmobile.object.Language;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.view.HomePageAdapter;

public class SettingsActivity extends DrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Settings");

        //Set up the info
        Spinner languages = (Spinner)findViewById(R.id.settings_language);
        //Set up the array of languages
        List<String> languageStrings = new ArrayList<String>();
        languageStrings.add(getResources().getString(R.string.english));
        languageStrings.add(getResources().getString(R.string.french));
        Collections.sort(languageStrings);
        //Standard ArrayAdapter
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_dropdown, languageStrings);
        //Apply the adapter to the spinner
        languages.setAdapter(languageAdapter);
        //Set the default selected to the user's chosen language
        languages.setSelection(App.getLanguage().ordinal());
        languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen language
                Language chosenLanguage = Language.values()[position];

                GoogleAnalytics.sendEvent(SettingsActivity.this, "Settings", "Language", chosenLanguage.getLanguageString(), null);

                //If it's different than the previously selected language, update it and reload
                if(App.getLanguage() != chosenLanguage){
                    App.setLanguage(chosenLanguage);
                    startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        Spinner homepages = (Spinner)findViewById(R.id.settings_homepage);
        final HomePageAdapter homePageAdapter = new HomePageAdapter(this);
        homepages.setAdapter(homePageAdapter);
        homepages.setSelection(App.getHomePage().ordinal());
        homepages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen homepage
                HomePage chosenHomePage = homePageAdapter.getItem(position);

                GoogleAnalytics.sendEvent(SettingsActivity.this, "Settings", "Homepage", chosenHomePage.toString(), null);

                //Update it in App
                App.setHomePage(chosenHomePage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Statistics
        CheckBox statistics = (CheckBox)findViewById(R.id.settings_statistics);
        statistics.setChecked(Load.loadStatistics(this));
        statistics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Save.saveStatistics(SettingsActivity.this, b);
            }
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SettingsActivity.this, App.getHomePage().getHomePageClass()));
        super.onBackPressed();
    }

}
