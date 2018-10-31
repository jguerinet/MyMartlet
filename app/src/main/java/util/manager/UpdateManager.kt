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

package com.guerinet.mymartlet.util.manager

import android.content.SharedPreferences
import com.guerinet.mymartlet.BuildConfig
import com.guerinet.mymartlet.model.AppUpdate
import com.guerinet.suitcase.util.BaseUpdateManager
import org.threeten.bp.ZonedDateTime

/**
 * Runs any update code
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param prefs [SharedPreferences] instance
 */
class UpdateManager(prefs: SharedPreferences) : BaseUpdateManager(prefs, BuildConfig.VERSION_CODE) {

    override fun firstOpen(): Boolean {
        // Add an app update
        AppUpdate(BuildConfig.VERSION_NAME, ZonedDateTime.now()).save()
        return true
    }

    override fun update(version: Int): Int {
        // Add an app update
        AppUpdate(BuildConfig.VERSION_NAME, ZonedDateTime.now()).save()
        return version
    }
}
