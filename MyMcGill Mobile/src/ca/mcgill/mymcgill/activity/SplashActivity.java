package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class SplashActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get the SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Get the username and password stored
        String username = sharedPrefs.getString(Constants.USERNAME, null);
        String password = sharedPrefs.getString(Constants.PASSWORD, null);

        //If one of them is null, send the user to the LoginActivity
        //TODO: UNDO AUTOLOGINPAGE
        if(true || username == null || password == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }
}