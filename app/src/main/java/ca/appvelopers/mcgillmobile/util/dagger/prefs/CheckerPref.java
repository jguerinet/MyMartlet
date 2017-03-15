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

package ca.appvelopers.mcgillmobile.util.dagger.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.guerinet.utils.prefs.StringPreference;

/**
 * Stores the user's preference for how often a checker service should run
 * @author Julien Guerinet
 * @since 2.4.0
 */
public class CheckerPref extends StringPreference {
    /**
     * Different checking frequencies
     */
    @StringDef({NEVER, WEEKLY, DAILY, TWELVE_HOURS, SIX_HOURS, HOURLY})
    public @interface Frequency {}
    public static final String NEVER = "NEVER";
    public static final String WEEKLY = "WEEKLY";
    public static final String DAILY = "DAILY";
    public static final String TWELVE_HOURS = "TWELVE_HOURS";
    public static final String SIX_HOURS = "SIX_HOURS";
    public static final String HOURLY = "HOURLY";

    /**
     * Default Constructor
     *
     * @param prefs {@link SharedPreferences} instance
     * @param key   Key under which the pref should be stored
     */
    CheckerPref(@NonNull SharedPreferences prefs, @NonNull String key) {
        super(prefs, key, NEVER);
    }

    @Override
    public void set(@Frequency String value) {
        super.set(value);
    }

    @Frequency
    @Override
    public String get() {
        //noinspection WrongConstant
        return super.get();
    }
}
