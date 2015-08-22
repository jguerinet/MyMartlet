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

import org.joda.time.LocalDate;

import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * The days of the week
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0.0
 */
public enum Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    /**
     * Used when no date has been set on the course
     */
    TBA;

    /**
     * Gets the day based on a number between 0 and 6
     *
     * @param dayIndex The day index
     * @return The corresponding day, TBA if not found
     */
    public static Day getDay(int dayIndex){
        switch (dayIndex) {
            case 0:
                return Day.MONDAY;
            case 1:
                return Day.TUESDAY;
            case 2:
                return Day.WEDNESDAY;
            case 3:
                return Day.THURSDAY;
            case 4:
                return Day.FRIDAY;
            case 5:
                return Day.SATURDAY;
            case 6:
                return Day.SUNDAY;
            default:
                return Day.TBA;
        }
    }

    /**
     * Gets the day based on a passed date
     *
     * @param date The date
     * @return The corresponding day
     */
    public static Day getDay(LocalDate date){
        //Offset by 1 since Jodatime starts counting at 1
        return getDay(date.getDayOfWeek() - 1);
    }

    /**
     * Gets the day based on a character (M,T,W,R,F,S,N). Characters taken from Minerva
     *
     * @param dayChar The day character
     * @return The corresponding day, TBA if not found
     */
    public static Day getDay(char dayChar){
        switch (dayChar) {
            case 'M':
                return Day.MONDAY;
            case 'T':
                return Day.TUESDAY;
            case 'W':
                return Day.WEDNESDAY;
            case 'R':
                return Day.THURSDAY;
            case 'F':
                return Day.FRIDAY;
            case 'S':
                return Day.SATURDAY;
            case 'N':
                return Day.SUNDAY;
            default:
                return Day.TBA;
        }
    }

    /**
     * Gets the character for a day
     *
     * @return The day character
     */
    public String getDayChar(){
        switch (this) {
            case MONDAY:
                return "M";
            case TUESDAY:
                return "T";
            case WEDNESDAY:
                return "W";
            case THURSDAY:
                return "R";
            case FRIDAY:
                return "F";
            case SATURDAY:
                return "S";
            case SUNDAY:
                return "N";
            default:
                return "TBA";
        }
    }

    @Override
    public String toString(){
        switch(this){
            case MONDAY:
                return App.getContext().getString(R.string.monday);
            case TUESDAY:
                return App.getContext().getString(R.string.tuesday);
            case WEDNESDAY:
                return App.getContext().getString(R.string.wednesday);
            case THURSDAY:
                return App.getContext().getString(R.string.thursday);
            case FRIDAY:
                return App.getContext().getString(R.string.friday);
            case SATURDAY:
                return App.getContext().getString(R.string.saturday);
            case SUNDAY:
                return App.getContext().getString(R.string.sunday);
            default:
                return null;
        }
    }

    /**
     * Gets A String representing all of the given days by their character
     *
     * @param days The days
     * @return The String representing the days
     */
    public static String getDayStrings(List<Day>days){
        String dayString = "";

        for(Day day : days){
            dayString += day.getDayChar();
        }

        return dayString;
    }
}
