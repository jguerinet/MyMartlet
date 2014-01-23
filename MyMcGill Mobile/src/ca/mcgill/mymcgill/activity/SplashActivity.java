package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ca.mcgill.mymcgill.R;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class SplashActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //TODO TEMPORARY
        startActivity(new Intent(this, LoginActivity.class));

    }
}