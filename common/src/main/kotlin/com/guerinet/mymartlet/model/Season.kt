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

/**
 * Different seasons a term can be in
 * @author Julien Guerinet
 * @since 1.0.0
 */
enum class Season(val title: String, val number: String) {

    /** January, February, March, April */
    WINTER("Winter", "01"),

    /** May, June, July */
    SUMMER("Summer", "05"),

    /** September, October, November, December */
    FALL("Fall", "09");

    companion object {

        /**
         * Returns the [Season] for the [title], or null if none exists
         */
        fun getSeasonFromTitle(title: String): Season? = values().firstOrNull {
            title.equals(it.title, ignoreCase = true)
        }

        /**
         * Returns the [Season] for the [number], or null if none exists
         */
        fun getSeasonFromNumber(number: String): Season? = values().firstOrNull {
            number.equals(it.number, ignoreCase = true)
        }
    }
}
