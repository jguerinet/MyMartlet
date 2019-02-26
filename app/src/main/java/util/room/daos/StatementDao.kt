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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guerinet.mymartlet.model.Statement

/**
 * Dao for the e-bill related objects
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
abstract class StatementDao : BaseDao<Statement>() {

    /**
     * Returns all of the [Statement]s in an observable format
     */
    @Query("SELECT * FROM Statement")
    abstract fun getAll(): LiveData<List<Statement>>

    /**
     * Deletes all of the stored [Statement]s
     */
    @Query("DELETE FROM Statement")
    abstract fun deleteAll()

    /**
     * Updates the list of [statements] locally stored
     */
    @Transaction
    open fun update(statements: List<Statement>) = update(statements, this::deleteAll)
}
