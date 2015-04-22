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

/**
 * Author: Ryan
 * Date: 04/02/14, 12:50 PM
 *
 * This class holds strings that the parser looks for when it extracts data from the transcript
 */
public enum Token {

    //Semester names
    READMITTED_FALL,
    READMITTED_WINTER,
    READMITTED_SUMMER,
    CHANGE_PROGRAM,

    DIPLOMA,
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
            case READMITTED_FALL:
                return "Readmitted Fall";
            case READMITTED_WINTER:
                return "Readmitted Winter";
            case READMITTED_SUMMER:
                return "Readmitted Summer";
            case CHANGE_PROGRAM:
                return "Change";
            case DIPLOMA:
                return "Dip";
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
