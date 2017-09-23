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

package com.guerinet.mymartlet.util.manager;

import android.content.Context;

import com.guerinet.mymartlet.BuildConfig;
import com.guerinet.mymartlet.model.AppUpdate;
import com.guerinet.mymartlet.util.dagger.prefs.PrefsModuleKt;
import com.guerinet.suitcase.prefs.IntPref;

import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Runs any update code
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
public class UpdateManager {
    /**
     * App context
     */
    private final Context context;
    /**
     * Version {@link IntPref}
     */
    private final IntPref versionPref;

    /**
     * Default Injectable Constructor
     *
     * @param context     App context
     * @param versionPref Version {@link IntPref}
     */
    @Inject
    UpdateManager(Context context, @Named(PrefsModuleKt.VERSION) IntPref versionPref) {
        this.context = context;
        this.versionPref = versionPref;
    }

    /**
     * Checks if the app has been updated and runs any update code needed if so
     */
    public void update() {
        // Get the version code
        int code = BuildConfig.VERSION_CODE;

        // Get the current version number
        int storedVersion = versionPref.get();

        // Stored version is smaller than version number
        if (storedVersion < code) {
            // Add an app update
            new AppUpdate(BuildConfig.VERSION_NAME, ZonedDateTime.now()).save();

            updateLoop: while (storedVersion < code) {
                // Find the closest version to the stored one and cascade down through the updates
                switch (storedVersion) {
                    case -1:
                        // First time opening the app, break out of the loop
                        break updateLoop;
                    case 0:
                        // This will never get directly called, it will only be accessed through
                        //  another update above
                        break updateLoop;
                }
                storedVersion ++;
            }

            // Store the new version in the SharedPrefs
            versionPref.set(code);
        }
    }
}
