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

package com.guerinet.mymartlet.data

import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.retrofit.McGillService
import com.guerinet.mymartlet.util.room.daos.CourseDao
import com.guerinet.suitcase.coroutines.bgDispatcher
import com.guerinet.suitcase.coroutines.ioDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Getters for the list of [Course]
 * @author Julien Guerinet
 * @since 2.0.0
 */
class CourseRepository(private val mcGillService: McGillService, private val courseDao: CourseDao) {

    fun getCourses() = courseDao.getCourses()

    /**
     * Refreshes the list of [Course]s for this [term]
     */
    suspend fun refreshCourses(term: Term) = withContext(bgDispatcher) {
        try {
            val courses = mcGillService.schedule(term).await()
            withContext(ioDispatcher) {
                courseDao.update(courses, term)
            }
        } catch (e: Exception) {
            // TODO Deal propagating errors back
            Timber.tag("CourseRepository").e(e, "Error refreshing courses")
        }
    }
}