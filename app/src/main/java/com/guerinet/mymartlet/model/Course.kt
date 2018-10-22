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

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.guerinet.suitcase.date.extensions.getMediumDateString
import com.guerinet.suitcase.date.extensions.getShortTimeString
import com.guerinet.suitcase.util.Utils
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import timber.log.Timber

/**
 * A course in the user's schedule or one that a user can register for
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 *
 * @param id    Unique Id for this course
 * @param term  The currentTerm this class is for
 * @param subject   The course's 4-letter subject (ex: MATH)
 * @param number    The course's number (ex: 263)
 * @param title The course title
 * @param crn   The course CRN number
 * @param section   The course section (ex: 001)
 * @param startTime The course's start time
 * @param endTime   The course's end time
 * @param days  The days this course is on
 * @param type  The course type (ex: lecture, tutorial...)
 * @param location  The course location (generally building and room number)
 * @param instructor The course's instructor's name
 * @param credits   The number of credits for this course
 * @param startDate The course start date
 * @param endDate The course end date
 */
@Entity
open class Course(
        @PrimaryKey var id: String = Utils.uuid(),
        val term: Term,
        val subject: String,
        val number: String,
        val title: String,
        val crn: Int = 0,
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

    /** Course code */
    val code: String
        get() = "$subject $number"

    /** Start time of the course, rounded off to the nearest half hour */
    val roundedStartTime: LocalTime
        // Check if the start time is already a half hour increment
        //  If not, remove 5 minutes to the start time to get round numbers
        //  (McGill start times are always 5 minutes after the nearest half hour)
        get() = if (startTime.minute == 0 || startTime.minute == 30) {
            startTime
        } else startTime.minusMinutes(5)

    /** End time of the course, rounded off the the nearest half hour */
    val roundedEndTime: LocalTime
        // Check if the end time is already a half hour increment
        // If not, add 5 minutes to the end time to get round numbers
        //  (McGill end times are always 5 minutes before the nearest half hour
        get() = if (endTime.minute == 0 || endTime.minute == 30) {
            endTime
        } else endTime.plusMinutes(5)

    /** Course times in String format */
    val timeString: String
        get() {
            if (startTime.hour == 0 && startTime.minute == 0) {
                Timber.i("No time associated when getting String")
                return ""
            }
            return "${startTime.getShortTimeString()} - ${endTime.getShortTimeString()}"
        }

    /** Course dates in String format */
    val dateString: String
        get() = "${startDate.getMediumDateString()} - ${endDate.getMediumDateString()}"

    /**
     * Returns true if the course is for the [date], false otherwise
     *  If the date is within the date range and that the course is offered on that day
     */
    fun isForDate(date: LocalDate): Boolean =
            !date.isBefore(startDate) && !date.isAfter(endDate) && days.contains(date.dayOfWeek)
}