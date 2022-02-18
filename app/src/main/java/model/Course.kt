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

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guerinet.suitcase.date.android.extensions.getMediumDateString
import com.guerinet.suitcase.util.Utils
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import timber.log.Timber

/**
 * A course in the user's schedule or one that a user can register for
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
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
 * @property id Unique Id for this course
 */
@Entity
open class Course(
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
    val endDate: LocalDate,
    @PrimaryKey var id: String = Utils.uuid()
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
        date in startDate..endDate && days.contains(date.dayOfWeek)
}
