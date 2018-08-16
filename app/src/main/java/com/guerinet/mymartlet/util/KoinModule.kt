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
import com.guerinet.mymartlet.util.manager.ClearManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.prefs.RegisterTermsPref
import com.guerinet.mymartlet.util.retrofit.ConfigService
import com.guerinet.mymartlet.util.room.UserDb
import com.guerinet.mymartlet.viewmodel.SemesterViewModel
import com.guerinet.mymartlet.viewmodel.TranscriptViewModel
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.date.NullDatePref
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.prefs.IntPref
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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
    bean<GAManager> {
        object : GAManager(androidApplication(), R.xml.global_tracker) {
            override val isDisabled: Boolean = BuildConfig.DEBUG ||
                    !get<BooleanPref>(Prefs.STATS).value
        }
    }

    // InputMethodManager
    bean {
        androidApplication().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    // Clear Manager
    bean { ClearManager(get(), get(), get(), get(), get(), get(Prefs.REMEMBER_USERNAME))}
}

val dbModule = applicationContext {

    // UserDb
    bean { UserDb.init(androidApplication()) }

    // TranscriptDao
    bean { get<UserDb>().transcriptDao() }
}

val networkModule: Module = applicationContext {

    // ConfigService
    bean { get<Retrofit>().create(ConfigService::class.java) }

    // HttpLoggingInterceptor
    bean {
        HttpLoggingInterceptor { message -> Timber.tag("OkHttp").i(message) }
                .level = HttpLoggingInterceptor.Level.BASIC
    }

    // McGillService
    bean { get<McGillManager>().mcGillService }

    // OkHttp
    bean { OkHttpClient.Builder().addInterceptor(get()).build() }

    // Retrofit
    bean {
        Retrofit.Builder()
                .client(get())
                .baseUrl("https://mymartlet.herokuapp.com/api/v2")
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .build()
    }
}

/**
 * Contains all of the SharedPreferences providers
 */
val prefsModule: Module = applicationContext {

    // DefaultTermPref
    bean { DefaultTermPref(get()) }

    bean { RegisterTermsPref(get()) }

    bean(Prefs.EULA) { BooleanPref(get(), Prefs.EULA, false) }

    bean(Prefs.GRADE_CHECKER) { BooleanPref(get(), Prefs.GRADE_CHECKER, false) }

    bean(Prefs.IMS_CATEGORIES) { NullDatePref(get(), Prefs.IMS_CATEGORIES, null) }

    bean(Prefs.IMS_CONFIG) { NullDatePref(get(), Prefs.IMS_CONFIG, null) }

    bean(Prefs.IMS_PLACES) { NullDatePref(get(), Prefs.IMS_PLACES, null) }

    bean(Prefs.IMS_REGISTRATION) { NullDatePref(get(), Prefs.IMS_REGISTRATION, null) }

    bean(Prefs.IS_FIRST_OPEN) { BooleanPref(get(), Prefs.IS_FIRST_OPEN, true) }

    bean(Prefs.MIN_VERSION) { IntPref(get(), Prefs.MIN_VERSION, -1) }

    bean(Prefs.REMEMBER_USERNAME) { BooleanPref(get(), Prefs.REMEMBER_USERNAME, true) }

    bean(Prefs.SCHEDULE_24HR) { BooleanPref(get(), Prefs.SCHEDULE_24HR, false) }

    bean(Prefs.SEAT_CHECKER) { BooleanPref(get(), Prefs.SEAT_CHECKER, false) }

    bean(Prefs.STATS) { BooleanPref(get(), Prefs.STATS, true) }
}

val viewModelsModule = applicationContext {

    // SemesterViewModel
    viewModel { SemesterViewModel(get()) }

    // TranscriptViewModel
    viewModel { TranscriptViewModel(get()) }

}