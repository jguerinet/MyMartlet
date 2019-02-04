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

package com.guerinet.mymartlet.model

import timber.log.Timber

/**
 * Models an error that occurred during registration
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @property crn Course CRN with the error
 * @property message Error message
 */
class RegistrationError(
    private val crn: Int,
    private val message: String
) {

    /**
     * Returns the String to show to the user explaining the error message. We first look for the
     *  course this is for from the list of [courses]
     */
    fun getString(courses: List<Course>): String {
        val course = courses.firstOrNull { it.crn == crn }
        if (course == null) {
            // If the course is null, don't continue
            Timber.e(IllegalStateException("No course for the registration error"))
            return ""
        }
        return "${course.code} (${course.type}) - $message"
    }
}
