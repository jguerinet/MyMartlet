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

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint

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

    var coordinates: GeoPoint = GeoPoint(0.0, 0.0)

    companion object {

        /**
         * Converts a Firestore [document] into a [Place] (null if error during parsing)
         */
        fun fromDocument(document: DocumentSnapshot): Place? = document.toObject(Place::class.java)?.apply {
            id = document.id.toInt()
        }
    }
}
