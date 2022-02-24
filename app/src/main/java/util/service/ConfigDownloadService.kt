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

package com.guerinet.mymartlet.util.service

import android.content.Intent
import androidx.core.app.JobIntentService
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.retrofit.ConfigService
import com.guerinet.suitcase.date.NullDateSetting
import com.guerinet.suitcase.date.android.extensions.rfc1123String
import com.guerinet.suitcase.settings.IntSetting
import com.guerinet.suitcase.util.extensions.isConnected
import kotlinx.datetime.Clock
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import retrofit2.Call
import timber.log.Timber

/**
 * Downloads the latest version of the config info from the server
 * @author Julien Guerinet
 * @since 2.4.0
 */
class ConfigDownloadService : JobIntentService() {

    private val configService by inject<ConfigService>()

    private val imsConfigPref by inject<NullDateSetting>(named(Prefs.IMS_CONFIG))

    private val minVersionPref by inject<IntSetting>(named(Prefs.MIN_VERSION))

    override fun onHandleWork(intent: Intent) {
        if (!isConnected) {
            // If we're not connected to the internet, don't continue
            return
        }

        // Config
        val config = executeRequest(configService.config(getIMS(imsConfigPref)), imsConfigPref)
        config?.apply { minVersionPref.value = androidMinVersion }
    }

    /**
     * Returns the ims String based on the [pref] to use for the call
     */
    private fun getIMS(pref: NullDateSetting): String? = pref.date.rfc1123String

    /**
     * Executes the [call] and updates the [imsPref] if successful. Returns the response object,
     * null if there was an error
     */
    private fun <T> executeRequest(call: Call<T>, imsPref: NullDateSetting): T? {
        return try {
            val response = call.execute()
            if (response.isSuccessful) {
                imsPref.date = Clock.System.now()
            }
            response.body()
        } catch (e: Exception) {
            Timber.e(e, "Error downloading config section")
            null
        }
    }

    /**
     * Config skeleton class
     */
    class Config {

        /**
         * Minimum version of the app that the user needs
         */
        var androidMinVersion = -1
    }
}
