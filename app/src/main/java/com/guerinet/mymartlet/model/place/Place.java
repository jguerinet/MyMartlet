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

package com.guerinet.mymartlet.model.place;

import com.google.android.gms.maps.model.LatLng;
import com.guerinet.mymartlet.util.dbflow.databases.PlacesDB;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * A place on the campus map
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Table(database = PlacesDB.class, allFields = true)
public class Place extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Place Id
     */
    @PrimaryKey
    int id;
    /**
     * The place name
     */
    String name;
    /**
     * The place categories in CSV format
     */
    String categoriesList;
    /**
     * The address of this place
     */
    String address;
    /**
     * Name of the place when listed under a course location
     */
    String courseName;
    /**
     * The latitude coordinate of this place
     */
    double latitude;
    /**
     * The longitude coordinate of this place
     */
    double longitude;
    /**
     * List of categories
     */
    @ColumnIgnore
    private List<Integer> categories;

    /**
     * Default Moshi Constructor
     */
    Place() {}

    /* GETTERS */

    /**
     * @return Place Id
     */
    public int getId() {
        return id;
    }

    /**
     * @return The name of this place
     */
    public String getName() {
        return name;
    }

    /**
     * @return The address of this place
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return Name of the place when listed under a course location
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @return The place coordinates
     */
    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    /**
     * @return List of categories, which is loaded if it hasn't been done in the past
     */
    private List<Integer> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
            if (categoriesList != null) {
                for (String character : categoriesList.split(",")) {
                    try {
                        categories.add(Integer.valueOf(character));
                    } catch (Exception e) {
                        Timber.e(e, "Cannot convert into number: %s", character);
                    }
                }
            }
        }
        return categories;
    }

    /**
     * @return True if this place is in the user's favorites, false otherwise
     */
    public boolean isFavorite() {
        return getCategories().contains(Category.FAVORITES);
    }

    /* SETTERS */

    /**
     * @param favorite True if this place should be in the user's favorites, false otherwise
     */
    public void setFavorite(boolean favorite) {
        if (favorite && !getCategories().contains(Category.FAVORITES)) {
            // Place is now a favorite, add the Id to the list of categories
            getCategories().add(Category.FAVORITES);
        } else if (!favorite) {
            // Place is no longer a favorite, remove the Id from the list of categories
            getCategories().remove(Integer.valueOf(Category.FAVORITES));
        }
        // Save the object back
        save();
    }

    /* HELPERS */

    /**
     * @param category {@link Category} to check
     * @return True if the place is within the given category, false otherwise
     */
    public boolean isWithinCategory(Category category) {
        // Every place is in the all category
        return category.getId() == Category.ALL || getCategories().contains(category.getId());
    }

    @Override
    public boolean save() {
        // Create the categories String from the list
        categoriesList = "";
        for (int i = 0; i < categories.size(); i ++) {
            int category = categories.get(i);
            categoriesList += category;
            if (i != categories.size() - 1) {
                categoriesList += ",";
            }
        }

        return super.save();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Place && ((Place) object).getId() == id;
    }
}