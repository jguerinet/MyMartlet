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

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime

/**
 * Representation of a timezone independent time (does not yet exist in Kotlinx date-time)
 *  https://github.com/Kotlin/kotlinx-datetime/issues/57
 * @author Julien Guerinet
 * @since 3.0.0
 */
data class LocalTime(
    val hour: Int,
    val minute: Int,
    val second: Int = 0,
    val nanosecond: Int = 0,
) {

    override fun toString(): String = "$hour:$minute:$second:$nanosecond"

    fun plusMinutes(minutes: Int): LocalTime = if (minute + minutes >= 60) {
        LocalTime(hour + 1, minute + minutes % 60, second, nanosecond)
    } else {
        LocalTime(hour, minute + minutes, second, nanosecond)
    }

    fun minusMinutes(minutes: Int): LocalTime = if (minute - minutes < 0) {
        LocalTime(hour - 1, 60 - minute - minutes, second, nanosecond)
    } else {
        LocalTime(hour, minute - minutes, second, nanosecond)
    }

    fun getShortTimeString() = "$hour:$minute"

    companion object {

        /**
         * Returns a parsed [LocalTime] from a given [string]
         */
        fun parse(string: String): LocalTime {
            val components = string
                .split(":")
                .mapNotNull { it.toIntOrNull() }
                .toMutableList()
            // Add 4 0s in case a number is missing
            components.addAll(listOf(0, 0, 0, 0))

            return LocalTime(components[0], components[1], components[2], components[3])
        }
    }
}

val LocalDateTime.time
    get() = LocalTime(
        hour = hour,
        minute = minute,
        second = second,
        nanosecond = nanosecond,
    )

fun LocalDate.atTime(localTime: LocalTime) = atTime(
    hour = localTime.hour,
    minute = localTime.minute,
    second = localTime.second,
    nanosecond = localTime.nanosecond,
)