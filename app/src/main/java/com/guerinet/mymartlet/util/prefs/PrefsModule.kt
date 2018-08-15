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

package com.guerinet.mymartlet.util.prefs

import android.content.SharedPreferences
import com.guerinet.mymartlet.util.dagger.AppModule
import com.guerinet.suitcase.prefs.BooleanPref
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

const val FIRST_OPEN = "first_open"
const val SCHEDULE_24HR = "24hr Schedule"
const val SEAT_CHECKER = "seat_checker"
const val GRADE_CHECKER = "grade_checker"

/**
 * Dagger module for the [SharedPreferences] values
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Module(includes = arrayOf(AppModule::class))
class PrefsModule {

    /**
     * True if this is the first time the user logs in, false otherwise. Defaults to true
     */
    @Provides
    @Singleton
    @Named(FIRST_OPEN)
    fun provideFirstOpen(prefs: SharedPreferences): BooleanPref =
            BooleanPref(prefs, FIRST_OPEN, true)

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
}