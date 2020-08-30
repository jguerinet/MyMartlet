/*
 * Copyright 2014-2020 Julien Guerinet
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

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.guerinet.morf.Morf
import com.guerinet.mymartlet.util.appModule
import com.guerinet.mymartlet.util.dbModule
import com.guerinet.mymartlet.util.networkModule
import com.guerinet.mymartlet.util.prefsModule
import com.guerinet.mymartlet.util.viewModelsModule
import com.guerinet.suitcase.log.KoinLogger
import com.guerinet.suitcase.log.ProductionTree
import com.guerinet.suitcase.util.extensions.getColorCompat
import com.jakewharton.threetenabp.AndroidThreeTen
import com.orhanobut.hawk.Hawk
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.net.SocketTimeoutException

/**
 * Base application instance
 * @author Julien Guerinet
 * @since 1.0.0
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        initializeCrashlytics()
        initializeAndroidThreeTen()
        initializeKoin()
        initializeHawk()
        initializeMorf()
        initializeTwitter()
    }

    private fun initializeTimber() {

        if (BuildConfig.DEBUG) {
            Timber.plant(object : ProductionTree() {

                override fun log(priority: Int, tag: String?, message: String) =
                    FirebaseCrashlytics
                        .getInstance()
                        .log("${getPriorityChar(priority)}/$tag: $message")

                override fun logException(t: Throwable) {
                    // Don't log socket timeouts
                    if (t is SocketTimeoutException) {
                        return
                    }
                    FirebaseCrashlytics.getInstance().recordException(t)
                }

                private fun getPriorityChar(priority: Int) = when (priority) {
                    Log.ASSERT -> 'A'
                    Log.DEBUG -> 'D'
                    Log.ERROR -> 'E'
                    Log.INFO -> 'I'
                    Log.WARN -> 'W'
                    else -> 'V'
                }
            })
        }
    }

    private fun initializeCrashlytics() =
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

    private fun initializeAndroidThreeTen() = AndroidThreeTen.init(this)

    private fun initializeKoin() = startKoin {
        KoinLogger()
        androidContext(this@App)
        modules(appModule, dbModule, networkModule, prefsModule, viewModelsModule)
    }

    private fun initializeHawk() = Hawk.init(this).build()

    private fun initializeMorf() = Morf.createAndSetShape {
        backgroundId = R.drawable.transparent_redpressed
        drawablePaddingId = R.dimen.padding_small
        paddingId = R.dimen.padding_small
        iconColor = getColorCompat(R.color.red)
    }

    private fun initializeTwitter() {
        val authConfig = TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET)
        val twitterConfig = TwitterConfig.Builder(this)
            .twitterAuthConfig(authConfig)
            .debug(BuildConfig.DEBUG)
            .build()
        Twitter.initialize(twitterConfig)
    }

//    companion object {

//        fun setAlarm(context: Context) {
//            BootReceiver.setAlarm(context)
//        }

//        fun cancelAlarm(context: Context) {
//            BootReceiver.cancelAlarm(context)
//        }
//    }
}
