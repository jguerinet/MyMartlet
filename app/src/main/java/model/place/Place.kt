/*
 * Copyright 2014-2019 Julien Guerinet
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

import com.google.firebase.firestore.GeoPoint
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.extensions.get
import com.guerinet.mymartlet.util.firestore
import com.guerinet.suitcase.coroutines.ioDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * A place on the campus map
 * @author Julien Guerinet
 * @since 1.0.0
 */
data class Place(
    val id: Int,
    val name: String,
    val categories: List<Int>,
    val address: String,
    private val courseName: String,
    val coordinates: GeoPoint
) {

    /** Name of the place when listed under a course location, an empty String if equivalent to the [name]  */
    val coursePlaceName: String
        // If there is no override, simply use the name
        get() = courseName.takeIf { it.isNotEmpty() } ?: name

    companion object {

        /**
         * Loads the places from the Firestore
         */
        suspend fun loadPlaces(): List<Place> = withContext(ioDispatcher) {
            firestore.get(Constants.Firebase.PLACES) {
                val id = it.id.toInt()
                val name = it["name"] as? String
                @Suppress("UNCHECKED_CAST")
                val categories = it["categories"] as? List<Long>
                val address = it["address"] as? String
                val courseName = it["courseName"] as? String ?: ""
                val coordinates = it["coordinates"] as? GeoPoint

                if (name != null && categories != null && address != null && coordinates != null) {
                    // Note: not checking courseName here as it is an optional field, and if missing can be
                    //  safely cast to an empty String
                    Place(id, name, categories.map { category -> category.toInt() }, address, courseName, coordinates)
                } else {
                    Timber.tag("LoadPlaces").e(
                        Exception(
                            "Error parsing Place with id $id. name: $name, " +
                                "categories: $categories, address: $address, coordinates: $coordinates"
                        )
                    )
                    null
                }
            }
        }
    }
}
