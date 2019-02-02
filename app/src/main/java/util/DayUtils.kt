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

package com.guerinet.mymartlet.util

import androidx.annotation.StringRes
import com.guerinet.mymartlet.R
import org.threeten.bp.DayOfWeek

/**
 * Utility methods for the days of the week
 * @author Julien Guerinet
 * @since 1.0.0
 */
object DayUtils {

    val days = listOf(
            Triple('M', DayOfWeek.MONDAY, R.string.monday),
            Triple('T', DayOfWeek.TUESDAY, R.string.tuesday),
            Triple('W', DayOfWeek.WEDNESDAY, R.string.wednesday),
            Triple('R', DayOfWeek.THURSDAY, R.string.thursday),
            Triple('F', DayOfWeek.FRIDAY, R.string.friday),
            Triple('S', DayOfWeek.SATURDAY, R.string.saturday),
            Triple('U', DayOfWeek.SUNDAY, R.string.sunday)
    )

    /**
     * Returns the day based on a [dayChar] (M, T, W, R, F, S, N). Characters taken from Minerva
     */
    fun getDay(dayChar: Char): DayOfWeek =
            days.find { it.first == dayChar }?.second
                    ?: throw IllegalStateException("Unknown day character: $dayChar")

    /**
     * Returns the day character for a [day]
     */
    fun getDayChar(day: DayOfWeek): Char =
            days.find { it.second == day }?.first
                    ?: throw IllegalStateException("Unknown day: $day")

    /**
     * Returns the String Id for this given day
     *
     * @param day Day
     * @return Corresponding String Id
     */
    @StringRes
    fun getStringId(day: DayOfWeek): Int =
            days.find { it.second == day }?.third
                    ?: throw IllegalStateException("Unknown day $day")

    /**
     * Returns a String representing all of the [days] by their character
     */
    fun getDayStrings(days: List<DayOfWeek>): String =
            days.joinToString("") { getDayChar(it).toString() }
}
