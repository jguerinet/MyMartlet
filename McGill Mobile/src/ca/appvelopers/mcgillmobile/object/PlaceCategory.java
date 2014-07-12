package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 11:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 * Represents the category of a place
 */

public enum PlaceCategory{
    BUILDING,
    FOOD,
    SOCIAL,
    ADMINISTRATION,
    ATHLETICS,
    LIBRARY,
    MUSEUM,
    RESIDENCE,
    SECURITY,
    STUDENT_ASSOCIATION,
    FACULTY_OFFICE;

    public String toString(Context context){
        switch(this){
            case BUILDING:
                return context.getString(R.string.map_building);
            case FOOD:
                return context.getString(R.string.map_food);
            case SOCIAL:
                return context.getString(R.string.map_social);
            case ADMINISTRATION:
                return context.getString(R.string.map_administration);
            case ATHLETICS:
                return context.getString(R.string.map_athletics);
            case LIBRARY:
                return context.getString(R.string.map_library);
            case MUSEUM:
                return context.getString(R.string.map_museum);
            case RESIDENCE:
                return context.getString(R.string.map_residence);
            case SECURITY:
                return context.getString(R.string.map_security);
            case STUDENT_ASSOCIATION:
                return context.getString(R.string.map_student_associations);
            case FACULTY_OFFICE:
                return context.getString(R.string.map_faculty_offices);
            default:
                return null;
        }
    }
}
