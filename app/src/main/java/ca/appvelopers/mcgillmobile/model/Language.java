/*
 * Copyright 2014-2015 Appvelopers
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

package ca.appvelopers.mcgillmobile.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * The languages that this app is offered in
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Language {
    /**
     * The different languages
     */
    @Retention(RetentionPolicy.CLASS)
    @IntDef({UNDEFINED, ENGLISH, FRENCH})
    public @interface Type{}
    public static final int UNDEFINED = -1;
    public static final int ENGLISH = 0;
    public static final int FRENCH = 1;

    /**
     * @return The localized language String
     */
    public static String getString(@Type int language) {
        switch (language) {
            case ENGLISH:
                return App.getContext().getString(R.string.english);
            case FRENCH:
                return App.getContext().getString(R.string.french);
            default:
                throw new IllegalStateException("Unknown Language");
        }
    }

    /**
     * @return The language code
     */
    public static String getCode(@Type int language) {
        switch (language) {
            case ENGLISH:
                return "en; ";
            case FRENCH:
                return "fr";
            default:
                throw new IllegalStateException("Unknown language");
        }
    }
}
