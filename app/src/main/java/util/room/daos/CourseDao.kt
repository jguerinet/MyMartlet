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
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.Term

/**
 * Dao for accessing [Course] related info
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
abstract class CourseDao : BaseDao<Course>() {

    /**
     * Deletes all of the stored [Course]s
     */
    @Query("DELETE FROM Course")
    abstract fun deleteAll()

    /**
     * Updates the stored [courses]
     */
    @Transaction
    open fun update(courses: List<Course>, term: Term) {
        // Set the term on the list of courses
        courses.forEach { it.term = term }

        update(courses, this::deleteAll)
    }
}