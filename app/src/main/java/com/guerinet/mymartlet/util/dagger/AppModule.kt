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

package com.guerinet.mymartlet.util.dagger

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import com.guerinet.mymartlet.BuildConfig
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.util.dagger.prefs.STATS
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.prefs.BooleanPref
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * Base Dagger module
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Module
class AppModule (val context: Context){

    /**
     * Provides the app [Context]
     */
    @Provides
    @Singleton
    fun provideContext(): Context = context

    /**
     * Provides the [SharedPreferences] singleton instance from the given [Context]
     */
    @Provides
    @Singleton
    fun provideSharedPrefs(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Provides the [Moshi] singleton instance
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    /**
     * Provides the [GAManager] singleton instance
     */
    @Provides
    @Singleton
    fun provideGAManager(context: Context, @Named(STATS) statsPref: BooleanPref): GAManager =
            GAManager(context, R.xml.global_tracker, BuildConfig.DEBUG || !statsPref.get())

    /**
     * Provides the [InputMethodManager] for the given [Context]
     */
    @Provides
    fun provideIMM(context: Context): InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}