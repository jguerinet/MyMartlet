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
)

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