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

package ca.appvelopers.mcgillmobile.util;

import android.content.Context;

import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.IntPreference;

import ca.appvelopers.mcgillmobile.App;

/**
 * Runs any update code
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Update {

    /**
     * Checks if the app has been updated and runs any update code needed if so
     *
     * @param context      App context
     * @param versionPrefs Version {@link IntPreference}
     */
    public static void update(Context context, IntPreference versionPrefs) {
        //Get the version code
        int code = Utils.versionCode(context);

        //Get the current version number
        int storedVersion = versionPrefs.get();

        //Stored version is smaller than version number
        if (storedVersion < code) {
            updateLoop: while (storedVersion < code) {
                //Find the closest version to the stored one and cascade down through the updates
                switch (storedVersion) {
                    case -1:
                        //First time opening the app, break out of the loop
                        break updateLoop;
                    case 6:
                        update7();
                    case 12:
                        update13();
                    case 15:
                        update16();
                    case 0:
                        //This will never get directly called, it will only be accessed through
                        // another update above
                        break updateLoop;
                }
                storedVersion ++;
            }

            //Store the new version in the SharedPrefs
            versionPrefs.set(code);
        }
    }

    /**
     * v2.1.0
     * - Removed Hungarian notation everywhere -> redownload config and user data
     */
    private static void update16() {
        //Redownload everything
        clearConfig();
        clearUserInfo();
        App.setWishlist(null);
        App.setFavoritePlaces(null);
    }

    /**
     * v2.0.1
     * - Object Changes  -> Force the user to reload all of their info
     * - Place changes -> Force the reload of all of the config stuff
     */
    private static void update13() {
        //Re-download all user info
        clearConfig();
        clearUserInfo();
    }

    /**
     * v1.0.1
     * - Object changes -> Force the reload of all of the info
     */
    private static void update7() {
        //Force the user to re-update all of the information in the app
        clearUserInfo();
    }

    /**
     * Clears all of the config info
     */
    private static void clearConfig() {
        App.setPlaces(null);
        App.setPlaceTypes(null);
        App.setRegisterTerms(null);
    }

    /**
     * Clears all of the downloaded user info
     */
    private static void clearUserInfo() {
        App.setTranscript(null);
        App.setCourses(null);
        App.setEbill(null);
        App.setUser(null);
        App.setDefaultTerm(null);
    }
}
