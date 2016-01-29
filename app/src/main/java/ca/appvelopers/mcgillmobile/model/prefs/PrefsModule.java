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

package ca.appvelopers.mcgillmobile.model.prefs;

import android.content.SharedPreferences;

import com.guerinet.utils.prefs.BooleanPreference;
import com.guerinet.utils.prefs.IntPreference;

import javax.inject.Named;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.AppModule;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the {@link SharedPreferences} values
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Module(includes = AppModule.class)
public class PrefsModule {

    /* PREFERENCE NAMES */
    public static final String VERSION = "version";
    public static final String FIRST_OPEN = "first_open";
    public static final String HIDE_PARSER_ERROR = "hide_parser_error";
    public static final String HIDE_LOADING = "hide_loading";
    public static final String STATISTICS = "statistics";
    public static final String REMEMBER_USERNAME = "remember_username";
    public static final String EULA = "user_agreement";
    public static final String SEAT_CHECKER = "seat_checker";
    public static final String GRADE_CHECKER = "grade_checker";

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return Stored version {@link IntPreference}, defaults to -1
     */
    @Provides
    @Singleton
    @Named(VERSION)
    protected IntPreference provideVersion(SharedPreferences prefs) {
        return new IntPreference(prefs, VERSION, -1);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if this is the first time that the user logs in, false otherwise
     */
    @Provides
    @Singleton
    @Named(FIRST_OPEN)
    protected BooleanPreference provideFirstOpen(SharedPreferences prefs) {
        return new BooleanPreference(prefs, FIRST_OPEN, true);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should hide parser errors, false otherwise
     */
    @Provides
    @Singleton
    @Named(HIDE_PARSER_ERROR)
    protected BooleanPreference provideHideParserError(SharedPreferences prefs) {
        return new BooleanPreference(prefs, HIDE_PARSER_ERROR, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should hide the loading skip dialog, false otherwise
     */
    @Provides
    @Singleton
    @Named(HIDE_LOADING)
    protected BooleanPreference provideHideLoading(SharedPreferences prefs) {
        return new BooleanPreference(prefs, HIDE_LOADING, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we can collect anonymous usage statistics, false otherwise (defaults to true)
     */
    @Provides
    @Singleton
    @Named(STATISTICS)
    protected BooleanPreference provideStatistics(SharedPreferences prefs) {
        return new BooleanPreference(prefs, STATISTICS, true);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should remember the user's username, false otherwise (defaults to true)
     */
    @Provides
    @Singleton
    @Named(REMEMBER_USERNAME)
    protected BooleanPreference provideRememberUsername(SharedPreferences prefs) {
        return new BooleanPreference(prefs, REMEMBER_USERNAME, true);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if the user has accepted the EULA, false otherwise (defaults to false)
     */
    @Provides
    @Singleton
    @Named(EULA)
    protected BooleanPreference provideEULA(SharedPreferences prefs) {
        return new BooleanPreference(prefs, EULA, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should be checking seats for the user, false otherwise
     */
    @Provides
    @Singleton
    @Named(SEAT_CHECKER)
    protected BooleanPreference provideSeatChecker(SharedPreferences prefs) {
        return new BooleanPreference(prefs, SEAT_CHECKER, false);
    }

    /**
     * @param prefs {@link SharedPreferences} instance
     * @return True if we should be checking grades for the user, false otherwise
     */
    @Provides
    @Singleton
    @Named(GRADE_CHECKER)
    protected BooleanPreference provideGradeChecker(SharedPreferences prefs) {
        return new BooleanPreference(prefs, GRADE_CHECKER, false);
    }
}
