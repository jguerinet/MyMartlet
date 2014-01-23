package ca.mcgill.mymcgill.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

import ca.mcgill.mymcgill.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //This method is called when the desktop button is clicked
    public void viewDesktopSite(View v){

    }

    //This method is called when the schedule button is clicked
    public void viewSchedule(View v){

    }

    //This method is called when the logout button is clicked
    public void logout(View v){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
