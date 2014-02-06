package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.util.Clear;
import ca.mcgill.mymcgill.util.Load;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class SplashActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get the username and password stored
        String username = Load.loadUsername(this);
        String password = Load.loadPassword(this);

        //If one of them is null, send the user to the LoginActivity
//        if(!rememberMe || username == null || password == null){
            //If we need to go back to the login, make sure to
            //delete anything with the previous user's info
            Clear.clearSchedule(this);
            Clear.clearTranscript(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
//        }

    }
}