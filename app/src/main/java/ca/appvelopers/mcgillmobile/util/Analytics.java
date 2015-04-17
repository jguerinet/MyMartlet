/*
 * Copyright 2014-2015 Appvelopers Inc.
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

import android.content.Context;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;


/**
 * Sends screens and events to the Google Analytics console
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class Analytics {
    private static final String TAG = "Analytics";
    /**
     * The singleton instance of this class
     */
    private static Analytics mGoogleAnalytics;
    /**
     * The tracker
     */
    private Tracker mTracker;

    /**
     * @return The Analytics instance
     */
    public static Analytics getInstance(){
        //Instantiate it if it's not already ready
        if(mGoogleAnalytics == null){
            mGoogleAnalytics = new Analytics();
        }

        return mGoogleAnalytics;
    }

    /**
     * Default private constructor, used to set up the tracker
     */
    private Analytics(){
        //Set up the tracker
        this.mTracker = GoogleAnalytics.getInstance(App.getContext()).newTracker(R.xml.analytics);
    }

    /**
     * Sends an event
     *
     * @param category The event category
     * @param action   The event action
     * @param label    The event label
     */
    public void sendEvent(String category, String action, String label){
        if(!Constants.dev){
            this.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());

            Log.d(TAG, "Event: " + category + ", " + action + ", " + label);
        }
    }

    /**
     * Sends a screen view
     *
     * @param screenName The name of the screen
     */
    public void sendScreen(String screenName){
        if(!Constants.dev){
            //Set the screen name
            this.mTracker.setScreenName(screenName);

            //Send the screen view
            this.mTracker.send(new HitBuilders.ScreenViewBuilder().build());

            Log.d(TAG, "Screen: " + screenName);
        }
    }
}
