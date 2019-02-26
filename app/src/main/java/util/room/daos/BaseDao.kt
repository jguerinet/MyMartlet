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

package com.guerinet.mymartlet.util.room.daos

import androidx.room.Delete
import com.guerinet.room.dao.BaseDao

/**
 * Basic Dao functions
 * @author Julien Guerinet
 * @since 2.0.0
 */
abstract class BaseDao<T> : BaseDao<T> {

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
