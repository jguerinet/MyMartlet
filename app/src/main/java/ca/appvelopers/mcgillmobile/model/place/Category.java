/*
 * Copyright 2014-2017 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.model.place;

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.Locale;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.PlaceCategoriesDB;

/**
 * A type of place that the user can filter by
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Table(database = PlaceCategoriesDB.class, allFields = true)
public class Category extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The user's saved favorite places
     */
    public static final int FAVORITES = -2;
    /**
     * All of the places
     */
    static final int ALL = -1;
    /**
     * Category Id
     */
    @PrimaryKey
    int id;
    /**
     * Category name in English
     */
    String en;
    /**
     * Category name in French
     */
    String fr;

    /**
     * Default Moshi Constructor
     */
    Category() {}

    /**
     * Constructor used to create the Favorites and All types
     *
     * @param favorites True if this is the favorites type, false if this is the all type
     */
    public Category(boolean favorites) {
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
     * @param context  App context
     * @return String to use
     */
    public String getString(Context context) {
        if (id == FAVORITES) {
            return context.getString(R.string.map_favorites);
        } else if (id == ALL) {
            return context.getString(R.string.map_all);
        } else if (Locale.getDefault().getLanguage().equals(new Locale("fr").getLanguage())) {
            return fr;
        }
        return en;
    }

    /**
     * @param object object to compare
     * @return True if they have the same Id, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof Category && ((Category) object).id == this.id;
    }
}
