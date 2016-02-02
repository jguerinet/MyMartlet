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

package ca.appvelopers.mcgillmobile.util;

import android.content.Context;

import org.threeten.bp.DayOfWeek;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;

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
            case 'N':
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
     * Returns the String for this given day
     *
     * @param context App context
     * @param day     Day
     * @return The corresponding String
     */
    public static String getString(Context context, DayOfWeek day) {
        int stringId;

        switch (day) {
            case MONDAY:
                stringId = R.string.monday;
                break;
            case TUESDAY:
                stringId = R.string.tuesday;
                break;
            case WEDNESDAY:
                stringId = R.string.wednesday;
                break;
            case THURSDAY:
                stringId = R.string.thursday;
                break;
            case FRIDAY:
                stringId = R.string.friday;
                break;
            case SATURDAY:
                stringId = R.string.saturday;
                break;
            case SUNDAY:
                stringId = R.string.sunday;
                break;
            default:
                throw new IllegalStateException("Unknown day " + day);
        }
        return context.getString(stringId);
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
