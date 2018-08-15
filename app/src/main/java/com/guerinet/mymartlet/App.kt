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

package com.guerinet.mymartlet

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.guerinet.morf.Morf
import com.guerinet.mymartlet.util.appModule
import com.guerinet.mymartlet.util.networkModule
import com.guerinet.mymartlet.util.prefsModule
import com.guerinet.suitcase.log.ProductionTree
import com.guerinet.suitcase.util.extensions.getColorCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.orhanobut.hawk.Hawk
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.twitter.sdk.android.core.TwitterAuthConfig
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import java.net.SocketTimeoutException

/**
 * Base application instance
 * @author Julien Guerinet
 * @since 1.0.0
 */
class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        initializeTimber()

        // Fabric, Twitter, Crashlytics
        val authConfig = TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET)
        val crashlytics = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(!BuildConfig.REPORT_CRASHES).build())
                .build()
//        Fabric.with(this, Twitter(authConfig), TweetComposer(), crashlytics)

        // Android ThreeTen
        AndroidThreeTen.init(this)

        initializeKoin()

        // Hawk
        Hawk.init(this).build()

        // DBFlow
        FlowManager.init(FlowConfig.Builder(this).build())

        initializeMorf()
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        if (BuildConfig.REPORT_CRASHES) {
            Timber.plant(object : ProductionTree() {

                override fun log(tag: String?, message: String) =
                        Crashlytics.log("$tag: $message")

                override fun logException(t: Throwable) {
                    // Don't log socket timeouts
                    if (t !is SocketTimeoutException) {
                        Crashlytics.logException(t)
                    }
                }
            })
        }
    }

    private fun initializeKoin() = startKoin(this, listOf(appModule, networkModule, prefsModule))

    private fun initializeMorf() =
            Morf.createAndSetShape {
                backgroundId = R.drawable.transparent_redpressed
                drawablePaddingId = R.dimen.padding_small
                paddingId = R.dimen.padding_small
                iconColor = getColorCompat(R.color.red)
            }

    companion object {

//        fun setAlarm(context: Context) {
//            BootReceiver.setAlarm(context)
//        }

//        fun cancelAlarm(context: Context) {
//            BootReceiver.cancelAlarm(context)
//        }
    }
}