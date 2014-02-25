package ca.mcgill.mymcgill.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.ebill.EbillActivity;
import ca.mcgill.mymcgill.activity.inbox.InboxActivity;
import ca.mcgill.mymcgill.activity.settings.SettingsActivity;
import ca.mcgill.mymcgill.activity.transcript.TranscriptActivity;
import ca.mcgill.mymcgill.util.Clear;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
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

    //This method is called when the e-bill button is clicked
    public void viewEbill(View v){
        startActivity(new Intent(this, EbillActivity.class));
    }

    //This method is called when the email button is clicked
    public void viewInbox(View v){
        startActivity(new Intent(this, InboxActivity.class));
    }
    
    //This method is called when the settings button is clicked
    public void viewSettings(View v){
    	startActivity(new Intent(this, SettingsActivity.class));
    }

    //This method is called when the logout button is clicked
    public void logout(View v){
        Clear.clearAllInfo(this);

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
