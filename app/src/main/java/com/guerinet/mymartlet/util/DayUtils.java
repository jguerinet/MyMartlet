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

import android.support.annotation.StringRes;

import com.guerinet.mymartlet.R;

import org.threeten.bp.DayOfWeek;

import java.util.List;

/**
 * Utility methods for the days of the week
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class DayUtils {

    /**
     * Gets the day based on a character (M, T, W, R, F, S, N). Characters taken from Minerva
     *
     * @param dayChar The day character
     * @return The corresponding day
     */
    public static DayOfWeek getDay(char dayChar) {
        switch (dayChar) {
            case 'M':
                return DayOfWeek.MONDAY;
            case 'T':
                return DayOfWeek.TUESDAY;
            case 'W':
                return DayOfWeek.WEDNESDAY;
            case 'R':
                return DayOfWeek.THURSDAY;
            case 'F':
                return DayOfWeek.FRIDAY;
            case 'S':
                return DayOfWeek.SATURDAY;
            case 'U':
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalStateException("Unknown day character: " + dayChar);
        }
    }

    /**
     * Gets the character for a given day
     *
     * @return The day character
     */
    public static char getDayChar(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return 'M';
            case TUESDAY:
                return 'T';
            case WEDNESDAY:
                return 'W';
            case THURSDAY:
                return 'R';
            case FRIDAY:
                return 'F';
            case SATURDAY:
                return 'S';
            case SUNDAY:
                return 'N';
            default:
                throw new IllegalStateException("Unknown day: " + day);
        }
    }

    /**
     * Returns the String Id for this given day
     *
     * @param day Day
     * @return Corresponding String Id
     */
    public static @StringRes int getStringId(DayOfWeek day) {
        switch (day) {
            case MONDAY:
                return R.string.monday;
            case TUESDAY:
                return R.string.tuesday;
            case WEDNESDAY:
                return R.string.wednesday;
            case THURSDAY:
                return R.string.thursday;
            case FRIDAY:
                return R.string.friday;
            case SATURDAY:
                return R.string.saturday;
            case SUNDAY:
                return R.string.sunday;
            default:
                throw new IllegalStateException("Unknown day " + day);
        }
    }

    /**
     * Gets a String representing all of the given days by their character
     *
     * @param days The days
     * @return The String representing the days
     */
    public static String getDayStrings(List<DayOfWeek> days) {
        String dayString = "";

        for (DayOfWeek day : days) {
            dayString += getDayChar(day);
        }

        return dayString;
    }
}
