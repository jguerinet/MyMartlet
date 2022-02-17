/*
 * Copyright 2014-2022 Julien Guerinet
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

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/**
 * A course in the user's schedule or one that a user can register for
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 *
 * Migrated since 2.3.2
 *
 * @property term Term this class is for
 * @property subject Course's 4-letter subject (ex: MATH)
 * @property number Course's number (ex: 263)
 * @property title Course title
 * @property crn Course CRN number
 * @property section Course section (ex: 001)
 * @property startTime Course's start time
 * @property endTime Course's end time
 * @property days Days this course is on
 * @property type Course type (ex: lecture, tutorial...)
 * @property location Course location (generally building and room number)
 * @property instructor Course's instructor's name
 * @property credits Number of credits for this course
 * @property startDate Course start date
 * @property endDate Course end date
 */
data class Course(
    val term: Term,
    val subject: String,
    val number: String,
    val title: String,
    val crn: Int,
    val section: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val days: List<DayOfWeek>,
    val type: String,
    val location: String,
    val instructor: String,
    val credits: Double,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    companion object {

        /** Start time that will yield 0 for the rounded start time*/
        val defaultStartTime = LocalTime(0, 5)

        /** End time that will yield 0 for the rounded end time */
        val defaultEndTime = LocalTime(0, 55)
    }
}
