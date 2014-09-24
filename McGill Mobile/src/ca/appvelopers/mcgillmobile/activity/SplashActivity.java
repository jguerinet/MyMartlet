package ca.appvelopers.mcgillmobile.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.dialog.SkipDialog;
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
    private InfoDownloader mInfoDownloader;

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
            mInfoDownloader = new InfoDownloader(this);
            mInfoDownloader.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Only show the skip option if we already have data in the app
        if(App.getClasses() != null && App.getTranscript() != null && App.getUserInfo() != null &&
                App.getEbill() != null && !App.forceUserReload){
            getMenuInflater().inflate(R.menu.skip, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //If they choose skip, show them the dialog
        if(item.getItemId() == R.id.action_skip){
            final SkipDialog skipDialog = new SkipDialog(this);
            skipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if(skipDialog.skip()){
                        mInfoDownloader.cancel(true);
                    }
                }
            });
            if(!skipDialog.show()){
                mInfoDownloader.cancel(true);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class InfoDownloader extends AsyncTask<Void, String, Void>{
        private Context mContext;
        private TextView mProgressTextView;
        private ProgressBar mProgressBar;
        private ConnectionStatus mConnectionStatus;

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
        protected Void doInBackground(Void... params) {
            GoogleAnalytics.sendEvent(SplashActivity.this, "Splash", "Auto-Login", "true", null);

            //Connect to Minerva
            mConnectionStatus = Connection.getInstance().connectToMinerva(mContext);

            //If we successfully connect
            if(mConnectionStatus == ConnectionStatus.CONNECTION_OK){
                //set the background receiver after successful login
//                        if(!App.isAlarmActive()){
//                        	App.SetAlarm(SplashActivity.this);
//                        }

                //Check if there is all the info or if we need to force reload everything
                if(App.getClasses() == null || App.getTranscript() == null || App.getUserInfo() == null ||
                        App.getEbill() == null || App.forceUserReload){
                    //If there isn't, Update everything
                    Connection.getInstance().downloadAll(mContext, this);
                }
                else{
                    //If there is, update the essentials
                    Connection.getInstance().downloadEssential(mContext, this);
                }
            }

            return null;
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
        protected void onCancelled(){
            onPostExecute(null);
        }

        @Override
        protected void onPostExecute(Void result) {
            //Hide the text
            mProgressTextView.setVisibility(View.INVISIBLE);
            //Hide the progress bar
            mProgressBar.setVisibility(View.INVISIBLE);

            //Connection successful: home page
            if(mConnectionStatus == ConnectionStatus.CONNECTION_OK ||
                    mConnectionStatus == ConnectionStatus.CONNECTION_NO_INTERNET){
                startActivity(new Intent(mContext, App.getHomePage().getHomePageClass()));
                finish();
            }
            //Connection not successful : login page
            else{
                Clear.clearAllInfo(mContext);
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra(Constants.CONNECTION_STATUS, mConnectionStatus);
                startActivity(intent);
                finish();
            }
        }
    }

}