package ca.mcgill.mymcgill.object;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;

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

    /**
     * Get the names of all of the faculties
     * @param context The app context
     * @return The list of faculty Strings
     */
    public static List<String> getFacultyStrings(Context context){
        //Order matters here
        List<String> strings = new ArrayList<String>();
        strings.add(context.getResources().getString(R.string.faculty_enviro));
        strings.add(context.getResources().getString(R.string.faculty_arts));
        strings.add(context.getResources().getString(R.string.faculty_continuing_studies));
        strings.add(context.getResources().getString(R.string.faculty_dentistry));
        strings.add(context.getResources().getString(R.string.faculty_education));
        strings.add(context.getResources().getString(R.string.faculty_engineering));
        strings.add(context.getResources().getString(R.string.faculty_graduate));
        strings.add(context.getResources().getString(R.string.faculty_law));
        strings.add(context.getResources().getString(R.string.faculty_management));
        strings.add(context.getResources().getString(R.string.faculty_medecine));
        strings.add(context.getResources().getString(R.string.faculty_music));
        strings.add(context.getResources().getString(R.string.faculty_religion));
        strings.add(context.getResources().getString(R.string.faculty_science));

        return strings;
    }
}
