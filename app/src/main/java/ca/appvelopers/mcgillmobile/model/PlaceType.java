/*
 * Copyright 2014-2016 Appvelopers
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

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.manager.LanguageManager;

/**
 * A type of place that the user can filter by
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class PlaceType implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The user's saved favorite places
     */
    public static final int FAVORITES = -2;
    /**
     * All of the places
     */
    public static final int ALL = -1;
    /**
     * The type name
     */
    protected int id;
    /**
     * The type English String
     */
    protected String en;
    /**
     * The type French String
     */
    protected String fr;

    /**
     * Default Moshi Constructor
     */
    protected PlaceType() {}

    /**
     * Constructor used to create the Favorites and All types
     *
     * @param favorites True if this is the favorites type, false if this is the all type
     */
    public PlaceType(boolean favorites) {
        this.id = favorites ? FAVORITES : ALL;
        this.en = null;
        this.fr = null;
    }

    /* GETTERS */

    /**
     * @return Category Id
     */
    public int getId() {
        return id;
    }

    /* HELPERS */

    /**
     * TODO
     * Get the list of types based on a list of Strings
     *
     * @param typeStrings The list of Strings
     * @return The corresponding list of types
     */
    public static List<PlaceType> getTypes(String[] typeStrings) {
        List<PlaceType> types = new ArrayList<>();
        //Go through the type Strings
        for (String type : typeStrings) {
            //Go through the place types
            for (PlaceType placeType : App.getPlaceTypes()) {
                //If a type String equals the place type's name, then add it and break the loop
                if (type.equals(placeType.getId())) {
                    types.add(placeType);
                    break;
                }
            }
        }

        return types;
    }

    /**
     * @param context  App context
     * @param language The current language
     * @return The String to use
     */
    public String getString(Context context, @LanguageManager.Language int language) {
        if (id == FAVORITES) {
            return context.getString(R.string.map_favorites);
        } else if(id == ALL) {
            return context.getString(R.string.map_all);
        } else if (language == LanguageManager.FRENCH) {
            return fr;
        }
        return en;
    }


    /**
     * @param object The object to compare
     * @return True if they have the same name, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof PlaceType && ((PlaceType) object).id == this.id;
    }
}
