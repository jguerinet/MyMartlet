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
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.model.place.Place

/**
 * Dao used to retrieve the data for the Map section
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
interface MapDao {

    /**
     * Returns all of the places as an observable
     */
    @Query("SELECT * FROM Place")
    fun getPlaces(): List<Place>

    /**
     * Returns all of the categories as an observable
     */
    @Query("SELECT * FROM Category")
    fun getCategories(): List<Category>
}