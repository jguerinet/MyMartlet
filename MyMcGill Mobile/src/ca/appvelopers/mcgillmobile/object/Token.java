package ca.appvelopers.mcgillmobile.object;

/**
 * Author: Ryan
 * Date: 04/02/14, 12:50 PM
 *
 * This class holds strings that the parser looks for when it extracts data from the transcript
 */
public enum Token {

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
    GRANTED,

    //End of semester items
    ADVANCED_STANDING,
    TERM_CREDITS,
    TOTAL_CREDITS,
    CREDIT_EXEMPTION,
    TERM_GPA,
    CUM_GPA,
    STANDING;

    //Get the string for a given token
    public String getString(){
        switch(this){

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
            case GRANTED:
                return "Granted";
            case ADVANCED_STANDING:
                return "Advanced Standing";
            case TERM_CREDITS:
                return "TERM TOTALS:";
            case TOTAL_CREDITS:
                return "TOTAL CREDITS:";
            case CREDIT_EXEMPTION:
                return "Credits/Exemptions";
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
