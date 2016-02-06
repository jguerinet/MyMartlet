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

package ca.appvelopers.mcgillmobile.model;

import android.content.Context;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ca.appvelopers.mcgillmobile.R;

/**
 * The different seasons a term can be in
 * @author Julien Guerinet
 * @since 1.0.0
 */
public final class Season {
    /**
     * The different seasons for a term can be in
     */
    @Retention(RetentionPolicy.CLASS)
    @StringDef({FALL, WINTER, SUMMER})
    public @interface Type {}

    /**
     * September - December
     */
    public static final String FALL = "Fall";
    /**
     * January - April
     */
    public static final String WINTER = "Winter";
    /**
     * May, June, July
     */
    public static final String SUMMER = "Summer";

    /**
     * Private Constructor
     */
    private Season(){}

    /**
     * Finds a season based on its language independent Id
     *
     * @param season The String
     * @return The corresponding season
     */
    public static @Type String getSeason(String season) {
        if (season.equalsIgnoreCase(FALL)) {
            return FALL;
        } else if(season.equalsIgnoreCase(WINTER)) {
            return WINTER;
        } else if(season.equalsIgnoreCase(SUMMER)) {
            return SUMMER;
        }
        throw new IllegalStateException("Unknown season: " + season);
    }

    /**
     * @param season The season
     * @return The McGill season number for the given season
     */
    public static String getSeasonNumber(@Type String season) {
        switch(season) {
            case FALL:
                return "09";
            case WINTER:
                return "01";
            case SUMMER:
                return "05";
            default:
                throw new IllegalStateException("Unknown Season: " + season);
        }
    }

    /**
     * @param season The season
     * @return The locale-based String representation of the season
     */
    public static String getString(Context context, @Type String season) {
        switch (season) {
            case FALL:
                return context.getString(R.string.fall);
            case WINTER:
                return context.getString(R.string.winter);
            case SUMMER:
                return context.getString(R.string.summer);
            default:
                throw new IllegalStateException("Unknown Season: " + season);
        }
    }
}
