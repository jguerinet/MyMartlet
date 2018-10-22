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

package com.guerinet.mymartlet.model.place

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

/**
 * A place on the campus map
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param id            Place Id
 * @param name          Place name
 * @param categories    List of categories
 * @param address       Address of this place
 * @param courseName    Name of the place when listed under a course location
 * @param latitude      Latitude coordinate of this place
 * @param longitude     Longitude coordinate of this place
 */
@Entity
data class Place(
        @PrimaryKey val id: Int = 0,
        val name: String,
        val categories: MutableList<Int>,
        val address: String,
        val courseName: String,
        val latitude: Double,
        val longitude: Double
) {

    /** Place coordinates */
    @Ignore
    val coordinates = LatLng(latitude, longitude)

    /** True if this place is in the user's favorites, false otherwise */
    // Place is now a favorite, add the Id to the list of categories
    // Place is no longer a favorite, remove the Id from the list of categories
    // Save the object back
    var isFavorite: Boolean
        get() = categories.contains(Category.FAVORITES)
        set(value) {
            if (value && !categories.contains(Category.FAVORITES)) {
                categories.add(Category.FAVORITES)
            } else if (!value) {
                categories.remove(Category.FAVORITES)
            }
        }

    /**
     * Returns True if the place is within the [category], false otherwise
     *  Note: every place is within [Category.ALL]
     */
    fun isWithinCategory(category: Category): Boolean =
            category.id == Category.ALL || categories.contains(category.id)
}
