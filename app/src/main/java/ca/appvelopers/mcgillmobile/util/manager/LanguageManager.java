/*
 * Copyright 2014-2016 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.util.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import com.guerinet.utils.prefs.IntPreference;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.R;

/**
 * Manages the app's language, an extension of the {@link IntPreference}
 * @author Julien Guerinet
 * @since 2.0.4
 */
public class LanguageManager extends IntPreference {
    /**
     * The different languages
     */
    @Retention(RetentionPolicy.CLASS)
    @IntDef({UNDEFINED, ENGLISH, FRENCH})
    public @interface Language {}
    public static final int UNDEFINED = -1;
    public static final int ENGLISH = 0;
    public static final int FRENCH = 1;

    /**
     * App context
     */
    private Context context;

    /**
     * Default Constructor
     *
     * @param prefs        {@link SharedPreferences} instance
     * @param context      App context
     */
    @Inject
    public LanguageManager(SharedPreferences prefs, Context context) {
        super(prefs, "language", ENGLISH);
        this.context = context;
    }

    @Override
    @SuppressWarnings("ResourceType")
    public @Language int get() {
        return super.get();
    }

    @Override
    public void set(@Language int value) {
        super.set(value);
    }

    /**
     * @return The current language String
     */
    public String getString() {
        return getString(get());
    }

    /**
     * @return The localized language String
     */
    public String getString(@Language int language) {
        switch (language) {
            case ENGLISH:
                return context.getString(R.string.english);
            case FRENCH:
                return context.getString(R.string.french);
            default:
                throw new IllegalStateException("Unknown Language");
        }
    }

    /**
     * @return The language code
     */
    public String getCode() {
        switch (get()) {
            case ENGLISH:
                return "en";
            case FRENCH:
                return "fr";
            default:
                throw new IllegalStateException("Unknown language");
        }
    }
}
