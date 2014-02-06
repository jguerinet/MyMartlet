package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;
import ca.mcgill.mymcgill.util.Clear;
import ca.mcgill.mymcgill.util.Load;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //This method is called when the desktop button is clicked
    public void viewDesktopSite(View v){
        startActivity(new Intent(this, DesktopActivity.class));
    }

    //This method is called when the schedule button is clicked
    public void viewSchedule(View v){
        startActivity(new Intent(this, ScheduleActivity.class));
    }

    //This method is called when the transcript button is clicked
    public void viewTranscript(View v){
        startActivity(new Intent(this, TranscriptActivity.class));
    }

    //This method is called when the logout button is clicked
    public void logout(View v){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Check if we need to remove the username
        if(!Load.loadRememberUsername(this)){
            Clear.clearUsername(this);
        }

        //Remove the stored password
        Clear.clearPassword(this);

        //Remove the stored info
        Clear.clearSchedule(this);
        Clear.clearTranscript(this);

        //Go back to the Login Activity
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
