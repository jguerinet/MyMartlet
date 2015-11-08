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

package ca.appvelopers.mcgillmobile.util.storage;

import android.content.SharedPreferences;

import java.util.ArrayList;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Clears objects from internal storage or {@link SharedPreferences}
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class Clear {

    /**
     * Clears all of the user's info
     */
    public static void all(){
        //If the user had not chosen to remember their username, clear it
        if(!Load.rememberUsername()) {
            Constants.PREFS.edit()
                    .remove(Constants.USERNAME)
                    .apply();

        }

        //Password
        Constants.PREFS.edit()
                .remove(Constants.PASSWORD)
                .apply();

        //Schedule
        App.setCourses(new ArrayList<Course>());

        //Transcript
        App.setTranscript(null);

        //Ebill
        App.setEbill(new ArrayList<Statement>());

        //User Info
        App.setUser(null);

        //Homepage
        App.setHomepage(Homepage.SCHEDULE);

        //Default Term
        App.setDefaultTerm(null);

        //Wishlist
        App.setWishlist(new ArrayList<Course>());

        //Favorite places
        App.setFavoritePlaces(new ArrayList<Place>());
    }
}
