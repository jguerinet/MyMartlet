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

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

/**
 * Basic Dao functions
 * @author Julien Guerinet
 * @since 2.0.0
 */
abstract class BaseDao<T> {

    /**
     * Inserts 1 [obj] into the database
     */
    @Insert
    abstract fun insert(obj: T)

    /**
     * Inserts a list of [objects] into the database
     */
    @Insert
    abstract fun insert(objects: List<T>)

    /**
     * Updates the [obj]
     */
    @Update
    abstract fun update(obj: T)

    /**
     * Deletes the [obj]
     */
    @Delete
    abstract fun delete(obj: T)

    /**
     * Updates a [list] of objects by [delete]ing the old objects and [insert]ing the new ones
     */
    fun update(list: List<T>, delete: () -> Unit) {
        // Delete all the old objects
        delete()
        // Insert all the new objects
        insert(list)
    }
}