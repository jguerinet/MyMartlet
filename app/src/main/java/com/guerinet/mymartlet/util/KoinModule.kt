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
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.prefs.BooleanPref
import com.squareup.moshi.Moshi
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

/**
 * Koin modules
 * @author Julien Guerinet
 * @since 2.0.0
 */

/**
 * Base module with generic providers
 */
val appModule: Module = applicationContext {

    bean { PreferenceManager.getDefaultSharedPreferences(androidApplication().applicationContext) }

    bean { Moshi.Builder().build() }

    bean {
        object : GAManager(androidApplication(), R.xml.global_tracker) {
            override fun isDisabled() = BuildConfig.DEBUG || !get<BooleanPref>(STATS).value
        }
    }

    bean {
        androidApplication().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
}