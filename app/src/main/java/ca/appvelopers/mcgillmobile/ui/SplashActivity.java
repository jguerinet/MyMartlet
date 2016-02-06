/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.BooleanPreference;
import com.guerinet.utils.prefs.IntPreference;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.ConnectionStatus;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.settings.AgreementActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.util.Update;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.storage.Clear;
import ca.appvelopers.mcgillmobile.util.thread.ConfigDownloader;

/**
 * First activity that is opened when the app is started
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SplashActivity extends BaseActivity {
    /**
     * Code used when starting the AgreementActivity
     */
    private static final int AGREEMENT_CODE = 100;
    /**
     * Hide loading {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.HIDE_LOADING)
    protected BooleanPreference hideLoadingPref;
    /**
     * Remember username {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.REMEMBER_USERNAME)
    protected BooleanPreference rememberUsernamePref;
    /**
     * Version {@link IntPreference}
     */
    @Inject
    @Named(PrefsModule.VERSION)
    protected IntPreference versionPref;
    /**
     * EULA {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.EULA)
    protected BooleanPreference eulaPref;
    /**
     * {@link UsernamePreference} instance
     */
    @Inject
    protected UsernamePreference usernamePref;
    /**
     * {@link PasswordPreference} instance
     */
    @Inject
    protected PasswordPreference passwordPref;
    /**
     * The {@link HomepageManager} instance
     */
    @Inject
    protected HomepageManager homepageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        App.component(this).inject(this);

        //Check if the user has accepted the user agreement
        if (eulaPref.get()) {
            //Run the config downloader if so
            runConfigDownloader();
        } else{
            //If not, show it
            Intent intent = new Intent(this, AgreementActivity.class)
                    .putExtra(Constants.EULA_REQUIRED, true);
            startActivityForResult(intent, AGREEMENT_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
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
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Runs the config downloader
     */
    private void runConfigDownloader(){
        new ConfigDownloader() {
            @Override
            public Void doInBackground(Void... params) {
                //Run the update code, if any
                Update.update(SplashActivity.this, versionPref);

                return super.doInBackground(params);
            }

            @Override
            protected void onPostExecute(Void param) {
                //Check if we have the minimum required version
                if(this.getMinVersion() > Utils.versionCode(SplashActivity.this)){
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

                //If one of them is null, show the login screen with no error message
                if(usernamePref.get() == null || passwordPref.get() == null){
                    showLoginScreen((ConnectionStatus)getIntent()
                            .getSerializableExtra(Constants.CONNECTION_STATUS));
                }
                //If not, try to log them in and download the info
                else{
                    new AppInitializer(false).execute();
                }
            }
        }.execute();
    }

    public void showLoginScreen(ConnectionStatus error){
        //Move the logo to the top
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        View logo = findViewById(R.id.logo);
        logo.setLayoutParams(params);

        //Show the login container
        final LinearLayout loginContainer = (LinearLayout)findViewById(R.id.login_container);
        loginContainer.setVisibility(View.VISIBLE);

        //Get the username before clearing everything
        String username = usernamePref.get();
        //Make sure to delete anything with the previous user's info
        Clear.all(rememberUsernamePref, usernamePref, passwordPref, homepageManager);

        Analytics.get().sendScreen("Login");

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
        rememberUsernameView.setChecked(rememberUsernamePref.get());

        //Check if an error message needs to be displayed, display it if so
        if (error != null) {
            DialogHelper.error(this, error.getErrorStringId());
        }

        //Fill out username text if it is present
        if(username != null){
            usernameView.setText(username);
        }

        //Set up the OnClickListener for the login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide the keyboard
                usernameView.clearFocus();
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(usernameView.getWindowToken(), 0);

                //Get the inputted username and password
                final String username = usernameView.getText().toString().trim();
                final String password = passwordView.getText().toString().trim();

                //Check that both of them are not empty, create appropriate error messages if so
                if (TextUtils.isEmpty(username)) {
                    DialogHelper.error(SplashActivity.this, R.string.login_error_username_empty);
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    DialogHelper.error(SplashActivity.this, R.string.login_error_password_empty);
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Set the username and password
                        Connection.getInstance().setUsername(
                                username + getString(R.string.login_email));
                        Connection.getInstance().setPassword(password);
                        final ConnectionStatus status = Connection.getInstance().login();
                        // If the connection was successful, start the app initializer
                        if (status == ConnectionStatus.OK) {
                            // Store the login info.
                            usernamePref.set(username);
                            passwordPref.set(password);
                            rememberUsernamePref.set(rememberUsernameView.isChecked());
                            Analytics.get().sendEvent("Login", "Remember Username",
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
                                    new AppInitializer(true).execute();
                                }
                            });
                        }
                        //Else show error dialog
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Analytics.get().sendEvent("Login", "Login Error",
                                            status.getGAString());
                                    progressDialog.dismiss();
                                    DialogHelper.error(SplashActivity.this,
                                            status.getErrorStringId());
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    /**
     * Initializes the app by logging the user in and downloading the required information
     */
    public class AppInitializer extends AsyncTask<Void, String, Void>{
        /**
         * True if the user has already logged in (first open), false otherwise
         */
        private boolean mLoggedIn;
        /**
         * The connection status
         */
        private ConnectionStatus mConnectionStatus;
        /**
         * True if we need to download everything (first open), false otherwise
         */
        private boolean mDownloadEverything;
        /**
         * The loading container
         */
        private LinearLayout mLoadingContainer;
        /**
         * The TextView showing the progress
         */
        private TextView mProgressTextView;
        /**
         * The skip button
         */
        private Button mSkip;
        /**
         * True if a bug was found, false otherwise
         */
        private boolean mBugPresent = false;
        /**
         * True if it was a transcript bug, false if it was a schedule bug
         */
        private boolean mTranscriptBug = false;
        /**
         * The term that the bug was found in, if any
         */
        private String mTermBug;

        //The passed boolean is true when they sign in for the first time,
        //  false when it's on auto-login.

        /**
         * Default Constructor
         *
         * @param loggedIn True if the user has already logged in (first open), false otherwise
         */
        public AppInitializer(boolean loggedIn){
            this.mLoggedIn = loggedIn;
        }

        @Override
        protected void onPreExecute(){
            //Check if we need to download everything or only the essential stuff
            //We need to download everything if there is null info or if we are forcing a reload
            mDownloadEverything = App.getTranscript() == null || App.getUser() == null ||
                    App.getEbill() == null;

            //Loading Container
            mLoadingContainer = (LinearLayout)findViewById(R.id.loading_container);
            mLoadingContainer.setVisibility(View.VISIBLE);

            //Progress dialog
            mProgressTextView = (TextView)findViewById(R.id.loading_title);
            //Reset the text (if it was set during a previous login attempt
            mProgressTextView.setText("");

            //Skip button
            mSkip = (Button)findViewById(R.id.skip);
            //If we are not downloading everything, show the skip button
            if(!mDownloadEverything){
                mSkip.setVisibility(View.VISIBLE);
            }
            mSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    //If it's already been cancelled, no need to skip it again
                    if(isCancelled()){
                        return;
                    }

                    //If the user has checked the "Do Not Show" option previously, skip directly
                    if (hideLoadingPref.get()) {
                        publishNewProgress(getString(R.string.skipping));
                        cancel(false);
                        return;
                    }

                    //If no, show the explanation dialog. Inflate the layout, bind the checkbox
                    View layout = View.inflate(SplashActivity.this, R.layout.dialog_checkbox, null);
                    final CheckBox doNotShow = (CheckBox)layout.findViewById(R.id.skip);

                    //Set up the dialog
                    new AlertDialog.Builder(SplashActivity.this)
                            .setCancelable(false)
                            .setView(layout)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.skip_loading))
                            .setPositiveButton(getString(android.R.string.yes),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which){
                                            //Save the do not show option
                                            hideLoadingPref.set(doNotShow.isChecked());

                                            //Cancel the info downloader
                                            publishNewProgress(getString(R.string.skipping));
                                            cancel(false);

                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton(getString(android.R.string.no),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which){
                                            //Save the do not show option
                                            hideLoadingPref.set(doNotShow.isChecked());
                                            dialog.dismiss();
                                        }
                                    })
                            .create().show();
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            Analytics.get().sendEvent("Splash", "Auto-Login", "true");

            //The connection instance
            Connection connection = Connection.getInstance();

            //Set up a while loop to go through everything while checking if the user cancelled
            //  every time
            int downloadIndex = 0;
            downloadLoop: while(!isCancelled()){
                //Use a switch to figure out what to download next based on the index
                switch(downloadIndex){
                    //Log him in
                    case 0:
                        publishNewProgress(getString(R.string.logging_in));

                        //If he's already logged in, the connection is OK
                        mConnectionStatus = mLoggedIn ? ConnectionStatus.OK : connection.login();

                        //If we did not connect, break the loop now
                        if(mConnectionStatus != ConnectionStatus.OK){
                            break downloadLoop;
                        }
                        break;
                    //Transcript
                    case 1:
                        publishNewProgress(getString(mDownloadEverything ?
                                R.string.downloading_transcript : R.string.updating_transcript));

                        //Download the transcript
                        try{
                            String transcriptBug;
                            if(Test.LOCAL_TRANSCRIPT){
                                transcriptBug = Test.testTranscript();
                            }
                            else{
                                transcriptBug = Parser.parseTranscript(
                                        connection.get(Connection.TRANSCRIPT_URL));
                            }
                            //If there was an error, show it
                            if(transcriptBug != null){
                                reportBug(true, transcriptBug);
                            }

                        } catch(MinervaException e){
                            //Set the connection status and break the loop
                            mConnectionStatus = ConnectionStatus.WRONG_INFO;
                            break downloadLoop;
                        } catch(Exception e){
                            //IOException or no internet - continue in any case
                        }
                        break;
                    //Semesters
                    case 2:
                        String scheduleBug = null;

                        //Test mode : only one semester to do
                        if(Test.LOCAL_SCHEDULE){
                            scheduleBug = Test.testSchedule();
                        }
                        else {
                            //List of semesters
                            List<Semester> semesters = App.getTranscript().getSemesters();
                            //The current term
                            Term currentTerm = Term.getCurrentTerm();

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
                                if(mDownloadEverything || term.equals(currentTerm) ||
                                        term.isAfter(currentTerm)){
                                    publishNewProgress(getString(mDownloadEverything ?
                                                    R.string.downloading_semester :
                                                    R.string.updating_semester,
                                            term.getString(SplashActivity.this)));

                                    //Download the schedule
                                    try{
                                        scheduleBug = Parser.parseCourses(term,
                                                connection.get(Connection.getScheduleURL(term)));
                                    } catch(MinervaException e){
                                        //Set the connection status and break the loop
                                        mConnectionStatus = ConnectionStatus.WRONG_INFO;
                                        break downloadLoop;
                                    } catch(Exception e){
                                        //IOException or no internet - continue in any case
                                    }
                                }
                            }

                            //Set the default term if there is none set yet
                            if(App.getDefaultTerm() == null){
                                App.setDefaultTerm(currentTerm);
                            }
                        }

                        //If there was an error, show it
                        if(scheduleBug != null){
                            reportBug(false, scheduleBug);
                        }
                        break;
                    //Ebill + user info
                    case 3:
                        //Ebill
                        publishNewProgress(getString(mDownloadEverything ?
                                R.string.downloading_ebill : R.string.updating_ebill));

                        //Download the eBill and user info
                        try{
                            String ebillString = connection.get(Connection.EBILL_URL);
                            Parser.parseEbill(ebillString);
                        } catch(MinervaException e){
                            //Set the connection status and break the loop
                            mConnectionStatus = ConnectionStatus.WRONG_INFO;
                            break downloadLoop;
                        } catch(Exception e){
                            //IOException or no internet - continue in any case
                        }
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

        @Override
        protected void onPostExecute(Void result) {
            //Hide the container
            mLoadingContainer.setVisibility(View.GONE);
            //Hide the skip button
            mSkip.setVisibility(View.GONE);

            //Connection successful: home page
            if(mConnectionStatus == ConnectionStatus.OK ||
                    mConnectionStatus == ConnectionStatus.NO_INTERNET){

                Intent intent = new Intent(SplashActivity.this, homepageManager.getActivity());
                //If there's a bug, add it to the intent
                if(mBugPresent){
                    intent.putExtra(Constants.BUG, mTranscriptBug ? Constants.TRANSCRIPT : "")
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

        /**
         * Reports a bug in the parsing of the transcript or schedule
         *
         * @param transcript True if it was the transcript, false otherwise
         * @param term       The term that the bug was in, if applicable
         */
        private void reportBug(boolean transcript, String term){
            mBugPresent = true;
            mTranscriptBug = transcript;
            mTermBug = term;
        }
    }

}