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

package com.guerinet.mymartlet.parser

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.util.Locale

/**
 * Attempts to parse any [LocalTime] within the provided string
 *
 * @author Allan Wang
 * @since 2.3.2
 */
internal fun String.parseTime(): LocalTime? {
    val matches = REGEX_TIME.find(trim())?.groupValues ?: return null
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

private val dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.CANADA)

/**
 * Attempt to parse date of the format Jan 01, 2019
 */
fun String.parseDateAbbrev(): LocalDate? = try {
    LocalDate.parse(trim(), dtf)
} catch (ignore: DateTimeParseException) {
    null
}

/**
 * Parses a String into a LocalDate object.
 * String should be of the format MM/DD
 *
 * @author Allan Wang
 * @since 2.3.2
 *
 * @param year The current year
 * @return The corresponding local date
 */
fun String.parseDateMD(year: Int): LocalDate? {
    val matches = REGEX_DATE_MD.find(trim())?.groupValues ?: return null
    val month = matches[1].toInt()
    val day = matches[2].toInt()
    return LocalDate.of(year, month, day)
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
fun String.parseDateMDRange(year: Int): Pair<LocalDate, LocalDate>? {
    // Split the range into the 2 date Strings
    val dates = split("-")
    val startDate = dates[0].parseDateMD(year) ?: return null
    val endDate = dates[1].parseDateMD(year) ?: return null
    // Parse the dates, return them as a pair
    return startDate to endDate
}

/**
 * Object mapping [DayOfWeek] to minerva character keys
 *
 * @author Allan Wang
 * @since 2.3.2
 */
object DayUtils {

    // TODO verify, docs from original dayutils mentions N as well for sunday
    private val days = listOf(
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
