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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.settings_languages, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        languages.setAdapter(adapter);
    }
}
