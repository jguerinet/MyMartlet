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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.guerinet.utils.prefs.StringPreference;

/**
 * Stores the user's preference for how often a checker service should run
 * @author Julien Guerinet
 * @since 2.4.0
 */
public class CheckerPreference extends StringPreference {
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
     * App context
     */
    private final Context context;

    /**
     * Default Constructor
     *
     * @param prefs   {@link SharedPreferences} instance
     * @param key     Key under which the pref should be stored
     * @param context App context
     */
    CheckerPreference(@NonNull SharedPreferences prefs, @NonNull String key, Context context) {
        super(prefs, key, NEVER);
        this.context = context;
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

    /**
     * @return Title String of the current frequency
     */
    public String getString() {
        return getFrequencyString(context, get());
    }

    /**
     * @param context   App context
     * @param frequency Frequency type
     * @return Title String for the given frequency
     */
    public static String getFrequencyString(Context context, @Frequency String frequency) {
        // TODO Strings
        switch (frequency) {
            case CheckerPreference.NEVER:
                return "Never";
            case CheckerPreference.WEEKLY:
                return "Weekly";
            case CheckerPreference.DAILY:
                return "Daily";
            case CheckerPreference.TWELVE_HOURS:
                return "Every 12 hours";
            case CheckerPreference.SIX_HOURS:
                return "Every 6 hours";
            case CheckerPreference.HOURLY:
                return "Hourly";
            default:
                throw new IllegalArgumentException("Unknown frequency: " + frequency);
        }
    }
}
