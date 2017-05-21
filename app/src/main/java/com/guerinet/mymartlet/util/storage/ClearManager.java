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

package com.guerinet.mymartlet.util.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.guerinet.mymartlet.util.dagger.prefs.DefaultTermPreference;
import com.guerinet.mymartlet.util.dagger.prefs.PrefsModule;
import com.guerinet.mymartlet.util.dagger.prefs.RegisterTermPreference;
import com.guerinet.mymartlet.util.dagger.prefs.UsernamePreference;
import com.guerinet.mymartlet.util.dbflow.databases.CourseDB;
import com.guerinet.mymartlet.util.dbflow.databases.PlaceDB;
import com.guerinet.mymartlet.util.dbflow.databases.StatementDB;
import com.guerinet.mymartlet.util.dbflow.databases.TranscriptDB;
import com.guerinet.mymartlet.util.dbflow.databases.WishlistDB;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.utils.prefs.BooleanPreference;
import com.orhanobut.hawk.Hawk;
import com.raizlabs.android.dbflow.config.FlowManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

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
     * Remember Username {@link BooleanPreference}
     */
    private final BooleanPreference rememberUsernamePref;
    /**
     * {@link DefaultTermPreference} instance
     */
    private final DefaultTermPreference defaultTermPref;
    /**
     * {@link HomepageManager}
     */
    private final HomepageManager homepageManager;
    /**
     * {@link RegisterTermPreference} instance
     */
    private final RegisterTermPreference registerTermPref;

    /**
     * Default Injectable Constructor
     *
     * @param context              App context
     * @param usernamePref         {@link UsernamePreference} instance
     * @param rememberUsernamePref Remember Username {@link BooleanPreference}
     * @param homepageManager      {@link HomepageManager} instance
     * @param defaultTermPref      {@link DefaultTermPreference} instance
     * @param registerTermPref     {@link RegisterTermPreference} instance
     */
    @Inject
    protected ClearManager(Context context, UsernamePreference usernamePref,
            @Named(PrefsModule.REMEMBER_USERNAME) BooleanPreference rememberUsernamePref,
            HomepageManager homepageManager, DefaultTermPreference defaultTermPref,
            RegisterTermPreference registerTermPref) {
        this.context = context;
        this.rememberUsernamePref = rememberUsernamePref;
        this.usernamePref = usernamePref;
        this.homepageManager = homepageManager;
        this.defaultTermPref = defaultTermPref;
        this.registerTermPref = registerTermPref;
    }

    /**
     * Clears all of the user's info
     */
    public void all() {
        //If the user had not chosen to remember their username, clear it
        if (!rememberUsernamePref.get()) {
            usernamePref.clear();
        }

        // Password
        Hawk.delete(PrefsModule.Hawk.PASSWORD);

        //Schedule
        context.deleteDatabase(CourseDB.FULL_NAME);

        // Transcript
        TranscriptDB.clearTranscript(context);

        // Statements
        context.deleteDatabase(StatementDB.FULL_NAME);

        //HomepageManager
        homepageManager.clear();

        // Default Term
        defaultTermPref.clear();

        // Wishlist
        context.deleteDatabase(WishlistDB.FULL_NAME);
    }

    /**
     * Clears all of the config info
     */
    public void config() {
        // Places
        FlowManager.getDatabase(PlaceDB.class).reset(context);

        // Register terms
        registerTermPref.clear();
    }
}
