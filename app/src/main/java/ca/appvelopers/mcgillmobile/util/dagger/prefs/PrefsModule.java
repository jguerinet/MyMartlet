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

package ca.appvelopers.mcgillmobile.util.dagger.prefs;

import android.content.SharedPreferences;

import com.guerinet.utils.prefs.BooleanPreference;
import com.guerinet.utils.prefs.DatePreference;
import com.guerinet.utils.prefs.IntPreference;

import javax.inject.Named;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.util.dagger.AppModule;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the SharedPreferences values
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Module(includes = AppModule.class)
public class PrefsModule {

    /* PREFERENCE NAMES */
    public static final String VERSION = "version";
    public static final String MIN_VERSION = "min_version";
    public static final String FIRST_OPEN = "first_open";
    public static final String STATS = "statistics";
    public static final String SCHEDULE_24HR = "24hr Schedule";
    public static final String REMEMBER_USERNAME = "remember_username";
    public static final String EULA = "user_agreement";
    public static final String SEAT_CHECKER = "seat_checker";
    public static final String GRADE_CHECKER = "grade_checker";
    public static final String IMS_CONFIG = "ims_config";
    public static final String IMS_PLACES = "ims_places";
    public static final String IMS_CATEGORIES = "ims_categories";
    public static final String IMS_REGISTRATION = "ims_registration";

    /* HAWK PREFERENCE NAMES */
    public class Hawk {
        public static final String PASSWORD = "password";
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return Stored version {@link IntPreference}, defaults to -1
     */
    @Provides
    @Singleton
    @Named(VERSION)
    IntPreference provideVersion(SharedPreferences prefs) {
        return new IntPreference(prefs, VERSION, -1);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return The minimum app version required, defaults to -1
     */
    @Provides
    @Singleton
    @Named(MIN_VERSION)
    IntPreference provideMinVersion(SharedPreferences prefs) {
        return new IntPreference(prefs, MIN_VERSION, -1);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if this is the first time that the user logs in, false otherwise
     */
    @Provides
    @Singleton
    @Named(FIRST_OPEN)
    BooleanPreference provideFirstOpen(SharedPreferences prefs) {
        return new BooleanPreference(prefs, FIRST_OPEN, true);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we can collect anonymous usage statistics, false otherwise (defaults to true)
     */
    @Provides
    @Singleton
    @Named(STATS)
    BooleanPreference provideStatistics(SharedPreferences prefs) {
        return new BooleanPreference(prefs, STATS, true);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if User wants their schedule in the 24 hour format
     */
    @Provides
    @Singleton
    @Named(SCHEDULE_24HR)
    BooleanPreference provideScheduleTime(SharedPreferences prefs) {
        return new BooleanPreference(prefs, SCHEDULE_24HR, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should remember the user's username, false otherwise (defaults to true)
     */
    @Provides
    @Singleton
    @Named(REMEMBER_USERNAME)
    BooleanPreference provideRememberUsername(SharedPreferences prefs) {
        return new BooleanPreference(prefs, REMEMBER_USERNAME, true);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if the user has accepted the EULA, false otherwise (defaults to false)
     */
    @Provides
    @Singleton
    @Named(EULA)
    BooleanPreference provideEULA(SharedPreferences prefs) {
        return new BooleanPreference(prefs, EULA, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should be checking seats for the user, false otherwise
     */
    @Provides
    @Singleton
    @Named(SEAT_CHECKER)
    BooleanPreference provideSeatChecker(SharedPreferences prefs) {
        return new BooleanPreference(prefs, SEAT_CHECKER, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should be checking grades for the user, false otherwise
     */
    @Provides
    @Singleton
    @Named(GRADE_CHECKER)
    BooleanPreference provideGradeChecker(SharedPreferences prefs) {
        return new BooleanPreference(prefs, GRADE_CHECKER, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return The If-Modified-Since date for the config, null if none
     */
    @Provides
    @Singleton
    @Named(IMS_CONFIG)
    DatePreference provideIMSConfig(SharedPreferences prefs) {
        return new DatePreference(prefs, IMS_CONFIG, null);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return The If-Modified-Since date for the places, null if none
     */
    @Provides
    @Singleton
    @Named(IMS_PLACES)
    DatePreference provideIMSPlaces(SharedPreferences prefs) {
        return new DatePreference(prefs, IMS_PLACES, null);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return The If-Modified-Since date for the categories, null if none
     */
    @Provides
    @Singleton
    @Named(IMS_CATEGORIES)
    DatePreference provideIMSCategories(SharedPreferences prefs) {
        return new DatePreference(prefs, IMS_CATEGORIES, null);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return The If-Modified-Since date for the registration semesters, null if none
     */
    @Provides
    @Singleton
    @Named(IMS_REGISTRATION)
    DatePreference provideIMSRegistration(SharedPreferences prefs) {
        return new DatePreference(prefs, IMS_REGISTRATION, null);
    }
}
