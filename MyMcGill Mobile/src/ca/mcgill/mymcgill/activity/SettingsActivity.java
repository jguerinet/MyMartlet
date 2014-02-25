package ca.mcgill.mymcgill.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerAdapter;
import ca.mcgill.mymcgill.object.Language;
import ca.mcgill.mymcgill.util.ApplicationClass;

public class SettingsActivity extends DrawerActivity {
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        mDrawerAdapter = new DrawerAdapter(this, DrawerAdapter.SETTINGS_POSITION);
        super.onCreate(savedInstanceState);

        //Set up the info
        Spinner languages = (Spinner)findViewById(R.id.settings_language);
        //Standard ArrayAdapter
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
                R.array.settings_languages, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        languages.setAdapter(languageAdapter);
        //Set the default selected to the user's chosen language
        languages.setSelection(ApplicationClass.getLanguage().getLanguageInt());
        languages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen language
                Language chosenLanguage = Language.getLanguage(position);

                //If it's different than the previously selected language, update it and reload
                if(ApplicationClass.getLanguage() != chosenLanguage){
                    ApplicationClass.setLanguage(chosenLanguage);
                    startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        Spinner homepages = (Spinner)findViewById(R.id.settings_homepage);
        //Standard ArrayAdapter
        ArrayAdapter<CharSequence> homepageAdapter = ArrayAdapter.createFromResource(this,
                R.array.settings_homepages, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        homepageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        homepages.setAdapter(homepageAdapter);
    }
}
