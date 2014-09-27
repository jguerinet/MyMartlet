package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 11:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 * Represents the category of a place
 */

@JsonIgnoreProperties(ignoreUnknown =  true)
public class PlaceCategory{
    public static final String FAVORITES = "Favorites";
    public static final String ALL = "All";

    private String mName;
    private String mEnglishString;
    private String mFrenchString;

    public PlaceCategory(@JsonProperty("Name") String name,
                         @JsonProperty("En") String englishString,
                         @JsonProperty("Fr") String frenchString){
        this.mName = name;
        this.mEnglishString = englishString;
        this.mFrenchString = frenchString;
    }

    //For the Favorites and All Categories
    public PlaceCategory(boolean favorite){
        this.mName = favorite ? FAVORITES : ALL;
        this.mEnglishString = null;
        this.mFrenchString = null;
    }

    /* GETTERS */
    public String getName(){
        return this.mName;
    }

    /* HELPERS */
    @Override
    public String toString(){
        //If it's Favorites, then get the String from the Strings document
        if(mName.equals(FAVORITES)){
            return App.getContext().getString(R.string.map_favorites);
        }
        else if(mName.equals(ALL)){
            return App.getContext().getString(R.string.map_all);
        }

        if(App.getLanguage() == Language.FRENCH){
            return mFrenchString;
        }
        return mEnglishString;
    }

    public static List<PlaceCategory> getCategories(String[] categoryStrings){
        List<PlaceCategory> categories = new ArrayList<PlaceCategory>();
        //Go through the category Strings
        for(String category : categoryStrings){
            //Go through the place categories
            for(PlaceCategory placeCategory : App.getPlaceCategories()){
                //If a category String equals the place category's name, then add it and break the loop
                if(category.equals(placeCategory.getName())){
                    categories.add(placeCategory);
                    break;
                }
            }
        }

        return categories;
    }

    public static List<String> getCategories(Context context){
        List<String> categories = new ArrayList<String>();

        //Get the Strings of each of the categories
        for(PlaceCategory placeCategory : App.getPlaceCategories()){
            categories.add(placeCategory.toString());
        }

        //Sort them alphabetically
        Collections.sort(categories);

        //Add "All" at the top
        categories.add(0, context.getString(R.string.map_all));

        return categories;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof PlaceCategory)){
            return false;
        }

        PlaceCategory placeCategory = (PlaceCategory)object;

        return this.mName.equals(placeCategory.getName());
    }
}
