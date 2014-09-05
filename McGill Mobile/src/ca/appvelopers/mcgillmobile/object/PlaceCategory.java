package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 11:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 * Represents the category of a place
 */

public enum PlaceCategory{
    FAVORITES,
    BUILDING,
    FOOD,
    SOCIAL,
    ATHLETICS,
    LIBRARY,
    RESIDENCE;

    public String toString(Context context){
        switch(this){
            case FAVORITES:
                return context.getString(R.string.map_favorites);
            case BUILDING:
                return context.getString(R.string.map_building);
            case FOOD:
                return context.getString(R.string.map_food);
            case SOCIAL:
                return context.getString(R.string.map_social);
            case ATHLETICS:
                return context.getString(R.string.map_athletics);
            case LIBRARY:
                return context.getString(R.string.map_library);
            case RESIDENCE:
                return context.getString(R.string.map_residence);
            default:
                return null;
        }
    }

    public static List<PlaceCategory> getCategories(String[] categoryStrings){
        List<PlaceCategory> categories = new ArrayList<PlaceCategory>();
        for(String category : categoryStrings){
            if(category.equals("Building")){
                categories.add(BUILDING);
            }
            else if(category.equals("Food")){
                categories.add(FOOD);
            }
            else if(category.equals("Social")){
                categories.add(SOCIAL);
            }
            else if(category.equals("Athletics")){
                categories.add(ATHLETICS);
            }
            else if(category.equals("Library")){
                categories.add(LIBRARY);
            }
            else if(category.equals("Residence")){
                categories.add(RESIDENCE);
            }
        }

        return categories;
    }

    public static List<String> getCategories(Context context){
        List<String> categories = new ArrayList<String>();

        //Get the Strings of each of the categories
        for(PlaceCategory placeCategory : PlaceCategory.values()){
            categories.add(placeCategory.toString(context));
        }

        //Sort them alphabetically
        Collections.sort(categories);

        //Add "All" at the top
        categories.add(0, context.getString(R.string.map_all));

        return categories;
    }
}
