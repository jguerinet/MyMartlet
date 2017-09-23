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

package com.guerinet.mymartlet.util.dagger.prefs

import android.content.SharedPreferences
import com.guerinet.mymartlet.util.dagger.AppModule
import com.guerinet.suitcase.date.DatePref
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.prefs.IntPref
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

const val VERSION = "version"
const val MIN_VERSION = "min_version"
const val FIRST_OPEN = "first_open"
const val STATS = "statistics"
const val SCHEDULE_24HR = "24hr Schedule"
const val REMEMBER_USERNAME = "remember_username"
const val EULA = "user_agreement"
const val SEAT_CHECKER = "seat_checker"
const val GRADE_CHECKER = "grade_checker"
const val IMS_CONFIG = "ims_config"
const val IMS_PLACES = "ims_places"
const val IMS_CATEGORIES = "ims_categories"
const val IMS_REGISTRATION = "ims_registration"
const val PASSWORD = "password"

/**
 * Dagger module for the [SharedPreferences] values
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Module(includes = arrayOf(AppModule::class))
class PrefsModule {

    /**
     * Stored version, defaults to -1
     */
    @Provides
    @Singleton
    @Named(VERSION)
    fun provideVersion(prefs: SharedPreferences): IntPref = IntPref(prefs, VERSION, -1)

    /**
     * Minimum app version required, defaults to -1
     */
    @Provides
    @Singleton
    @Named(MIN_VERSION)
    fun provideMinVersion(prefs: SharedPreferences): IntPref = IntPref(prefs, MIN_VERSION, -1)

    /**
     * True if this is the first time the user logs in, false otherwise. Defaults to true
     */
    @Provides
    @Singleton
    @Named(FIRST_OPEN)
    fun provideFirstOpen(prefs: SharedPreferences): BooleanPref =
            BooleanPref(prefs, FIRST_OPEN, true)

    /**
     * True if we can collect anonymous usage stats, false otherwise. Defaults to true
     */
    @Provides
    @Singleton
    @Named(STATS)
    fun provideStats(prefs: SharedPreferences): BooleanPref = BooleanPref(prefs, STATS, true)

    /**
     * True if the user wants their schedule in the 24 hour format, false otherwise.
     *  Defaults to false
     */
    @Provides
    @Singleton
    @Named(SCHEDULE_24HR)
    fun provideScheduleTime(prefs: SharedPreferences): BooleanPref =
            BooleanPref(prefs, SCHEDULE_24HR, false)

    /**
     * True if we should remember the user's username, false otherwise. Defaults to true
     */
    @Provides
    @Singleton
    @Named(REMEMBER_USERNAME)
    fun provideUsername(prefs: SharedPreferences): BooleanPref =
            BooleanPref(prefs, REMEMBER_USERNAME, true)

    /**
     * True if the user has accepted the EULA, false otherwise. Defaults to false
     */
    @Provides
    @Singleton
    @Named(EULA)
    fun provideEULA(prefs: SharedPreferences): BooleanPref = BooleanPref(prefs, EULA, false)

    /**
     * True if we should be checking seats for the user, false otherwise. Defaults to false
     */
    @Provides
    @Singleton
    @Named(SEAT_CHECKER)
    fun provideSeatChecker(prefs: SharedPreferences): BooleanPref =
            BooleanPref(prefs, SEAT_CHECKER, false)

    /**
     * True if we should be checking grades for the user, false otherwise. Defaults to false
     */
    @Provides
    @Singleton
    @Named(GRADE_CHECKER)
    fun provideGradeChecker(prefs: SharedPreferences): BooleanPref =
            BooleanPref(prefs, GRADE_CHECKER, false)

    /**
     * If-Modified-Since date for the config endpoint, null if none. Defaults to null
     */
    @Provides
    @Singleton
    @Named(IMS_CONFIG)
    fun provideIMSConfig(prefs: SharedPreferences): DatePref = DatePref(prefs, IMS_CONFIG, null)

    /**
     * If-Modified-Since date for the places endpoint, null if none. Defaults to null
     */
    @Provides
    @Singleton
    @Named(IMS_PLACES)
    fun provideIMSPlaces(prefs: SharedPreferences): DatePref = DatePref(prefs, IMS_PLACES, null)

    /**
     * If-Modified-Since date for the categories endpoint, null if none. Defaults to null
     */
    @Provides
    @Singleton
    @Named(IMS_CATEGORIES)
    fun provideIMSCategories(prefs: SharedPreferences): DatePref =
            DatePref(prefs, IMS_CATEGORIES, null)

    /**
     * If-Modified-Since date for the registration terms endpoint, null if none. Defaults to null
     */
    @Provides
    @Singleton
    @Named(IMS_REGISTRATION)
    fun provideIMSRegistration(prefs: SharedPreferences): DatePref =
            DatePref(prefs, IMS_REGISTRATION, null)
}