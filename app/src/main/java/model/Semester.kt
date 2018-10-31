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

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Contains information pertaining to each semester such as current program, term credits,
 *  term GPA, and full time status
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param id            Unique semester Id
 * @param term          Semester term
 * @param program       User's program for this semester
 * @param bachelor      User's bachelor's name for this semester
 * @param credits       Number of credits for this semester
 * @param gpa           Semester GPA
 * @param isFullTime    True if the user was full time during this semester, false otherwise
 */
@Entity
data class Semester(
    @PrimaryKey val id: Int,
    val term: Term,
    val program: String,
    val bachelor: String,
    val credits: Double,
    val gpa: Double,
    val isFullTime: Boolean
) {

    /**
     * Returns the semester's name (using the [context])
     */
    fun getName(context: Context): String = term.getString(context)
}
