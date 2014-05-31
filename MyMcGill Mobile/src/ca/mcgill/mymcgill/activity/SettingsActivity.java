package ca.mcgill.mymcgill.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.object.Language;

public class SettingsActivity extends DrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);

        TextView languageTitle = (TextView)findViewById(R.id.settings_language_title);
        languageTitle.setText(getResources().getString(R.string.settings_language));

        //Set up the info
        Spinner languages = (Spinner)findViewById(R.id.settings_language);
        //Set up the array of languages
        List<String> languageStrings = new ArrayList<String>();
        languageStrings.add(getResources().getString(R.string.english));
        languageStrings.add(getResources().getString(R.string.french));
        Collections.sort(languageStrings);
        //Standard ArrayAdapter
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, languageStrings);
        //Specify the layout to use when the list of choices appears
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        languages.setAdapter(languageAdapter);
        //Set the default selected to the user's chosen language
        languages.setSelection(App.getLanguage().ordinal());
        languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen language
                Language chosenLanguage = Language.values()[position];

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

        //Standard ArrayAdapter
        ArrayAdapter<String> homepageAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, HomePage.getHomePageStrings(this));
        //Specify the layout to use when the list of choices appears
        homepageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        homepages.setAdapter(homepageAdapter);
        homepages.setSelection(App.getHomePage().ordinal());
        homepages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen language
                HomePage chosenHomePage = HomePage.values()[position];
                //Update it in the ApplicationClass
                    App.setHomePage(chosenHomePage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(SettingsActivity.this, App.getHomePage().getHomePageClass()));
        super.onBackPressed();
    }

}
