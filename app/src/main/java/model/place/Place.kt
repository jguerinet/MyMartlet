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

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

/**
 * A place on the campus map
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @property id Place Id
 * @property name Place name
 * @property categories List of categories
 * @property address Address of this place
 * @property courseName Name of the place when listed under a course location
 * @property latitude Latitude coordinate of this place
 * @property longitude Longitude coordinate of this place
 */
@Entity
data class Place(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val categories: MutableList<Int>,
    val address: String,
    val courseName: String?,
    val latitude: Double,
    val longitude: Double
) {

    /** Place coordinates */
    @Ignore
    val coordinates = LatLng(latitude, longitude)
}
