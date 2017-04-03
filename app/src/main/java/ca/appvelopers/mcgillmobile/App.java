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

package ca.appvelopers.mcgillmobile;

import android.app.Application;
import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.utils.ProductionTree;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.SocketTimeoutException;

import ca.appvelopers.mcgillmobile.util.dagger.AppModule;
import ca.appvelopers.mcgillmobile.util.dagger.BaseComponent;
import ca.appvelopers.mcgillmobile.util.dagger.DaggerBaseComponent;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Application implementation
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class App extends Application {
    /**
     * Dagger {@link BaseComponent}
     */
    private BaseComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        // Timber
        if (BuildConfig.DEBUG) {
           Timber.plant(new Timber.DebugTree());
        }
        if (BuildConfig.REPORT_CRASHES) {
            Timber.plant(new ProductionTree() {
                @Override
                protected void log(String message) {
                    Crashlytics.log(message);
                }

                @Override
                protected void logException(Throwable t) {
                    //Don't log SocketTimeoutExceptions
                    if (!(t instanceof SocketTimeoutException)) {
                        Crashlytics.logException(t);
                    }
                }
            });
        }

        // Fabric: Twitter, Crashlytics
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY,
                BuildConfig.TWITTER_SECRET);
        @SuppressWarnings("PointlessBooleanExpression")
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
                .disabled(!BuildConfig.REPORT_CRASHES).build()).build();
        Fabric.with(this, new Twitter(authConfig), new TweetComposer(), crashlytics);

        // Android ThreeTen
        AndroidThreeTen.init(this);

        // Dagger
        component = DaggerBaseComponent.builder()
                .appModule(new AppModule(this))
                .build();
        component.inject(this);

        // DBFlow
        FlowManager.init(new FlowConfig.Builder(this).build());

        // FormGenerator
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding_small);
        FormGenerator.set(new FormGenerator.Builder()
                .setDefaultBackground(R.drawable.transparent_redpressed)
                .setDefaultDrawablePaddingSize(padding)
                .setDefaultPaddingSize(padding)
                .setDefaultIconColor(ContextCompat.getColor(this, R.color.red)));
    }

    /* GETTERS */

    /**
     * @param context App context
     * @return {@link BaseComponent} instance
     */
    public static BaseComponent component(Context context) {
        return ((App) context.getApplicationContext()).component;
    }

    /* HELPER METHODS */

//    //to be set after successful login
//    public static void SetAlarm(Context context){
////        BootReceiver.setAlarm(context);
//    }
//    public static void UnsetAlarm(Context context){
//        BootReceiver.cancelAlarm(context);
//    }
}
