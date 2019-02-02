/*
 * Copyright 2014-2019 Julien Guerinet
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
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.manager.UpdateManager
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.prefs.RegisterTermsPref
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.mymartlet.util.retrofit.ConfigService
import com.guerinet.mymartlet.util.room.ConfigDb
import com.guerinet.mymartlet.util.room.UserDb
import com.guerinet.mymartlet.viewmodel.EbillViewModel
import com.guerinet.mymartlet.viewmodel.SemesterViewModel
import com.guerinet.mymartlet.viewmodel.TranscriptViewModel
import com.guerinet.room.UpdateDb
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.date.NullDatePref
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.prefs.IntPref
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
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

    // Clear Manager
    single { ClearManager(get(), get(), get(), get(), get(Prefs.REMEMBER_USERNAME), get(), get()) }

    // GAManager
    single<GAManager> {
        object : GAManager(androidContext(), R.xml.global_tracker) {

            override val isDisabled: Boolean = BuildConfig.DEBUG ||
                    !get<BooleanPref>(Prefs.STATS).value
        }
    }

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

    // UpdateManager
    single { UpdateManager(get(), get()) }
}

val dbModule = module {

    // CategoryDao
    single { get<ConfigDb>().categoryDao() }

    // ConfigDb
    single { ConfigDb.init(androidContext()) }

    // CourseDao
    single { get<UserDb>().courseDao() }

    // CourseResultDao
    single { get<UserDb>().courseResultDao() }

    // PlaceDao
    single { get<ConfigDb>().placeDao() }

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
        HttpLoggingInterceptor { message -> Timber.tag("OkHttp").i(message) }
            .apply { level = HttpLoggingInterceptor.Level.BASIC }
    } bind Interceptor::class

    // McGillService
    single { get<McGillManager>().mcGillService }

    // OkHttp
    single { OkHttpClient.Builder().addInterceptor(get()).build() }

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

    // RegisterTermsPref
    single { RegisterTermsPref(get()) }

    // UsernamePref
    single { UsernamePref(get(), get()) }

    single(Prefs.EULA) { BooleanPref(get(), Prefs.EULA, false) }

    single(Prefs.GRADE_CHECKER) { BooleanPref(get(), Prefs.GRADE_CHECKER, false) }

    single(Prefs.IMS_CATEGORIES) { NullDatePref(get(), Prefs.IMS_CATEGORIES, null) }

    single(Prefs.IMS_CONFIG) { NullDatePref(get(), Prefs.IMS_CONFIG, null) }

    single(Prefs.IMS_PLACES) { NullDatePref(get(), Prefs.IMS_PLACES, null) }

    single(Prefs.IMS_REGISTRATION) { NullDatePref(get(), Prefs.IMS_REGISTRATION, null) }

    single(Prefs.IS_FIRST_OPEN) { BooleanPref(get(), Prefs.IS_FIRST_OPEN, true) }

    single(Prefs.MIN_VERSION) { IntPref(get(), Prefs.MIN_VERSION, -1) }

    single(Prefs.REMEMBER_USERNAME) { BooleanPref(get(), Prefs.REMEMBER_USERNAME, true) }

    single(Prefs.SCHEDULE_24HR) { BooleanPref(get(), Prefs.SCHEDULE_24HR, false) }

    single(Prefs.SEAT_CHECKER) { BooleanPref(get(), Prefs.SEAT_CHECKER, false) }

    single(Prefs.STATS) { BooleanPref(get(), Prefs.STATS, true) }
}

val viewModelsModule = module {

    // EbillViewModel
    viewModel { EbillViewModel(get(), get()) }

    // SemesterViewModel
    viewModel { SemesterViewModel(get(), get()) }

    // TranscriptViewModel
    viewModel { TranscriptViewModel(get(), get(), get(), get()) }

}