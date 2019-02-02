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

import androidx.room.Entity
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

/**
 * A course in the user's schedule or one that a user can register for
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 *
 * @param capacity          Course total capacity (for registration)
 * @param seatsRemaining    Number of seats remaining (for registration)
 * @param waitlistRemaining Number of waitlist spots remaining
 */
@Entity
class CourseResult(
    term: Term,
    subject: String,
    number: String,
    title: String,
    crn: Int,
    section: String,
    startTime: LocalTime,
    endTime: LocalTime,
    days: List<DayOfWeek>,
    type: String,
    location: String,
    instructor: String,
    credits: Double,
    startDate: LocalDate,
    endDate: LocalDate,
    val capacity: Int,
    val seatsRemaining: Int,
    val waitlistRemaining: Int
) : Course(
    term, subject, number, title, crn, section, startTime, endTime, days, type, location,
    instructor, credits, startDate, endDate
)