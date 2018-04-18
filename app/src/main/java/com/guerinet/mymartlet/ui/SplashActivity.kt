/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.ui

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import butterknife.BindView
import com.guerinet.mymartlet.BuildConfig
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Transcript
import com.guerinet.mymartlet.model.exception.MinervaException
import com.guerinet.mymartlet.ui.settings.AgreementActivity
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.dagger.prefs.UsernamePref
import com.guerinet.mymartlet.util.dbflow.databases.StatementDB
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.manager.UpdateManager
import com.guerinet.mymartlet.util.service.ConfigDownloadService
import com.guerinet.mymartlet.util.thread.UserDownloader
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.prefs.IntPref
import com.guerinet.suitcase.util.extensions.isConnected
import com.guerinet.suitcase.util.extensions.openPlayStoreApp
import com.orhanobut.hawk.Hawk
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_splash.*
import okhttp3.ResponseBody
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

/**
 * First activity that is opened when the app is started
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SplashActivity : BaseActivity() {

    /**
     * Update text for when downloading everything
     */
    @BindView(R.id.progress_text)
    internal var progressText: TextView? = null
    /**
     * The [McGillManager] instance
     */
    @Inject
    internal var mcGillManager: McGillManager? = null

    private val rememberUsernamePref: BooleanPref by inject(Prefs.REMEMBER_USERNAME)

    private val minVersionPref: IntPref by inject(Prefs.MIN_VERSION)

    private val eulaPref: BooleanPref by inject(Prefs.EULA)

    private val usernamePref: UsernamePref by inject()

    private val imm: InputMethodManager by inject()

    /**
     * [UpdateManager] instance
     */
    @Inject
    internal var updateManager: UpdateManager? = null
    /**
     * The [HomepageManager] instance
     */
    @Inject
    internal var homepageManager: HomepageManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppInitializer().execute()

        // OnClick listeners
        versionButton.setOnClickListener { openPlayStoreApp() }
        loginButton.setOnClickListener { login() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            AGREEMENT_CODE -> if (resultCode == Activity.RESULT_OK) {
                // If they agreed, go to the next screen
                showNextScreen()
            } else {
                // If not, close the app
                finish()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onReceivedBroadcast(intent: Intent) {
        // Override the Minerva broadcast to not log the user out since they are already logged out
        if (intent.action != Constants.BROADCAST_MINERVA) {
            super.onReceivedBroadcast(intent)
        }
    }

    /**
     * Shows the first screen to the user depending on their situation
     */
    private fun showNextScreen() {
        if (minVersionPref.value > BuildConfig.VERSION_CODE) {
            // If we don't have the min required version, show the right container
            minVersionContainer.isVisible = true
        } else if (usernamePref.value == null || !Hawk.contains(Prefs.PASSWORD)) {
            // If we are missing some login info, show the login screen with no error message
            showLoginScreen(intent.getSerializableExtra(Constants.EXCEPTION) as? IOException)
        } else {
            // Try logging the user in and download their info
            AutoLogin(true).execute()
        }
    }

    /**
     * Shows the login screen and an eventual error message [e]
     */
    private fun showLoginScreen(e: IOException?) {
        // Show the login container
        loginContainer.isVisible = true

        // Delete of the previous user's info
        clearManager.clearUserInfo()

        // Fill out username text if it is present
        username.setText(usernamePref.value)

        // Set it to that when clicking the IME Action button it tries to log you in directly
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                loginButton.performClick()
                true
            } else {
                false
            }
        }

        // Remember Me box checked based on user's previous preference
        rememberUsername.isChecked = rememberUsernamePref.value

        // Check if an error message needs to be displayed, display it if so
        if (e != null) {
            errorDialog(if (e is MinervaException)
                R.string.login_error_wrong_data else R.string.error_other)
        }

        ga.sendScreen("Login")
    }

    /**
     * Attempts to log the user in
     */
    private fun login() {
        // Hide the keyboard
        username.clearFocus()
        imm.hideSoftInputFromWindow(username.windowToken, 0)

        // Get the inputted username and password
        val username = this.username.text.toString().trim()
        val password = this.password.text.toString().trim()

        // Check that both of them are not empty, create appropriate error messages if so
        if (username.isEmpty()) {
            errorDialog(R.string.login_error_username_empty)
            return
        } else if (password.isEmpty()) {
            errorDialog(R.string.login_error_password_empty)
            return
        } else if (!isConnected) {
            errorDialog(R.string.error_no_internet)
            return
        }

        progressContainer.isVisible = true

        mcGillManager!!.login(username + getString(R.string.login_email), password,
                object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>,
                            response: Response<ResponseBody>) {
                        // Store the login info
                        usernamePref.value = username
                        Hawk.put(Prefs.PASSWORD, password)
                        rememberUsernamePref.value = rememberUsername.isChecked

                        ga.sendEvent("Login", "Remember Username",
                                rememberUsername.isChecked.toString())

                        handler.post {
                            // Hide the login container
                            loginContainer.isVisible = false

                            // Start the downloading of information
                            AutoLogin(false).execute()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        val error = if (t is MinervaException)
                            R.string.login_error_wrong_data
                        else
                            R.string.error_other

                        handler.post {
                            // If for some reason the activity is finishing, don't show this
                            if (isFinishing) {
                                return@post
                            }
                            progressContainer.isVisible = false
                            errorDialog(error)
                        }
                    }
                })
    }

    /**
     * Initializes the app by starting the config download and running through update code
     */
    private inner class AppInitializer : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            // Run any update code
            updateManager!!.update()

            // Initialize the McGillService
            mcGillManager!!.init()

            // Start downloading the config
            startService(Intent(this@SplashActivity, ConfigDownloadService::class.java))
            return null
        }

        override fun onPostExecute(aVoid: Void) {
            if (!eulaPref.get()) {
                // If the user has not accepted the EULA, show it before continuing
                val intent = Intent(this@SplashActivity, AgreementActivity::class.java)
                        .putExtra(Prefs.EULA, true)
                startActivityForResult(intent, AGREEMENT_CODE)
            } else {
                // If they have, go to the next screen
                showNextScreen()
            }
        }
    }

    /**
     * Initializes the app by logging the user in and downloading the required information
     */
    private inner class AutoLogin
    /**
     * Default Constructor
     *
     * @param autoLogin True if we should auto-login the user, false if they have just logged in
     */
    (
            /**
             * True if we should auto-login the user, false if they have just logged in
             */
            private val autoLogin: Boolean) : AsyncTask<Void, String, IOException>() {

        override fun onPreExecute() {
            // Show the progress container
            progressContainer!!.visibility = View.VISIBLE

            // Reset the text (if it was set during a previous login attempt)
            progressText!!.text = ""
        }

        override fun doInBackground(vararg params: Void): IOException? {
            ga.sendEvent("Splash", "Auto-Login", java.lang.Boolean.toString(autoLogin))

            // If we're auto-logging in and there is no internet, skip everything
            if (autoLogin && !this@SplashActivity.isConnected) {
                return null
            }

            // Try logging them in if needed
            if (autoLogin) {
                try {
                    mcGillManager!!.login()
                } catch (e: IOException) {
                    return e
                }

            }

            // Check if we need to download everything or only the essential stuff
            //  We need to download everything if there is null info
            val downloadEverything = SQLite.select().from(Transcript::class.java).querySingle() == null || !getDatabasePath(StatementDB.FULL_NAME).exists()

            // If we need to download everything, do it synchronously. If not, do it asynchronously
            val userDownloader = object : UserDownloader(this@SplashActivity) {
                override fun update(section: String) {
                    publishProgress(section)
                }
            }

            if (downloadEverything) {
                try {
                    userDownloader.execute()
                } catch (e: IOException) {
                    // If there was an exception, return it
                    return e
                }

            } else {
                userDownloader.start()
            }
            return null
        }

        override fun onProgressUpdate(vararg progress: String) {
            // Update the TextView
            progressText!!.text = progress[0]
        }

        override fun onPostExecute(e: IOException?) {
            // Hide the container
            progressContainer!!.visibility = View.GONE

            if (e == null) {
                // Connection successful: home page
                startActivity(Intent(this@SplashActivity, homepageManager!!.activity))
                finish()
            } else {
                // Connection not successful: login
                showLoginScreen(e)
            }
        }
    }

    companion object {

        /**
         * Code used when starting the AgreementActivity
         */
        private const val AGREEMENT_CODE = 100
    }
}