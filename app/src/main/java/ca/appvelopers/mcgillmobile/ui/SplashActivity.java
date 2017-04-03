/*
 * Copyright 2014-2017 Julien Guerinet
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
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.orhanobut.hawk.Hawk;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.settings.AgreementActivity;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.StatementsDB;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
import ca.appvelopers.mcgillmobile.util.manager.UpdateManager;
import ca.appvelopers.mcgillmobile.util.service.ConfigDownloadService;
import ca.appvelopers.mcgillmobile.util.storage.ClearManager;
import ca.appvelopers.mcgillmobile.util.thread.UserDownloader;
import dagger.Lazy;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @BindView(R.id.version_container)
    LinearLayout minVersionContainer;
    /**
     * Container with the login views
     */
    @BindView(R.id.login_container)
    LinearLayout loginContainer;
    /**
     * Container with the loading progress bar (when signing in or loading info for the first time)
     */
    @BindView(R.id.progress_container)
    LinearLayout progressContainer;
    /**
     * The login {@link Button}
     */
    @BindView(R.id.login_button)
    Button loginButton;
    /**
     * {@link EditText} where the user enters their username
     */
    @BindView(R.id.login_username)
    EditText usernameView;
    /**
     * {@link EditText} where the user enters their password
     */
    @BindView(R.id.login_password)
    EditText passwordView;
    /**
     * {@link CheckBox} where the user decides if their username should be remembered
     */
    @BindView(R.id.login_remember_username)
    CheckBox rememberUsername;
    /**
     * Update text for when downloading everything
     */
    @BindView(R.id.progress_text)
    TextView progressText;
    /**
     * The {@link McGillManager} instance
     */
    @Inject
    McGillManager mcGillManager;
    /**
     * {@link ClearManager} instance
     */
    @Inject
    ClearManager clearManager;
    /**
     * Remember username {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.REMEMBER_USERNAME)
    BooleanPreference rememberUsernamePref;
    /**
     * Min version {@link IntPreference}
     */
    @Inject
    @Named(PrefsModule.MIN_VERSION)
    IntPreference minVersionPref;
    /**
     * EULA {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.EULA)
    BooleanPreference eulaPref;
    /**
     * {@link UsernamePreference} instance
     */
    @Inject
    UsernamePreference usernamePref;
    /**
     * {@link UpdateManager} instance
     */
    @Inject
    UpdateManager updateManager;
    /**
     * The {@link HomepageManager} instance
     */
    @Inject
    HomepageManager homepageManager;
    /**
     * The {@link InputMethodManager}
     */
    @Inject
    Lazy<InputMethodManager> imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        App.component(this).inject(this);

        new AppInitializer().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AGREEMENT_CODE:
                if (resultCode == RESULT_OK) {
                    // If they agreed, go to the next screen
                    showNextScreen();
                } else {
                    // If not, close the app
                    finish();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onReceivedBroadcast(Intent intent) {
        // Override the Minerva broadcast to not log the user out since they are already logged out
        if (!intent.getAction().equals(Constants.BROADCAST_MINERVA)) {
            super.onReceivedBroadcast(intent);
        }
    }

    /**
     * Shows the first screen to the user depending on their situation
     */
    private void showNextScreen() {
        if (minVersionPref.get() > Utils.versionCode(this)) {
            // If we don't have the min required version, show the right container
            minVersionContainer.setVisibility(View.VISIBLE);
        } else if (usernamePref.get() == null || !Hawk.contains(PrefsModule.Hawk.PASSWORD)) {
            // If we are missing some login info, show the login screen with no error message
            showLoginScreen((IOException) getIntent().getSerializableExtra(Constants.EXCEPTION));
        } else {
            // Try logging the user in and download their info
            new AutoLogin(true).execute();
        }
    }

    /**
     * Shows the login screen and an eventual error message
     *
     * @param e The error received when trying to login, null if none
     */
    private void showLoginScreen(@Nullable IOException e) {
        // Show the login container
        loginContainer.setVisibility(View.VISIBLE);

        // Delete of the previous user's info
        clearManager.all();

        // Fill out username text if it is present
        usernameView.setText(usernamePref.get());

        // Set it to that when clicking the IME Action button it tries to log you in directly
        passwordView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                loginButton.performClick();
                return true;
            }
            return false;
        });

        // Remember Me box checked based on user's previous preference
        rememberUsername.setChecked(rememberUsernamePref.get());

        // Check if an error message needs to be displayed, display it if so
        if (e != null) {
            DialogHelper.error(this, (e instanceof MinervaException) ?
                    R.string.login_error_wrong_data : R.string.error_other);
        }

        analytics.sendScreen("Login");
    }

    /**
     * Called when the min version button is clicked
     */
    @OnClick(R.id.version_button)
    protected void downloadNewVersion() {
        // Redirect them to the Play Store
        Utils.openPlayStoreApp(this);
    }

    /**
     * Called when the login button is clicked
     */
    @OnClick(R.id.login_button)
    protected void login() {
        // Hide the keyboard
        usernameView.clearFocus();
        imm.get().hideSoftInputFromWindow(usernameView.getWindowToken(), 0);

        // Get the inputted username and password
        final String username = usernameView.getText().toString().trim();
        final String password = passwordView.getText().toString().trim();

        // Check that both of them are not empty, create appropriate error messages if so
        if (TextUtils.isEmpty(username)) {
            DialogHelper.error(this, R.string.login_error_username_empty);
            return;
        } else if (TextUtils.isEmpty(password)) {
            DialogHelper.error(this, R.string.login_error_password_empty);
            return;
        } else if (!Utils.isConnected(this)) {
            DialogHelper.error(this, R.string.error_no_internet);
            return;
        }

        progressContainer.setVisibility(View.VISIBLE);

        mcGillManager.login(username + getString(R.string.login_email), password,
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                            Response<ResponseBody> response) {
                        // Store the login info
                        usernamePref.set(username);
                        Hawk.put(PrefsModule.Hawk.PASSWORD, password);
                        rememberUsernamePref.set(rememberUsername.isChecked());

                        analytics.sendEvent("Login", "Remember Username",
                                String.valueOf(rememberUsername.isChecked()));

                        handler.post(() -> {
                            // Hide the login container
                            loginContainer.setVisibility(View.GONE);

                            // Start the downloading of information
                            new AutoLogin(false).execute();
                        });
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        int error = t instanceof MinervaException ?
                                R.string.login_error_wrong_data : R.string.error_other;

                        handler.post(() -> {
                            // If for some reason the activity is finishing, don't show this
                            if (isFinishing()) {
                                return;
                            }
                            progressContainer.setVisibility(View.GONE);
                            DialogHelper.error(SplashActivity.this, error);
                        });
                    }
                });
    }

    /**
     * Initializes the app by starting the config download and running through update code
     */
    private class AppInitializer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Run any update code
            updateManager.update();

            // Start downloading the config
            startService(new Intent(SplashActivity.this, ConfigDownloadService.class));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!eulaPref.get()) {
                // If the user has not accepted the EULA, show it before continuing
                Intent intent = new Intent(SplashActivity.this, AgreementActivity.class)
                        .putExtra(PrefsModule.EULA, true);
                startActivityForResult(intent, AGREEMENT_CODE);
            } else {
                // If they have, go to the next screen
                showNextScreen();
            }
        }
    }

    /**
     * Initializes the app by logging the user in and downloading the required information
     */
    private class AutoLogin extends AsyncTask<Void, String, IOException> {
        /**
         * True if we should auto-login the user, false if they have just logged in
         */
        private boolean autoLogin;

        /**
         * Default Constructor
         *
         * @param autoLogin True if we should auto-login the user, false if they have just logged in
         */
        private AutoLogin(boolean autoLogin) {
            this.autoLogin = autoLogin;
        }

        @Override
        protected void onPreExecute() {
            // Show the progress container
            progressContainer.setVisibility(View.VISIBLE);

            // Reset the text (if it was set during a previous login attempt)
            progressText.setText("");
        }

        @Override
        protected IOException doInBackground(Void... params) {
            analytics.sendEvent("Splash", "Auto-Login", Boolean.toString(autoLogin));

            // If we're auto-logging in and there is no internet, skip everything
            if (autoLogin && !Utils.isConnected(SplashActivity.this)) {
                return null;
            }

            // Try logging them in if needed
            if (autoLogin) {
                try {
                    mcGillManager.login();
                } catch (IOException e) {
                    return e;
                }
            }

            // Check if we need to download everything or only the essential stuff
            //  We need to download everything if there is null info
            boolean downloadEverything =
                    SQLite.select().from(Transcript.class).querySingle() == null ||
                            !getDatabasePath(StatementsDB.FULL_NAME).exists();

            // If we need to download everything, do it synchronously. If not, do it asynchronously
            UserDownloader userDownloader = new UserDownloader(SplashActivity.this) {
                @Override
                public void update(String section) {
                    publishProgress(section);
                }
            };

            if (downloadEverything) {
                try {
                    userDownloader.execute();
                } catch (IOException e) {
                    // If there was an exception, return it
                    return e;
                }
            } else {
                userDownloader.start();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            // Update the TextView
            progressText.setText(progress[0]);
        }

        @Override
        protected void onPostExecute(IOException e) {
            // Hide the container
            progressContainer.setVisibility(View.GONE);

            if (e == null) {
                // Connection successful: home page
                startActivity(new Intent(SplashActivity.this, homepageManager.getActivity()));
                finish();
            } else {
                // Connection not successful: login
                showLoginScreen(e);
            }
        }
    }
}