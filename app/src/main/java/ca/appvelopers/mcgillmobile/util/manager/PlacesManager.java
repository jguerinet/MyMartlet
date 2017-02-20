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

package ca.appvelopers.mcgillmobile.util.manager;

import android.content.Context;

import com.guerinet.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.place.Category;
import ca.appvelopers.mcgillmobile.model.place.Place;

/**
 * Entirely manages the {@link Place}s and {@link Category}s lifecycles
 * @author Julien Guerinet
 * @since 2.2.0
 */
@SuppressWarnings("unchecked")
@Singleton
public class PlacesManager {
    /**
     * File names
     */
    private static final String FAVORITE_PLACES = "favorite_places";
    private static final String PLACE_TYPES = "place_types";
    /**
     * {@link Context} instance
     */
    private final Context context;
    /**
     * List of Ids of the favorite {@link Place}s
     */
    private List<Integer> favoritePlaceIds;
    /**
     * List of {@link Category}s
     */
    private List<Category> categories;

    /**
     * Default Injectable Constructor
     *
     * @param context App Context
     */
    @Inject
    protected PlacesManager(Context context) {
        this.context = context;
    }

    /* GETTERS */

    /**
     * @return List of Ids of the favorite {@link Place}s
     */
    public List<Integer> getFavoritePlaceIds() {
        //Load the favorite place Ids from internal storage if they have not been loaded already
        if (favoritePlaceIds == null) {
            favoritePlaceIds = (List<Integer>) StorageUtils.loadObject(context, FAVORITE_PLACES,
                    "Favorite Places");

            //If they are still null, use an empty list
            if (favoritePlaceIds == null) {
                favoritePlaceIds = new ArrayList<>();
            }
        }
        return favoritePlaceIds;
    }

    /**
     * @return List of {@link Category}s
     */
    public List<Category> getCategories() {
        //Load the place types if necessary
        if (categories == null) {
            categories = (List<Category>) StorageUtils.loadObject(context, PLACE_TYPES,
                    "Place Types");

            if (categories == null) {
                return new ArrayList<>();
            }
        }
        return categories;
    }

    /**
     * @param types List of {@link Category}s to save
     */
    public void setCategories(List<Category> types) {
        //Don't save a null object
        if (types == null) {
            return;
        }
        this.categories = types;
        StorageUtils.saveObject(context, types, PLACE_TYPES, "Place Types");
    }

    /**
     * @param place {@link Place} to save to the favorites
     */
    public void addFavorite(Place place) {
        if (!getFavoritePlaceIds().contains(place.getId())) {
            getFavoritePlaceIds().add(place.getId());
            StorageUtils.saveObject(context, getFavoritePlaceIds(), FAVORITE_PLACES,
                    "Favorite Places");
        }
    }

    /**
     * @param place {@link Place} to remove from the favorites
     */
    public void removeFavorite(Place place) {
        getFavoritePlaceIds().remove(Integer.valueOf(place.getId()));
        StorageUtils.saveObject(context, getFavoritePlaceIds(), FAVORITE_PLACES, "Favorite Places");
    }

    /**
     * @param place {@link Place} instance
     * @return True if the passed place is a favorite, false otherwise
     */
    public boolean isFavorite(Place place) {
        return getFavoritePlaceIds().contains(place.getId());
    }

    /**
     * Clears the stored favorite {@link Place}s
     */
    public void clearFavorites() {
        //Clear both the local instance and the stored one
        favoritePlaceIds = new ArrayList<>();
        context.deleteFile(FAVORITE_PLACES);
    }

    /**
     * Clears the stored {@link Place}s and {@link Category}s
     */
    public void clearPlaces() {
        categories = new ArrayList<>();
        context.deleteFile(PLACE_TYPES);
    }
}
