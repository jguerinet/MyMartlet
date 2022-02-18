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

import android.content.Context
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.extensions.get
import com.guerinet.mymartlet.util.firestore
import com.guerinet.suitcase.date.extensions.today
import kotlinx.datetime.LocalDate
import java.io.Serializable

/**
 * One class term, consisting of a season and a year
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @property season Term [Season]
 * @property year Term year
 */
@Suppress("EqualsOrHashCode")
class Term(val season: Season, val year: Int) : Serializable {

    /**
     * Term Id, for parsing errors
     */
    val id = "${season.title} $year"

    /**
     * Returns true if the current term if after the given [term], false otherwise
     */
    fun isAfter(term: Term): Boolean = when {
        // Year after
        year > term.year -> true
        // Year Before
        year < term.year -> false
        // Same year: check the semesters
        else -> season.number.toInt() > term.season.number.toInt()
    }

    /**
     * Returns the String representation of this term, using the [context]
     */
    fun getString(context: Context): String = "${season.getString(context)} $year"

    /**
     * Returns the term in the format used by McGill
     */
    override fun toString(): String = year.toString() + season.number

    override fun equals(other: Any?): Boolean {
        if (other !is Term) {
            return false
        }
        return season == other.season && year == other.year
    }

    companion object {

        /**
         * Returns the term from the [term] in the MGill String format (Ex: 199901 is Winter 1999)
         */
        fun parseMcGillTerm(term: String): Term {
            // Split it into the year and the season
            val year = Integer.parseInt(term.substring(0, 4))
            val season = Season.getSeasonFromNumber(term.substring(4))
            return Term(season, year)
        }

        /**
         * Returns a parsed term from the [term] String
         */
        fun parseTerm(term: String): Term {
            val termParts = term.trim().split(" ")
            return Term(Season.getSeasonFromTitle(termParts[0]), termParts[1].toInt())
        }

        /**
         * Returns today's corresponding term
         */
        fun currentTerm(): Term {
            val today = LocalDate.today
            val year = today.year

            return when (today.monthNumber) {
                in 9..12 -> Term(Season.FALL, year)
                in 1..4 -> Term(Season.WINTER, year)
                else -> Term(Season.SUMMER, year)
            }
        }

        /**
         * Loads the registration [Term]s from the Firestore
         */
        suspend fun loadRegistrationTerms(): List<Term> = firestore.get(Constants.Firebase.REGISTRATION_TERMS) {
            // Load and parse the season
            val season = try {
                Season.getSeasonFromTitle(it["season"] as? String)
            } catch (e: Exception) {
                null
            }

            val year = it["year"] as? Long

            // If the season or year is null, something went wrong during parsing so don't continue
            return@get if (season != null && year != null) Term(season, year.toInt()) else null
        }
    }
}
