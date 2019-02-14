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

import org.threeten.bp.LocalDate
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalTime

/**
 * A course in the user's schedule or one that a user can register for
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 *
 * Migrated since 2.3.2
 *
 * @param term Term this class is for
 * @param subject Course's 4-letter subject (ex: MATH)
 * @param number Course's number (ex: 263)
 * @param title Course title
 * @param crn Course CRN number
 * @param section Course section (ex: 001)
 * @param startTime Course's start time
 * @param endTime Course's end time
 * @param days Days this course is on
 * @param type Course type (ex: lecture, tutorial...)
 * @param location Course location (generally building and room number)
 * @param instructor Course's instructor's name
 * @param credits Number of credits for this course
 * @param startDate Course start date
 * @param endDate Course end date
 */
data class Course(
    var term: Term,
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
        /**
         * @return A start time that will yield 0 for the rounded start time
         */
        val defaultStartTime = LocalTime.of(0, 5)

        /**
         * @return An end time that will yield 0 for the rounded end time
         */
        val defaultEndTime = LocalTime.of(0, 55)
    }
}
