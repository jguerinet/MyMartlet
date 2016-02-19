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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.BooleanPreference;
import com.guerinet.utils.prefs.IntPreference;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.ConnectionStatus;
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.settings.AgreementActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Update;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
import ca.appvelopers.mcgillmobile.util.storage.Clear;
import ca.appvelopers.mcgillmobile.util.thread.ConfigDownloader;
import ca.appvelopers.mcgillmobile.util.thread.UserDownloader;
import dagger.Lazy;

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
     * Container if the min version is not satisfied
     */
    @Bind(R.id.version_container)
    protected LinearLayout minVersionContainer;
    /**
     * Container with the login views
     */
    @Bind(R.id.login_container)
    protected LinearLayout loginContainer;
    /**
     * Container with the loading progress bar (when signing in or loading info for the first time)
     */
    @Bind(R.id.progress_container)
    protected LinearLayout progressContainer;
    /**
     * The login {@link Button}
     */
    @Bind(R.id.login_button)
    protected Button loginButton;
    /**
     * {@link EditText} where the user enters their username
     */
    @Bind(R.id.login_username)
    protected EditText usernameView;
    /**
     * {@link EditText} where the user enters their password
     */
    @Bind(R.id.login_password)
    protected EditText passwordView;
    /**
     * {@link CheckBox} where the user decides if their username should be remembered
     */
    @Bind(R.id.login_remember_username)
    protected CheckBox rememberUsername;
    /**
     * Update text for when downloading everything
     */
    @Bind(R.id.progress_text)
    protected TextView progressText;
    /**
     * The {@link McGillManager} instance
     */
    @Inject
    protected McGillManager mcGillManager;
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
     * Min version {@link IntPreference}
     */
    @Inject
    @Named(PrefsModule.MIN_VERSION)
    protected IntPreference minVersionPref;
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
    /**
     * The {@link InputMethodManager}
     */
    @Inject
    protected Lazy<InputMethodManager> imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        App.component(this).inject(this);

        //Run the update code, if any
        Update.update(this, versionPref);

        //Start downloading the config
        new ConfigDownloader(this).start();

        if (!eulaPref.get()) {
            //If the user has not accepted the EULA, show it before continuing
            Intent intent = new Intent(this, AgreementActivity.class)
                    .putExtra(PrefsModule.EULA, true);
            startActivityForResult(intent, AGREEMENT_CODE);
        } else {
            //If they have, go to the next screen
            showNextScreen();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AGREEMENT_CODE:
                if (resultCode == RESULT_OK) {
                    //If they agreed, run the config downloader
                    showNextScreen();
                } else {
                    //If not, close the app
                    finish();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Shows the first screen to the user depending on their situation
     */
    private void showNextScreen() {
        if (minVersionPref.get() > Utils.versionCode(this)) {
            //If we don't have the min required version, show the right container
            minVersionContainer.setVisibility(View.VISIBLE);
        } else if (usernamePref.get() == null || passwordPref.get() == null) {
            //If we are missing some login info, show the login screen with no error message
            showLoginScreen((ConnectionStatus) getIntent()
                    .getSerializableExtra(Constants.CONNECTION_STATUS));
        } else {
            //Try logging the user in and download their info
            new AppInitializer(true).execute();
        }
    }

    /**
     * Shows the login screen and an eventual error message
     *
     * @param error The error to display, null if none
     */
    private void showLoginScreen(ConnectionStatus error) {
        //Show the login container
        loginContainer.setVisibility(View.VISIBLE);

        //Delete of the previous user's info
        Clear.all(rememberUsernamePref, usernamePref, passwordPref, homepageManager);

        //Fill out username text if it is present
        usernameView.setText(usernamePref.get());

        //Set it to that when clicking the IME Action button it tries to log you in directly
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    loginButton.performClick();
                    return true;
                }
                return false;
            }
        });

        //Remember Me box checked based on user's previous preference
        rememberUsername.setChecked(rememberUsernamePref.get());

        //Check if an error message needs to be displayed, display it if so
        if (error != null) {
            DialogHelper.error(this, error.getErrorStringId());
        }

        Analytics.get().sendScreen("Login");
    }

    /**
     * Called when the min version button is clicked
     */
    @OnClick(R.id.version_button)
    protected void downloadNewVersion() {
        //Redirect them to the Play Store
        Utils.openPlayStoreApp(this, getPackageName());
    }

    /**
     * Called when the login button is clicked
     */
    @OnClick(R.id.login_button)
    protected void login() {
        //Hide the keyboard
        usernameView.clearFocus();
        imm.get().hideSoftInputFromWindow(usernameView.getWindowToken(), 0);

        //Get the inputted username and password
        final String username = usernameView.getText().toString().trim();
        final String password = passwordView.getText().toString().trim();

        //Check that both of them are not empty, create appropriate error messages if so
        if (TextUtils.isEmpty(username)) {
            DialogHelper.error(this, R.string.login_error_username_empty);
            return;
        } else if (TextUtils.isEmpty(password)) {
            DialogHelper.error(this, R.string.login_error_password_empty);
            return;
        }

        progressContainer.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final ConnectionStatus status = mcGillManager
                        .login(username + getString(R.string.login_email), password);

                // If the connection was successful, start the app initializer
                if (status == ConnectionStatus.OK) {
                    // Store the login info.
                    usernamePref.set(username);
                    passwordPref.set(password);
                    rememberUsernamePref.set(rememberUsername.isChecked());

                    Analytics.get().sendEvent("Login", "Remember Username",
                            String.valueOf(rememberUsername.isChecked()));

                    //Dismiss the progress dialog
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginContainer.setVisibility(View.GONE);

                            //Start the downloading of information
                            new AppInitializer(false).execute();
                        }
                    });
                } else {
                    //Else show error dialog
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Analytics.get().sendEvent("Login", "Login Error", status.getGAString());
                            progressContainer.setVisibility(View.GONE);
                            DialogHelper.error(SplashActivity.this, status.getErrorStringId());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Initializes the app by logging the user in and downloading the required information
     */
    class AppInitializer extends AsyncTask<Void, String, ConnectionStatus> {
        /**
         * True if we should auto-login the user, false if they have just logged in
         */
        private boolean autoLogin;

        /**
         * Default Constructor
         *
         * @param autoLogin True if we should auto-login the user, false if they have just logged in
         */
        public AppInitializer(boolean autoLogin) {
            this.autoLogin = autoLogin;
        }

        @Override
        protected void onPreExecute() {
            //Show the progress container
            progressContainer.setVisibility(View.VISIBLE);

            //Reset the text (if it was set during a previous login attempt
            progressText.setText("");
        }

        @Override
        protected ConnectionStatus doInBackground(Void... params) {
            Analytics.get().sendEvent("Splash", "Auto-Login", Boolean.toString(autoLogin));

            //If they're already logged in, the connection is OK
            ConnectionStatus status =  autoLogin ? ConnectionStatus.OK : mcGillManager.login();

            //Check if we need to download everything or only the essential stuff
            //We need to download everything if there is null info
            boolean downloadEverything = App.getTranscript() == null || App.getUser() == null ||
                    App.getEbill() == null;

            //If we did not connect, stop now
            if (status != ConnectionStatus.OK && status != ConnectionStatus.NO_INTERNET) {
                return status;
            }

            //If we need to download everything, do it synchronously. If not, do it asynchronously
            UserDownloader userDownloader = new UserDownloader(SplashActivity.this) {
                @Override
                public void update(String section) {
                    publishProgress(section);
                }
            };

            if (downloadEverything) {
                userDownloader.execute();
            } else {
                userDownloader.start();
            }

            return status;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            //Update the TextView
            progressText.setText(progress[0]);
        }

        @Override
        protected void onPostExecute(ConnectionStatus status) {
            //Hide the container
            progressContainer.setVisibility(View.GONE);

            if (status == ConnectionStatus.OK || status == ConnectionStatus.NO_INTERNET) {
                //Connection successful: home page
                startActivity(new Intent(SplashActivity.this, homepageManager.getActivity()));
                finish();
            } else {
                //Connection not successful: login
                showLoginScreen(status);
            }
        }
    }
}