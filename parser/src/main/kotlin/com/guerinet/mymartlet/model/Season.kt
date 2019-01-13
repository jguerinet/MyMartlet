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

/**
 * Different seasons a term can be in
 * @author Julien Guerinet
 * @since 1.0.0
 */
enum class Season(val title: String, val seasonNumber: String) {
    /**
     * January - April
     */
    WINTER("Winter", "01"),

    /**
     * May, June, July
     */
    SUMMER("Summer", "05"),

    /**
     * September - December
     */
    FALL("Fall", "09");

    /**
     * McGill season number
     */
    val number: Int = seasonNumber.toInt()

    companion object {

        /**
         * Returns the [Season] for the [title]
         */
        fun getSeasonFromTitle(title: String): Season =
            Season.values().firstOrNull {
                title.equals(it.title, ignoreCase = true)
            } ?: throw IllegalStateException("Unknown Season: $title")

        /**
         * Returns the [Season] for the [number]
         */
        fun getSeasonFromNumber(number: String): Season =
            Season.values().firstOrNull {
                number.equals(it.seasonNumber, ignoreCase = true)
            } ?: throw IllegalStateException("Unknown McGill Season: $number")
    }
}