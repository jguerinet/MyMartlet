/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.util.room.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.model.place.Place

/**
 * Dao used to retrieve the data for the Map section
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
abstract class PlaceDao : BaseDao<Place>() {

    /**
     * Returns all of the places as an observable
     */
    @Query("SELECT * FROM Place")
    abstract fun getPlaces(): List<Place>

    @Query("SELECT id FROM Place WHERE ${Category.FAVORITES} IN(categories)")
    abstract fun getFavoritePlaces(): List<Int>

    @Query("DELETE FROM Place WHERE Place.id NOT IN(:ids)")
    abstract fun deletePlaces(ids: List<Int>)

    /**
     * Updates the list of [Place]s by inserting the [places] and deleting the old ones
     */
    @Transaction
    fun updatePlaces(places: List<Place>) {
        // Get the list of Ids
        val placeIds = places.map { it.id }

        // Get the list of favorites
        val favorites = getFavoritePlaces()

        // Set the favorites boolean
        places.filter { favorites.contains(it.id) }.forEach { it.isFavorite = true }

        // Insert the new places
        insert(places)

        // Delete the ones that have not been updated
        deletePlaces(placeIds)
    }
}