package ca.appvelopers.mcgillmobile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        final String username = Load.loadFullUsername(this);
        final String password = Load.loadPassword(this);

        //If one of them is null, send the user to the LoginActivity
        if(username == null || password == null){
            //If we need to go back to the login, make sure to delete anything with the previous user's info
            Clear.clearAllInfo(this);
            GoogleAnalytics.sendEvent(this, "Splash", "Auto-Login", "false", null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        //If not, try to log him in, and send him to the LoginActivity if there's a problem
        else{
            new InfoDownloader(this).execute();
        }
    }

    public class InfoDownloader extends AsyncTask<Void, String, ConnectionStatus>{
        private Context mContext;
        private TextView mProgressTextView;
        private ProgressBar mProgressBar;

        public InfoDownloader(Context context){
            this.mContext = context;
        }

        @Override
        protected void onPreExecute(){
            mProgressTextView = (TextView)findViewById(R.id.loading_title);

            //Set the progress bar to visible
            mProgressBar = (ProgressBar)findViewById(R.id.loading_progress);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ConnectionStatus doInBackground(Void... params) {
            GoogleAnalytics.sendEvent(SplashActivity.this, "Splash", "Auto-Login", "true", null);

            //Connect to Minerva
            ConnectionStatus connectionStatus = Connection.getInstance().connectToMinerva(mContext);

            //If we successfully connect
            if(connectionStatus == ConnectionStatus.CONNECTION_OK){
                //set the background receiver after successful login
//                        if(!App.isAlarmActive()){
//                        	App.SetAlarm(SplashActivity.this);
//                        }

                //Update everything
                Connection.getInstance().downloadAll(mContext, this);
            }

            return connectionStatus;
        }

        public void publishNewProgress(String title){
            publishProgress(title);
        }

        @Override
        protected void onProgressUpdate(String... progress){
            //Update the TextView
            mProgressTextView.setText(progress[0]);
        }

        @Override
        protected void onPostExecute(ConnectionStatus connectionStatus) {
            //Hide the progress bar
            mProgressBar.setVisibility(View.INVISIBLE);

            //Connection successful: home page
            if(connectionStatus == ConnectionStatus.CONNECTION_OK ||
                    connectionStatus == ConnectionStatus.CONNECTION_NO_INTERNET){
                startActivity(new Intent(mContext, App.getHomePage().getHomePageClass()));
                finish();
            }
            //Connection not successful : login page
            else{
                Clear.clearAllInfo(mContext);
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra(Constants.CONNECTION_STATUS, connectionStatus);
                startActivity(intent);
                finish();
            }
        }
    }

}