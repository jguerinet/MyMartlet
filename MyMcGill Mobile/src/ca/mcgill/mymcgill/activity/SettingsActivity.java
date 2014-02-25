package ca.mcgill.mymcgill.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerAdapter;

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
