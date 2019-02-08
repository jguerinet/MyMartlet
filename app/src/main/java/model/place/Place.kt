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

/**
 * A place on the campus map
 * @author Julien Guerinet
 * @since 1.0.0
 */
class Place {

    var id: Int = 0

    var name: String = ""

    var categories: List<Int> = listOf()

    var address: String = ""

    /** Name of the place when listed under a course location, an empty String if equivalent to the [name]  */
    var courseName: String = ""
        get() {
            // If there is no override, simply use the name
            return if (field.isEmpty()) name else field
        }

    var coordinates: GeoPoint = GeoPoint(0.0, 0.0)

    companion object {

        /**
         * Loads the places from the Firestore
         */
        suspend fun loadPlaces(): List<Place> = firestore.get(Constants.Firebase.PLACES) {
            it.toObject(Place::class.java)?.apply {
                id = it.id.toInt()
            }
        }
    }
}
