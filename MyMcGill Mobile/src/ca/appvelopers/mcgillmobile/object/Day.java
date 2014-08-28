package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien
 * Date: 01/02/14, 6:54 PM
 */
public enum Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY,
    TBA;

    //Get the day based on the number from 1-7
    public static Day getDay(int dayNumber){
        switch (dayNumber) {
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

    //Get the day based on a character (M,T,W,R,F,S,N)
    public static Day getDay(char dayLetter){
        switch (dayLetter) {
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

    //Get the string for a given day
    public String getDayString(Context context){
        switch(this){
            case MONDAY:
                return context.getResources().getString(R.string.monday);
            case TUESDAY:
                return context.getResources().getString(R.string.tuesday);
            case WEDNESDAY:
                return context.getResources().getString(R.string.wednesday);
            case THURSDAY:
                return context.getResources().getString(R.string.thursday);
            case FRIDAY:
                return context.getResources().getString(R.string.friday);
            case SATURDAY:
                return context.getResources().getString(R.string.saturday);
            case SUNDAY:
                return context.getResources().getString(R.string.sunday);
            default:
                return null;
        }
    }

    /**
     * Get the character for a day
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

    /**
     * Get the strings for all of the days
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
