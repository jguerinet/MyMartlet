/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.BaseActivity;
import ca.appvelopers.mcgillmobile.activity.main.MainActivity;
import ca.appvelopers.mcgillmobile.dialog.SkipDialog;
import ca.appvelopers.mcgillmobile.object.ConnectionStatus;
import ca.appvelopers.mcgillmobile.object.Semester;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.thread.ConfigDownloader;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Clear;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien
 * Date: 22/01/14, 7:34 PM
 */
public class SplashActivity extends BaseActivity {
    private static final int AGREEMENT_CODE = 100;

    private InfoDownloader mInfoDownloader;

    private String mUsername;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Check if the user has accepted the user agreement
        if(Load.loadUserAgreement(this)){
            //Run the config downloader if so
            runConfigDownloader();
        }
        else{
            //If not, show it
            Intent intent = new Intent(this, AgreementActivity.class)
                    .putExtra(Constants.EULA_REQUIRED, true);
            startActivityForResult(intent, AGREEMENT_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AGREEMENT_CODE){
            //If they agreed, run the config downloader
            if(resultCode == RESULT_OK){
                runConfigDownloader();
            }
            //If not, close the app
            else if(resultCode == RESULT_CANCELED){
                finish();
            }
        }
    }

    private void runConfigDownloader(){
        new ConfigDownloader(this) {
            @Override
            protected void onPostExecute(Void param) {
                //Check if we have the minimum required version
                if(this.getMinVersion() > Help.getVersionNumber(SplashActivity.this)){
                    //If not, show the right container
                    LinearLayout minVersionContainer =
                            (LinearLayout) findViewById(R.id.version_container);
                    minVersionContainer.setVisibility(View.VISIBLE);

                    //Set up the button
                    Button versionButton = (Button) findViewById(R.id.version_button);
                    versionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Redirect them to the play store
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(Constants.PLAY_STORE_LINK));
                            startActivity(intent);
                        }
                    });

                    //Nothing else is done
                    return;
                }

                //Get the username and password stored
                mUsername = Load.loadUsername(SplashActivity.this);
                String password = Load.loadPassword(SplashActivity.this);

                //If one of them is null, show the login screen with no error message
                if(mUsername == null || password == null){
                    showLoginScreen((ConnectionStatus)getIntent().getSerializableExtra(Constants.CONNECTION_STATUS));
                }
                //If not, try to log him in and download the info
                else{
                    mInfoDownloader = new InfoDownloader(false);
                    mInfoDownloader.execute();
                }
            }
        }.execute();
    }

    public void showLoginScreen(ConnectionStatus error){
        //Move the logo to the top
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        View logo = findViewById(R.id.logo);
        logo.setLayoutParams(params);

        //Show the login container
        final LinearLayout loginContainer = (LinearLayout)findViewById(R.id.login_container);
        loginContainer.setVisibility(View.VISIBLE);

        //Make sure to delete anything with the previous user's info
        Clear.clearAllInfo(this);

        Analytics.getInstance().sendScreen("Login");

        //Get the necessary views
        final Button login = (Button) findViewById(R.id.login_button);
        final EditText usernameView = (EditText) findViewById(R.id.login_username);
        final EditText passwordView = (EditText) findViewById(R.id.login_password);
        //Set it to that when clicking the IME Action button it tries to log you in directly
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    login.performClick();
                    return true;
                }
                return false;
            }
        });

        final CheckBox rememberUsernameView = (CheckBox) findViewById(R.id.login_remember_username);
        //Remember Me box checked based on user's previous preference
        rememberUsernameView.setChecked(Load.loadRememberUsername(this));

        //Check if an error message needs to be displayed, display it if so
        if(error != null){
            DialogHelper.showNeutralAlertDialog(this, getString(R.string.error), error.getErrorString(this));
        }

        //Fill out username text if it is present
        if(mUsername != null){
            usernameView.setText(mUsername);
        }

        //Set up the OnClickListener for the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide the keyboard
                usernameView.clearFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(usernameView.getWindowToken(), 0);

                //Get the username text
                final String username = usernameView.getText().toString().trim();

                //Get the password text
                final String password = passwordView.getText().toString().trim();

                //Check that both of them are not empty, create appropriate error messages if so
                if (TextUtils.isEmpty(username)) {
                    DialogHelper.showNeutralAlertDialog(SplashActivity.this, getString(R.string.error),
                            getString(R.string.login_error_username_empty));
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    DialogHelper.showNeutralAlertDialog(SplashActivity.this, getString(R.string.error),
                            getString(R.string.login_error_password_empty));
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the username and password
                        Connection.getInstance().setUsername(username + getString(R.string.login_email));
                        Connection.getInstance().setPassword(password);
                        final ConnectionStatus connectionStatus = Connection.getInstance().connectToMinerva(SplashActivity.this);
                        // If the connection was successful, go to Homepage
                        if (connectionStatus == ConnectionStatus.OK) {
                            // Store the login info.
                            Save.saveUsername(SplashActivity.this, username);
                            Save.savePassword(SplashActivity.this, password);
                            Save.saveRememberUsername(SplashActivity.this, rememberUsernameView.isChecked());
                            Analytics.getInstance().sendEvent("Login", "Remember Username",
                                    String.valueOf(rememberUsernameView.isChecked()));

                            //set the background receiver after successful login
//                            if(!App.isAlarmActive()){
//                            	App.SetAlarm(LoginActivity.this);
//                            }

                            //Dismiss the progress dialog
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();

                                    //Hide the login container
                                    loginContainer.setVisibility(View.GONE);

                                    //Start the downloading of information
                                    mInfoDownloader = new InfoDownloader(true);
                                    mInfoDownloader.execute();
                                }
                            });
                        }
                        //Else show error dialog
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Analytics.getInstance().sendEvent("Login", "Login Error",
                                            connectionStatus.getGAString());
                                    progressDialog.dismiss();
                                    DialogHelper.showNeutralAlertDialog(SplashActivity.this, getString(R.string.error),
                                            connectionStatus.getErrorString(SplashActivity.this));
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    public class InfoDownloader extends AsyncTask<Void, String, Void>{
        private Context mContext;
        private boolean mLoggedIn;
        private ConnectionStatus mConnectionStatus;
        private boolean mDownloadEverything;

        private LinearLayout mLoadingContainer;
        private TextView mProgressTextView;
        private Button mSkip;

        //Bug Related info
        private boolean mBugPresent;
        private boolean mTranscriptBug;
        private String mTermBug;

        //The passed boolean is true when they sign in for the first time,
        //  false when it's on auto-login.
        public InfoDownloader(boolean loggedIn){
            this.mContext = SplashActivity.this;
            this.mLoggedIn = loggedIn;
            this.mBugPresent = false;
            this.mTranscriptBug = false;
        }

        @Override
        protected void onPreExecute(){
            //Bind the views
            mLoadingContainer = (LinearLayout)findViewById(R.id.loading_container);
            mProgressTextView = (TextView)findViewById(R.id.loading_title);
            mSkip = (Button)findViewById(R.id.skip);

            //Check if we need to download everything or only the essential stuff
            //We need to download everything if there is null info or if we are forcing a user reload
            mDownloadEverything = App.getClasses() == null || App.getTranscript() == null ||
                    App.getUserInfo() == null || App.getEbill() == null || App.forceUserReload;

            //OnClick listener for the skip button
            mSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //If it's already been cancelled, no need to do it again
                    if(mInfoDownloader.isCancelled()){
                        return;
                    }

                    //Show the explanation dialog
                    final SkipDialog skipDialog = new SkipDialog(SplashActivity.this);
                    skipDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            //If the user skips, cancel the info downloader
                            if(skipDialog.skip()){
                                mInfoDownloader.publishNewProgress(getString(R.string.skipping));
                                mInfoDownloader.cancel(false);
                            }
                        }
                    });
                    //If the dialog isn't showing, this means that the user has decided to never
                        //see the dialog again so cancel the info downloader
                    if(!skipDialog.show()){
                        mInfoDownloader.publishNewProgress(getString(R.string.skipping));
                        mInfoDownloader.cancel(false);
                    }
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Set the loading container to visible
                    mLoadingContainer.setVisibility(View.VISIBLE);

                    //Reset the text (if it was set during a previous login attempt
                    mProgressTextView.setText("");

                    //If we are not downloading everything, show the skip button
                    if(!mDownloadEverything){
                        mSkip.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            Analytics.getInstance().sendEvent("Splash", "Auto-Login", "true");

            //The connection
            Connection connection = Connection.getInstance();

            //Set up a while loop to go through everything while checking if the user cancelled every time
            int downloadIndex = 0;
            downloadLoop: while(!isCancelled()){
                //Use a switch to figure out what to download next based on the index
                switch(downloadIndex){
                    //Log him in
                    case 0:
                        publishNewProgress(getString(R.string.logging_in));

                        //If he's already logged in, the connection is OK
                        mConnectionStatus = mLoggedIn ? ConnectionStatus.OK :
                                connection.connectToMinerva(mContext);

                        //If we did not connect, break the loop now
                        if(mConnectionStatus != ConnectionStatus.OK){
                            break downloadLoop;
                        }
                        break;
                    //Transcript
                    case 1:
                        publishNewProgress(mContext.getString(mDownloadEverything ? R.string.downloading_transcript :
                                R.string.updating_transcript));

                        //Download the transcript
                        String transcriptBug = Test.LOCAL_TRANSCRIPT ? Test.testTranscript(mContext) :
                                Parser.parseTranscript(connection.getUrl(mContext, Connection.TRANSCRIPT));

                        //If there was an error, show it
                        if(transcriptBug != null){
                            reportBug(true, transcriptBug);
                        }
                        break;
                    //Semesters
                    case 2:
                        String scheduleBug = null;

                        //Test mode : only one semester to do
                        if(Test.LOCAL_SCHEDULE){
                            scheduleBug = Test.testSchedule(mContext);
                        }
                        else {
                            //List of semesters
                            List<Semester> semesters = App.getTranscript().getSemesters();
                            //The current term
                            Term currentTerm = Term.dateConverter(DateTime.now());

                            //Go through the semesters
                            for(Semester semester: semesters){
                                //If the AsyncTask was cancelled, stop everything
                                if (isCancelled()) {
                                    break downloadLoop;
                                }

                                //Get the term of this semester
                                Term term = semester.getTerm();

                                //If we are not downloading everything, only download it if it's the
                                //  current or future term
                                if(mDownloadEverything || term.equals(currentTerm) || term.isAfter(currentTerm)){
                                    publishNewProgress(mContext.getString(mDownloadEverything ?
                                            R.string.downloading_semester : R.string.updating_semester,
                                            term.toString(mContext)));

                                    //Download the schedule
                                    scheduleBug = Parser.parseClassList(term, connection.getUrl(mContext,
                                            Connection.getScheduleURL(term)));
                                }
                            }

                            //Set the default term if there is none set yet
                            if(App.getDefaultTerm() == null){
                                App.setDefaultTerm(Term.dateConverter(DateTime.now()));
                            }
                        }

                        //If there was an error, show it
                        if(scheduleBug != null){
                            reportBug(false, scheduleBug);
                        }
                        break;
                    //eBill + user info
                    case 3:
                        //eBill
                        publishNewProgress(mContext.getString(mDownloadEverything ? R.string.downloading_ebill :
                                R.string.updating_ebill));

                        //Download the eBill and user info
                        String ebillString = Connection.getInstance().getUrl(mContext, Connection.EBILL);
                        Parser.parseEbill(ebillString);

                        //User Info
                        publishNewProgress(mContext.getString(mDownloadEverything ? R.string.downloading_user :
                                R.string.updating_user));

                        Parser.parseUserInfo(ebillString);
                        break;
                    //We've reached the end, break the loop
                    default:
                        break downloadLoop;
                }

                //Increment the download index
                downloadIndex ++;
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

        public void reportBug(boolean transcript, String term){
            mBugPresent = true;
            mTranscriptBug = transcript;
            mTermBug = term;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Hide the container
            mLoadingContainer.setVisibility(View.GONE);

            //Hide th skip button
            mSkip.setVisibility(View.GONE);

            //Connection successful: home page
            if(mConnectionStatus == ConnectionStatus.OK ||
                    mConnectionStatus == ConnectionStatus.NO_INTERNET){

                Intent intent = new Intent(mContext, MainActivity.class);
                //If there's a bug, add it to the intent
                if(mBugPresent){
                    intent.putExtra(Constants.BUG, mTranscriptBug ? Constants.TRANSCRIPT : Constants.SCHEDULE)
                        .putExtra(Constants.TERM, mTermBug);
                }
                startActivity(intent);
                finish();
            }
            //Connection not successful : login
            else{
                showLoginScreen(mConnectionStatus);
            }
        }
    }

}