package ca.mcgill.mymcgill.object;

import android.content.Context;

import ca.mcgill.mymcgill.R;

/**
 * Author: Ryan
 * Date: 04/02/14, 12:50 PM
 *
 * This class holds strings that the parser looks for when it extracts data from the transcript
 */
public enum Token {

    //Student Information
    SCHOLARSHIP,
    CREDITS_REQUIRED,

    //Semester names
    FALL,
    WINTER,
    SUMMER,

    BACHELOR,
    MASTER,
    DOCTOR,
    YEAR,
    FULL_TIME,
    PROGRAM,

    //End of semester items
    ADVANCED_STANDING,
    TERM_CREDITS,
    TOTAL_CREDITS,
    TERM_GPA,
    CUM_GPA,
    STANDING;

    //Get the string for a given token
    public String getString(){
        switch(this){

            case SCHOLARSHIP:
                break;
            case CREDITS_REQUIRED:
                return "Credits Required";
            case FALL:
                return "Fall";
            case WINTER:
                return "Winter";
            case SUMMER:
                return "Summer";
            case BACHELOR:
                return "Bachelor";
            case MASTER:
                return "Master";
            case DOCTOR:
                return "Doctor";
            case FULL_TIME:
                return "Full-time";
            case YEAR:
                return "Year";
            case PROGRAM:
                break;
            case ADVANCED_STANDING:
                return "Advanced Standing";
            case TERM_CREDITS:
                return "TERM TOTALS:";
            case TOTAL_CREDITS:
                return "TOTAL CREDITS:";
            case TERM_GPA:
                return "TERM GPA";
            case CUM_GPA:
                return "CUM GPA";
            case STANDING:
                return "Standing:";
        }
        return null;
    }
}
