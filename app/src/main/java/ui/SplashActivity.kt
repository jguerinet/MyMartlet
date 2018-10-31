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
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.guerinet.mymartlet.BuildConfig
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.exception.MinervaException
import com.guerinet.mymartlet.model.transcript.Transcript
import com.guerinet.mymartlet.ui.settings.AgreementActivity
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.manager.UpdateManager
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.mymartlet.util.retrofit.Result
import com.guerinet.mymartlet.util.service.ConfigDownloadService
import com.guerinet.mymartlet.util.thread.UserDownloader
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.prefs.IntPref
import com.guerinet.suitcase.util.extensions.isConnected
import com.guerinet.suitcase.util.extensions.openPlayStoreApp
import com.orhanobut.hawk.Hawk
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.startService
import org.koin.android.ext.android.inject
import java.io.IOException

/**
 * First activity that is opened when the app is started
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SplashActivity : BaseActivity() {

    private val mcGillManager: McGillManager by inject()

    private val rememberUsernamePref: BooleanPref by inject(Prefs.REMEMBER_USERNAME)

    private val minVersionPref: IntPref by inject(Prefs.MIN_VERSION)

    private val eulaPref: BooleanPref by inject(Prefs.EULA)

    private val usernamePref: UsernamePref by inject()

    private val imm: InputMethodManager by inject()

    private val updateManager: UpdateManager by inject()

    private val homePageManager: HomepageManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // OnClick listeners
        versionButton.setOnClickListener { openPlayStoreApp() }
        loginButton.setOnClickListener {
            launch(UI) { loginPressed() }
        }

        launch {
            // Run any update code
            updateManager.update()

            // Initialize the McGillService
            mcGillManager.init()

            // Start downloading the Config
            startService<ConfigDownloadService>()

            if (!eulaPref.value) {
                // If the user has not accepted the EULA, show it before continuing
                startActivityForResult<AgreementActivity>(AGREEMENT_CODE, Prefs.EULA to true)
            } else {
                // If not, go to the next screen
                showNextScreen()
            }
        }
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
            // If we are missing some login info, show the loginPressed screen with no error message
            showLoginScreen(intent.getSerializableExtra(Constants.EXCEPTION) as? IOException)
        } else {
            // Try logging the user in and download their info
            launch(UI) { login(true) }
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
     * Called when the login button is pressed
     */
    private fun loginPressed() {
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

        val result = mcGillManager.login(username + getString(R.string.login_email), password)

        when (result) {
            is Result.Success<*> -> {
                // Store the login info
                usernamePref.value = username
                Hawk.put(Prefs.PASSWORD, password)
                rememberUsernamePref.value = rememberUsername.isChecked

                ga.sendEvent("Login", "Remember Username", rememberUsername.isChecked.toString())

                // Hide the login container
                loginContainer.isVisible = false

                // Start the downloading of information
                login(false)
            }
            is Result.Failure -> {
                val error = if (result.exception is MinervaException)
                    R.string.login_error_wrong_data
                else
                    R.string.error_other

                // If for some reason the activity is finishing, don't show this
                if (isFinishing) {
                    return
                }

                progressContainer.isVisible = false
                errorDialog(error)
            }
        }
    }

    /**
     * Attempts to either [autoLogin] or manually log in the user
     */
    private fun login(autoLogin: Boolean) {
        // Show the progress container
        progressContainer.isVisible = true

        // Reset the progress text (if it was set during a previous login attempt
        progressText.text = ""

        ga.sendEvent("Splash", "Auto-Login", autoLogin.toString())

        // If we're auto-logging in and there's no internet, skip everything
        if (autoLogin && !isConnected) {
            openHomePage()
            return
        }

        // Try auto login if needed
        if (autoLogin) {
            val result = mcGillManager.login()
            if (result is Result.Failure) {
                // If auto login isn't successful, don't continue
                showLoginScreen(result.exception)
                return
            }
        }

        // Check if we need to download everything or only the essential stuff
        //  We need to download everything if there is null info
        val downloadEverything = SQLite.select().from(Transcript::class).querySingle() == null

        // If we need to download everything, do it synchronously. If not, do it asynchronously
        val userDownloader = object : UserDownloader(this@SplashActivity) {
            override fun update(section: String) {
                updateProgress(section)
            }
        }

        if (downloadEverything) {
            try {
                userDownloader.execute()
            } catch (e: IOException) {
                // If there was an exception, stop here
                showLoginScreen(e)
                return
            }
        } else {
            userDownloader.start()
        }

        // Hide the container
        progressContainer.isVisible = false

        // Connection successful: home page
        openHomePage()
    }

    /**
     * Updates the progress [message]
     */
    private fun updateProgress(message: String) {
        progressText.text = message
    }

    private fun openHomePage() {
        startActivity(Intent(this, homePageManager.activity))
        finish()
    }

    companion object {

        /**
         * Code used when starting the AgreementActivity
         */
        private const val AGREEMENT_CODE = 100
    }
}