package ca.mcgill.mymcgill.activity.settings;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	    
	  SettingsFragment setFragment = new SettingsFragment();
	  FragmentManager fragmentManager = getFragmentManager();
	  FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	  fragmentTransaction.replace(android.R.id.content, setFragment);
	  fragmentTransaction.commit();
	 } 
}
