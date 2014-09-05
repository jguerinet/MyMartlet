package ca.appvelopers.mcgillmobile.activity;

import android.content.Intent;
import android.os.Bundle;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.object.ConnectionStatus;
import ca.appvelopers.mcgillmobile.util.Clear;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Load;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class SplashActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Get the username and password stored
        final String username = Load.loadUsername(this);
        final String password = Load.loadPassword(this);

        //If one of them is null, send the user to the LoginActivity
        if(username == null || password == null){
            //If we need to go back to the login, make sure to
            //delete anything with the previous user's info
            //Clear.clearAllInfo(this);
            GoogleAnalytics.sendEvent(this, "Splash", "Auto-Login", "false", null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //If not, try to log him in, and send him to the LoginActivity if there's a problem
        else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GoogleAnalytics.sendEvent(SplashActivity.this, "Splash", "Auto-Login", "true", null);
                    //Set the username and password
                    Connection.getInstance().setUsername(username + SplashActivity.this.getResources().getString(R.string.login_email));
                    Connection.getInstance().setPassword(password);
                    ConnectionStatus connectionResult = Connection.getInstance().connectToMinerva(SplashActivity.this);
                    
                    if(connectionResult == ConnectionStatus.CONNECTION_OK){
                    	//set the background reciever after successful login
                        if(!App.isAlarmActive()){
                        	App.SetAlarm(SplashActivity.this);
                        }
                    }
                    //Successful connection: ScheduleActivity
                    if(connectionResult == ConnectionStatus.CONNECTION_OK ||
                            connectionResult == ConnectionStatus.CONNECTION_NO_INTERNET){
                        //If anything is null, reload everything
                        if(App.getTranscript() == null || App.getClasses() == null || App.getEbill() == null
                                || App.getUserInfo() == null) {
                            Connection.getInstance().downloadAll(SplashActivity.this);
                        }
                        startActivity(new Intent(SplashActivity.this, App.getHomePage().getHomePageClass()));
                        finish();
                    }
                    else{
                        Clear.clearAllInfo(SplashActivity.this);
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        intent.putExtra(Constants.CONNECTION_STATUS, connectionResult);
                        startActivity(intent);
                        finish();
                    }
                }
            }).start();
        }
    }
}