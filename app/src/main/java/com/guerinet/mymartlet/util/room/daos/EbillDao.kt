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

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.guerinet.mymartlet.model.Statement

/**
 * Dao for the e-bill related objects
 * @author Julien Guerinet
 * @since 2.0.0
 */
interface EbillDao : BaseDao {

    /**
     * Returns all of the [Statement]s in an observable format
     */
    @Query("SELECT * FROM Statement")
    fun getStatements(): LiveData<List<Statement>>

    /**
     * Deletes all of the stored [Statement]s
     */
    @Query("DELETE FROM Statement")
    fun deleteStatements()

    /**
     * Inserts the list of [statements]
     */
    @Insert
    fun insertStatements(statements: List<Statement>)

    /**
     * Updates the list of [statements] locally stored
     */
    fun updateStatements(statements: List<Statement>) =
            update(statements, this::deleteStatements, this::insertStatements)
}