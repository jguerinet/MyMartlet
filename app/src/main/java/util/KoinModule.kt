/*
 * Copyright 2014-2022 Julien Guerinet
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
import com.guerinet.mymartlet.util.manager.ClearManager
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.manager.UpdateManager
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.mymartlet.util.retrofit.ConfigService
import com.guerinet.mymartlet.util.room.UserDb
import com.guerinet.mymartlet.viewmodel.EbillViewModel
import com.guerinet.mymartlet.viewmodel.MapViewModel
import com.guerinet.mymartlet.viewmodel.SemesterViewModel
import com.guerinet.mymartlet.viewmodel.TranscriptViewModel
import com.guerinet.room.UpdateDb
import com.guerinet.suitcase.analytics.Analytics
import com.guerinet.suitcase.analytics.FAnalytics
import com.guerinet.suitcase.date.NullDateSetting
import com.guerinet.suitcase.settings.BooleanSetting
import com.guerinet.suitcase.settings.IntSetting
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.russhwolf.settings.AndroidSettings
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import okhttp3.logging.HttpLoggingInterceptor.Logger
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
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
val appModule: Module = module {

    // Analytics
    single { FAnalytics(androidContext()) } bind Analytics::class

    // Clear Manager
    single { ClearManager(get(), get(), get(), get(named(Prefs.REMEMBER_USERNAME)), get()) }

    // HomePageManager
    single { HomepageManager(get(), androidContext()) }

    // InputMethodManager
    factory {
        androidContext().getSystemService(Context.INPUT_METHOD_SERVICE)
            as? InputMethodManager ?: error("InputMethodManager not available")
    }

    // McGillManager
    single { McGillManager(get(), get()) }

    // Moshi
    single { Moshi.Builder().build() }

    // Shared Prefs
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }

    // Settings
    single { AndroidSettings(get()) }

    // UpdateManager
    single { UpdateManager(get(), get()) }
}

val dbModule = module {

    // CourseDao
    single { get<UserDb>().courseDao() }

    // CourseResultDao
    single { get<UserDb>().courseResultDao() }

    // SemesterDao
    single { get<UserDb>().semesterDao() }

    // StatementDao
    single { get<UserDb>().statementDao() }

    // TranscriptDao
    single { get<UserDb>().transcriptDao() }

    // TranscriptCourseDao
    single { get<UserDb>().transcriptCourseDao() }

    // UpdateDao
    single { get<UpdateDb>().updateDao() }

    // UpdateDb
    single { UpdateDb.init(androidContext()) }

    // UserDb
    single { UserDb.init(androidContext()) }
}

val networkModule: Module = module {

    // ConfigService
    single { get<Retrofit>().create(ConfigService::class.java) }

    // HttpLoggingInterceptor
    single {
        HttpLoggingInterceptor(object : Logger {
            override fun log(message: String) = Timber.tag("OkHttp").i(message)
        }).apply { level = if (BuildConfig.DEBUG) Level.BODY else Level.BASIC }
    } bind Interceptor::class

    // McGillService
    single { get<McGillManager>().mcGillService }

    // OkHttp
    single { OkHttpClient.Builder().addInterceptor(get<Interceptor>()).build() }

    // Retrofit
    single {
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://mymartlet.herokuapp.com/api/v2/")
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }
}

/**
 * Contains all of the SharedPreferences providers
 */
val prefsModule: Module = module {

    // DefaultTermPref
    single { DefaultTermPref(get()) }

    // UsernamePref
    single { UsernamePref(get(), get()) }

    single(named(Prefs.EULA)) { BooleanSetting(get(), Prefs.EULA, false) }

    single(named(Prefs.GRADE_CHECKER)) { BooleanSetting(get(), Prefs.GRADE_CHECKER, false) }

    single(named(Prefs.IMS_CONFIG)) { NullDateSetting(get(), Prefs.IMS_CONFIG, null) }

    single(named(Prefs.IS_FIRST_OPEN)) { BooleanSetting(get(), Prefs.IS_FIRST_OPEN, true) }

    single(named(Prefs.MIN_VERSION)) { IntSetting(get(), Prefs.MIN_VERSION, -1) }

    single(named(Prefs.REMEMBER_USERNAME)) { BooleanSetting(get(), Prefs.REMEMBER_USERNAME, true) }

    single(named(Prefs.SCHEDULE_24HR)) { BooleanSetting(get(), Prefs.SCHEDULE_24HR, false) }

    single(named(Prefs.SEAT_CHECKER)) { BooleanSetting(get(), Prefs.SEAT_CHECKER, false) }
}

val viewModelsModule = module {

    // EbillViewModel
    viewModel { EbillViewModel(get(), get()) }

    // MapViewModel
    viewModel { MapViewModel(androidApplication()) }

    // SemesterViewModel
    viewModel { SemesterViewModel(get(), get()) }

    // TranscriptViewModel
    viewModel { TranscriptViewModel(get(), get(), get(), get()) }
}
