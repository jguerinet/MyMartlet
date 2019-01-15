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

package com.guerinet.mymartlet.parser

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Attempts to parse any [LocalTime] within the provided string
 *
 * @author Allan Wang
 * @since 2.3.2
 */
internal fun String.parseTime(): LocalTime? {
    val matches = REGEX_TIME.find(this)?.groupValues ?: return null
    var hour = matches[1].toInt()
    val minute = matches[2].toInt()
    val pm = matches[3] == "pm"
    // If it's PM, then add 12 hours to the hours for 24 hours format
    // Make sure it isn't noon
    // We can ignore midnight as there are no classes then
    if (pm && hour != 12) {
        hour += 12
    }
    return LocalTime.of(hour, minute)
}

/**
 * Parses a String into a LocalDate object
 *
 * TODO should just use a regex and return null if not found?
 *
 * @author Allan Wang
 * @since 2.3.2
 *
 * @param year The current year
 * @return The corresponding local date
 */
fun String.parseDate(year: Int): LocalDate {
    val (month, day) = split("/")
    return LocalDate.of(
        year, month.toInt(),
        day.toInt()
    )
}

/**
 * Parses the date range String into 2 dates
 *
 * @author Allan Wang
 * @since 2.3.2
 *
 * @param year The current year
 * @return A pair representing the starting and ending dates of the range
 * @throws IllegalArgumentException
 */
fun String.parseDateRange(year: Int): Pair<LocalDate, LocalDate> {
    //Split the range into the 2 date Strings
    val dates = split("-")
    val startDate = dates[0].trim()
    val endDate = dates[1].trim()

    //Parse the dates, return them as a pair
    return startDate.parseDate(year) to endDate.parseDate(year)
}

/**
 * Object mapping [DayOfWeek] to minerva character keys
 *
 * @author Allan Wang
 * @since 2.3.2
 */
object DayUtils {

    // TODO verify, docs from original dayutils mentions N as well for sunday
    val days = listOf(
        'M' to DayOfWeek.MONDAY,
        'T' to DayOfWeek.TUESDAY,
        'W' to DayOfWeek.WEDNESDAY,
        'R' to DayOfWeek.THURSDAY,
        'F' to DayOfWeek.FRIDAY,
        'S' to DayOfWeek.SATURDAY,
        'U' to DayOfWeek.SUNDAY
    )

    /**
     * Get day associated to character, or null otherwise
     */
    fun charToDay(dayChar: Char): DayOfWeek? =
        days.find { it.first == dayChar }?.second

    /**
     * Get char associated with day
     */
    fun dayToChar(day: DayOfWeek): Char =
        days.first { it.second == day }.first

}