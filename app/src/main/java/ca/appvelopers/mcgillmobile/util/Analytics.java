/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.util;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.BuildConfig;
import ca.appvelopers.mcgillmobile.R;
import timber.log.Timber;


/**
 * Sends screens and events to Google Analytics
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Analytics {
    /**
     * The singleton instance of this class
     */
    private static Analytics instance;
    /**
     * The tracker
     */
    private Tracker mTracker;

    /**
     * @return The Analytics instance
     */
    public static Analytics get() {
        //Instantiate it if it's not already ready
        if(instance == null) {
            instance = new Analytics();
        }
        return instance;
    }

    /**
     * Default private constructor, used to set up the tracker
     */
    private Analytics() {
        //Set up the tracker
        mTracker = GoogleAnalytics.getInstance(App.getContext()).newTracker(R.xml.global_tracker);
    }

    /**
     * Sends an event
     *
     * @param category Event category
     * @param action   Event action
     */
    public void sendEvent(String category, String action) {
        if (!BuildConfig.DEBUG) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .build());
        }
        Timber.d("GA Event: %s, %s", category, action);
    }

    /**
     * Sends an event
     *
     * @param category Event category
     * @param action   Event action
     * @param label    Event label
     */
    public void sendEvent(String category, String action, String label) {
        if (!BuildConfig.DEBUG) {
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .build());
        }
        Timber.d("GA Event: %s, %s, %s", category, action, label);
    }

    /**
     * Sends a screen view
     *
     * @param screenName Name of the screen
     */
    public void sendScreen(String screenName) {
        if (!BuildConfig.DEBUG) {
            //Set the screen name
            mTracker.setScreenName(screenName);

            //Send the screen view
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
        Timber.d("GA Screen: %s", screenName);
    }
}
