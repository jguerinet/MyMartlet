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

package ca.appvelopers.mcgillmobile.util.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.guerinet.utils.prefs.BooleanPreference;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.PlacesDB;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.StatementsDB;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.TranscriptDB;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.ScheduleManager;

/**
 * Clears objects from internal storage or {@link SharedPreferences}
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
public class ClearManager {
    /**
     * App context
     */
    private final Context context;
    /**
     * {@link UsernamePreference} instance
     */
    private final UsernamePreference usernamePref;
    /**
     * {@link PasswordPreference} instance
     */
    private final PasswordPreference passwordPref;
    /**
     * Remember Username {@link BooleanPreference}
     */
    private final BooleanPreference rememberUsernamePref;
    /**
     * {@link HomepageManager}
     */
    private final HomepageManager homepageManager;
    /**
     * {@link ScheduleManager} instance
     */
    private final ScheduleManager scheduleManager;

    /**
     * Default Injectable Constructor
     *
     * @param context              App context
     * @param usernamePref         {@link UsernamePreference} instance
     * @param passwordPref         {@link PasswordPreference} instance
     * @param rememberUsernamePref Remember Username {@link BooleanPreference}
     * @param homepageManager      {@link HomepageManager} instance
     * @param scheduleManager      {@link ScheduleManager} instance
     */
    @Inject
    protected ClearManager(Context context, UsernamePreference usernamePref,
            PasswordPreference passwordPref,
            @Named(PrefsModule.REMEMBER_USERNAME) BooleanPreference rememberUsernamePref,
            HomepageManager homepageManager, ScheduleManager scheduleManager) {
        this.context = context;
        this.rememberUsernamePref = rememberUsernamePref;
        this.usernamePref = usernamePref;
        this.passwordPref = passwordPref;
        this.homepageManager = homepageManager;
        this.scheduleManager = scheduleManager;
    }

    /**
     * Clears all of the user's info
     */
    public void all() {
        //If the user had not chosen to remember their username, clear it
        if (!rememberUsernamePref.get()) {
            usernamePref.clear();
        }

        //Password
        passwordPref.clear();

        //Schedule
        scheduleManager.clear();

        // Transcript
        TranscriptDB.clearTranscript(context);

        // Statements
        context.deleteDatabase(StatementsDB.FULL_NAME);

        //HomepageManager
        homepageManager.clear();

        //Default Term
        App.setDefaultTerm(null);

        //Wishlist
        App.setWishlist(new ArrayList<CourseResult>());
    }

    /**
     * Clears all of the config info
     */
    public void config() {
        // Places
        FlowManager.getDatabase(PlacesDB.class).reset(context);

        App.setRegisterTerms(new ArrayList<Term>());
    }
}
