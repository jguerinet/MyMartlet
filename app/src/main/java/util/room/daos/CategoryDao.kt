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

/**
 * Dao used to retrieve the data for the Map section
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
abstract class CategoryDao : BaseDao<Category>() {

    /**
     * Returns all of the [Category]s
     */
    @Query("SELECT * FROM Category")
    abstract fun getCategories(): List<Category>

    /**
     * Deletes all of the stored [Category]s
     */
    @Query("DELETE FROM Category")
    abstract fun deleteAll()

    /**
     * Updates the categories by deleting all old categories and inserting the new [categories]
     */
    @Transaction
    open fun update(categories: List<Category>) = update(categories, this::deleteAll)
}
