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

package ca.appvelopers.mcgillmobile.util.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.IntPreference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.storage.ClearManager;

/**
 * Runs any update code
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
public class UpdateManager {
    /**
     * App context
     */
    private final Context context;
    /**
     * Version {@link IntPreference}
     */
    private final IntPreference versionPref;
    /**
     * {@link ClearManager} instance
     */
    private final ClearManager clearManager;
    /**
     * {@link SharedPreferences} instance
     */
    private final SharedPreferences sharedPrefs;

    /**
     * Default Injectable Constructor
     *
     * @param context      App context
     * @param versionPref  Version {@link IntPreference}
     * @param clearManager {@link ClearManager} instance
     * @param sharedPrefs  {@link SharedPreferences} instance
     */
    @Inject
    UpdateManager(Context context, @Named(PrefsModule.VERSION) IntPreference versionPref,
            ClearManager clearManager, SharedPreferences sharedPrefs) {
        this.context = context;
        this.versionPref = versionPref;
        this.clearManager = clearManager;
        this.sharedPrefs = sharedPrefs;
    }

    /**
     * Runs any update code that would need to run before an activity is loaded
     */
    public void preLaunchUpdate() {
        // Get the version code
        int code = Utils.versionCode(context);

        // Get the current version number
        int storedVersion = versionPref.get();

        // Stored version is smaller than version number
        if (storedVersion < code) {
            updateLoop: while (storedVersion < code) {
                // Find the closest version to the stored one and cascade down through the updates
                switch (storedVersion) {
                    case -1:
                        // First time opening the app, break out of the loop
                        break updateLoop;
                    case 24:
                        preLaunchUpdate25();
                    case 0:
                        // This will never get directly called, it will only be accessed through
                        //  another update above
                        break updateLoop;
                }
                storedVersion ++;
            }
        }
    }

    /**
     * Checks if the app has been updated and runs any update code needed if so
     */
    public void update() {
        // Get the version code
        int code = Utils.versionCode(context);

        // Get the current version number
        int storedVersion = versionPref.get();

        // Stored version is smaller than version number
        if (storedVersion < code) {
            updateLoop: while (storedVersion < code) {
                // Find the closest version to the stored one and cascade down through the updates
                switch (storedVersion) {
                    case -1:
                        // First time opening the app, break out of the loop
                        break updateLoop;
                    case 6:
                        update7();
                    case 12:
                        update13();
                    case 15:
                        update16();
                    case 16:
                        update17();
                    case 0:
                        // This will never get directly called, it will only be accessed through
                        //  another update above
                        break updateLoop;
                }
                storedVersion ++;
            }

            // Store the new version in the SharedPrefs
            versionPref.set(code);
        }
    }

    /**
     * v2.4.0
     * - Deletion of old places file, we've moved to DBFlow.
     */
    private void updateSomething() {
        // Delete the old places file
        context.deleteFile("places");
    }

    /**
     * v2.3.2
     * - Changed the way the language pref was being stored
     */
    private void preLaunchUpdate25() {
        try {
            // Get the int stored at the current language key
            int language = sharedPrefs.getInt("language", 0);

            // Store the equivalent language code in its place
            sharedPrefs.edit()
                    .putString("language", language == 0 ? "en" : "fr")
                    .apply();
        } catch (Exception ignored) {
            // This will be happen if for some reason the pre-launch update code already run
            //  but the launch update code never ran
        }
    }

    /**
     * v2.2.0
     * - Redid the entire admin system
     * - Redid all of the user info parsing, made some changes to the objects
     */
    private void update17() {
        // Redownload everything
        clearManager.config();
        clearManager.all();
    }

    /**
     * v2.1.0
     * - Removed Hungarian notation everywhere -> redownload config and user data
     */
    private void update16() {
        // Redownload everything
        clearManager.config();
        clearManager.all();
    }

    /**
     * v2.0.1
     * - Object Changes  -> Force the user to reload all of their info
     * - Place changes -> Force the reload of all of the config stuff
     */
    private void update13() {
        // Re-download all user info
        clearManager.config();
        clearManager.all();
    }

    /**
     * v1.0.1
     * - Object changes -> Force the reload of all of the info
     */
    private void update7() {
        // Force the user to re-update all of the information in the app
        clearManager.all();
    }
}
