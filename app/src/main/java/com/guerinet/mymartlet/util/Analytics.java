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

package com.guerinet.mymartlet.util;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.guerinet.mymartlet.BuildConfig;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.util.dagger.prefs.PrefsModule;
import com.guerinet.utils.prefs.BooleanPreference;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;


/**
 * Sends screens and events to Google Analytics
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Analytics {
    /**
     * Google Analytics {@link Tracker}
     */
    private final Tracker tracker;
    /**
     * Statistics {@link BooleanPreference}
     */
    private final BooleanPreference statsPref;

    /**
     * Default Constructor
     *
     * @param context   App context
     * @param statsPref Statistics {@link BooleanPreference}
     */
    @Inject
    Analytics(Context context, @Named(PrefsModule.STATS) BooleanPreference statsPref) {
        // Set up the tracker
        tracker = GoogleAnalytics.getInstance(context).newTracker(R.xml.global_tracker);
        this.statsPref = statsPref;
    }

    /**
     * Analytics are disabled if we are in debug mode or if the user has disabled them
     *
     * @return True if statistics are disabled, false otherwise
     */
    private boolean isDisabled() {
        return BuildConfig.DEBUG || !statsPref.get();
    }

    /**
     * Sends an event
     *
     * @param category Event category
     * @param action   Event action
     */
    public void sendEvent(String category, String action) {
        if (isDisabled()) {
            Timber.d("GA Event: %s, %s", category, action);
            return;
        }

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
        Timber.i("GA Event: %s, %s", category, action);
    }

    /**
     * Sends an event
     *
     * @param category Event category
     * @param action   Event action
     * @param label    Event label
     */
    public void sendEvent(String category, String action, String label) {
        if (isDisabled()) {
            Timber.d("GA Event: %s, %s, %s", category, action, label);
            return;
        }

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
        Timber.i("GA Event: %s, %s, %s", category, action, label);
    }

    /**
     * Sends a screen view
     *
     * @param screenName Name of the screen
     */
    public void sendScreen(String screenName) {
        if (isDisabled()) {
            Timber.d("GA Screen: %s", screenName);
            return;
        }

        // Set the screen name and send it
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        Timber.i("GA Screen: %s", screenName);
    }
}
