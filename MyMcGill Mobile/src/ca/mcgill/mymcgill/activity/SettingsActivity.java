package ca.mcgill.mymcgill.activity;

import android.os.Bundle;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerAdapter;

public class SettingsActivity extends DrawerActivity {
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        mDrawerAdapter = new DrawerAdapter(this, DrawerAdapter.SETTINGS_POSITION);
        super.onCreate(savedInstanceState);

	 } 
}
