/*
 * Copyright 2014-2015 Appvelopers Inc.
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

package ca.appvelopers.mcgillmobile.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

public class PlaceCategory implements Serializable{
    private static final long serialVersionUID = 1L;

    public static final String FAVORITES = "Favorites";
    public static final String ALL = "All";

    private String mName;
    private String mEnglishString;
    private String mFrenchString;

    public PlaceCategory(String name, String englishString, String frenchString){
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

    @Override
    public boolean equals(Object object){
        if(!(object instanceof PlaceCategory)){
            return false;
        }

        PlaceCategory placeCategory = (PlaceCategory)object;

        return this.mName.equals(placeCategory.getName());
    }
}
