package ca.mcgill.mymcgill.activity.settings;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerAdapter;

public class SettingsActivity extends DrawerActivity {
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
        mDrawerAdapter = new DrawerAdapter(this, DrawerAdapter.SETTINGS_POSITION);
        super.onCreate(savedInstanceState);
	    
	  SettingsFragment setFragment = new SettingsFragment();
	  FragmentManager fragmentManager = getFragmentManager();
	  FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	  fragmentTransaction.replace(android.R.id.content, setFragment);
	  fragmentTransaction.commit();
	 } 
}
