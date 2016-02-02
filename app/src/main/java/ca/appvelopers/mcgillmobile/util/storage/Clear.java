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

package ca.appvelopers.mcgillmobile.util.storage;

import android.content.SharedPreferences;

import com.guerinet.utils.prefs.BooleanPreference;

import java.util.ArrayList;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;

/**
 * Clears objects from internal storage or {@link SharedPreferences}
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Clear {

    /**
     * Clears all of the user's info
     *
     * @param rememberUsernamePref Remember username {@link BooleanPreference}
     * @param usernamePref         {@link UsernamePreference} instance
     * @param passwordPref         {@link PasswordPreference} instance
     * @param homepageManager      {@link HomepageManager} instance
     */
    public static void all(BooleanPreference rememberUsernamePref, UsernamePreference usernamePref,
            PasswordPreference passwordPref, HomepageManager homepageManager) {
        //If the user had not chosen to remember their username, clear it
        if (!rememberUsernamePref.get()) {
            usernamePref.clear();
        }

        //Password
        passwordPref.clear();

        //Schedule
        App.setCourses(new ArrayList<Course>());

        //Transcript
        App.setTranscript(null);

        //Ebill
        App.setEbill(new ArrayList<Statement>());

        //User Info
        App.setUser(null);

        //HomepageManager
        homepageManager.clear();

        //Default Term
        App.setDefaultTerm(null);

        //Wishlist
        App.setWishlist(new ArrayList<Course>());

        //Favorite places
        App.setFavoritePlaces(new ArrayList<Place>());
    }
}
