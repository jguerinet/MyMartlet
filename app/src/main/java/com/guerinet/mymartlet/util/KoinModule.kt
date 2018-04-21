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

package com.guerinet.mymartlet.util

import android.content.Context
import android.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import com.guerinet.mymartlet.BuildConfig
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.util.dagger.prefs.STATS
import com.guerinet.mymartlet.util.manager.ClearManager
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.date.DatePref
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.prefs.IntPref
import com.squareup.moshi.Moshi
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import timber.log.Timber

/**
 * Koin modules
 * @author Julien Guerinet
 * @since 2.0.0
 */

/**
 * Base module with generic providers
 */
val appModule: Module = applicationContext {

    // Shared Prefs
    bean { PreferenceManager.getDefaultSharedPreferences(androidApplication().applicationContext) }

    // Moshi
    bean { Moshi.Builder().build() }

    // GAManager
    bean {
        object : GAManager(androidApplication(), R.xml.global_tracker) {
            override fun isDisabled() = BuildConfig.DEBUG || !get<BooleanPref>(STATS).value
        }
    }

    // InputMethodManager
    bean {
        androidApplication().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    // Clear Manager
    bean { ClearManager(get(), get(), get(), get(), get(), get(Prefs.REMEMBER_USERNAME))}
}

val networkModule: Module = applicationContext {

    // HttpLoggingInterceptor
    bean {
        val interceptor = HttpLoggingInterceptor({ message -> Timber.tag("OkHttp").i(message) })
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        interceptor
    }
}

/**
 * Contains all of the SharedPreferences providers
 */
val prefsModule: Module = applicationContext {

    bean(Prefs.VERSION) { IntPref(get(), Prefs.VERSION, -1) }

    bean(Prefs.MIN_VERSION) { IntPref(get(), Prefs.MIN_VERSION, -1) }

    bean(Prefs.IS_FIRST_OPEN) { BooleanPref(get(), Prefs.IS_FIRST_OPEN, true) }

    bean(Prefs.STATS) { BooleanPref(get(), Prefs.STATS, true) }

    bean(Prefs.SCHEDULE_24HR) { BooleanPref(get(), Prefs.SCHEDULE_24HR, false) }

    bean(Prefs.REMEMBER_USERNAME) { BooleanPref(get(), Prefs.REMEMBER_USERNAME, true) }

    bean(Prefs.EULA) { BooleanPref(get(), Prefs.EULA, false) }

    bean(Prefs.SEAT_CHECKER) { BooleanPref(get(), Prefs.SEAT_CHECKER, false) }

    bean(Prefs.GRADE_CHECKER) { BooleanPref(get(), Prefs.GRADE_CHECKER, false) }

    bean(Prefs.IMS_CONFIG) { DatePref(get(), Prefs.IMS_CONFIG, null) }

    bean(Prefs.IMS_PLACES) { DatePref(get(), Prefs.IMS_PLACES, null) }

    bean(Prefs.IMS_CATEGORIES) { DatePref(get(), Prefs.IMS_CATEGORIES, null) }

    bean(Prefs.IMS_REGISTRATION) { DatePref(get(), Prefs.IMS_REGISTRATION, null) }
}