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

import com.guerinet.utils.prefs.StringPreference;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.R;

/**
 * Manages the app's language, an extension of the {@link StringPreference}
 * @author Julien Guerinet
 * @since 2.0.4
 */
public class LanguagePreference extends StringPreference {
    /**
     * English language code
     */
    public static final String ENGLISH = "en";
    /**
     * French language code
     */
    public static final String FRENCH = "fr";
    /**
     * App context
     */
    private final Context context;

    /**
     * Default Constructor
     *
     * @param prefs   {@link SharedPreferences} instance
     * @param context App context
     */
    @Inject
    LanguagePreference(SharedPreferences prefs, Context context) {
        super(prefs, "language", ENGLISH);
        this.context = context;
    }

    /**
     * @return The current language String
     */
    public String getString() {
        return getString(get());
    }

    /**
     * @param language Language code
     * @return Localized language String
     */
    public String getString(String language) {
        switch (language) {
            case ENGLISH:
                return context.getString(R.string.english);
            case FRENCH:
                return context.getString(R.string.french);
            default:
                throw new IllegalStateException("Unknown Language");
        }
    }
}
