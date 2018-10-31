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
import com.guerinet.mymartlet.model.Semester

/**
 * Dao for accessing the Semester model
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
abstract class SemesterDao : BaseDao<Semester>() {

    /**
     * Returns the [Semester] with the [semesterId]
     */
    @Query("SELECT * FROM Semester WHERE id = :semesterId")
    abstract fun get(semesterId: Int): LiveData<Semester>

    /**
     * Returns the list of all [Semester]s
     */
    @Query("SELECT * FROM Semester")
    abstract fun getAll(): LiveData<List<Semester>>

    /**
     * Deletes all of the stored [Semester]s
     */
    @Query("DELETE FROM Semester")
    abstract fun deleteAll()

    /**
     * Updates the stored [semesters]
     */
    @Transaction
    open fun update(semesters: List<Semester>) = update(semesters, this::deleteAll)
}