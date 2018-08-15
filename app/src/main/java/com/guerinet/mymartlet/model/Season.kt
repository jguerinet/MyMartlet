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

import android.content.Context

import com.guerinet.mymartlet.R

/**
 * Different seasons a term can be in
 * @author Julien Guerinet
 * @since 1.0.0
 */
enum class Season(val title: String) {

    /**
     * September - December
     */
    FALL("Fall"),

    /**
     * January - April
     */
    WINTER("Winter"),

    /**
     * May, June, July
     */
    SUMMER("Summer");

    /**
     * McGill season number
     */
    val number: String
        get() = when (this) {
            FALL -> "09"
            WINTER -> "01"
            SUMMER -> "05"
        }

    /**
     * Returns the locale-based String representation of this season, using the [context]
     */
    fun getString(context: Context): String = when (this) {
        FALL -> context.getString(R.string.fall)
        WINTER -> context.getString(R.string.winter)
        SUMMER -> context.getString(R.string.summer)
    }

    companion object {

        /**
         * Returns the [Season] for the [title]
         */
        fun getSeasonFromTitle(title: String): Season = when {
            title.equals(Season.FALL.title, ignoreCase = true) -> Season.FALL
            title.equals(Season.WINTER.title, ignoreCase = true) -> Season.WINTER
            title.equals(Season.SUMMER.title, ignoreCase = true) -> Season.SUMMER
            else -> throw IllegalStateException("Unknown Season: $title")
        }

        /**
         * Returns the [Season] for the [number]
         */
        fun getSeasonFromNumber(number: String): Season = when (number) {
            "09" -> Season.FALL
            "01" -> Season.WINTER
            "05" -> Season.SUMMER
            else -> throw IllegalStateException("Unknown McGill Season: $number")
        }
    }
}