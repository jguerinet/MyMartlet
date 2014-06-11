package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author : Julien
 * Date :  2014-05-28 7:56 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public enum Faculty {
    ENVIRONMENTAL_SCIENCES,
    ARTS,
    CONTINUING_STUDIES,
    DENTISTRY,
    EDUCATION,
    ENGINEERING,
    GRADUATE,
    LAW,
    MANAGEMENT,
    MEDICINE,
    MUSIC,
    RELIGIOUS_STUDIES,
    SCIENCE;

    public String toString(Context context) {
        switch (this) {
            case ENVIRONMENTAL_SCIENCES:
                return context.getResources().getString(R.string.faculty_enviro);
            case ARTS:
                return context.getResources().getString(R.string.faculty_arts);
            case CONTINUING_STUDIES:
                return context.getResources().getString(R.string.faculty_continuing_studies);
            case DENTISTRY:
                return context.getResources().getString(R.string.faculty_dentistry);
            case EDUCATION:
                return context.getResources().getString(R.string.faculty_education);
            case ENGINEERING:
                return context.getResources().getString(R.string.faculty_engineering);
            case GRADUATE:
                return context.getResources().getString(R.string.faculty_graduate);
            case LAW:
                return context.getResources().getString(R.string.faculty_law);
            case MANAGEMENT:
                return context.getResources().getString(R.string.faculty_management);
            case MEDICINE:
                return context.getResources().getString(R.string.faculty_medecine);
            case MUSIC:
                return context.getResources().getString(R.string.faculty_music);
            case RELIGIOUS_STUDIES:
                return context.getResources().getString(R.string.faculty_religion);
            case SCIENCE:
                return context.getResources().getString(R.string.faculty_science);
            default:
                return "";
        }
    }
}
