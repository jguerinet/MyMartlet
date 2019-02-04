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
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.Term

/**
 * Dao for retrieving [CourseResult] related stuff
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Dao
abstract class CourseResultDao : BaseDao<CourseResult>() {

    /**
     * Returns all stored [CourseResult]s
     */
    @Query("SELECT * FROM CourseResult")
    abstract fun getAll(): List<CourseResult>

    /**
     * Returns all [CourseResult]s for the [term]
     */
    @Query("SELECT * FROM CourseResult WHERE term = :term")
    abstract fun get(term: Term): List<CourseResult>

    /**
     * Returns one [CourseResult] with the same [term] and [crn] if found
     */
    @Query("SELECT * FROM CourseResult WHERE term = :term AND crn = :crn")
    abstract fun get(term: Term, crn: Int): CourseResult?
}
