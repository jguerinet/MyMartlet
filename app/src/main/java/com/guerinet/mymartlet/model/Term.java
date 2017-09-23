/*
 * Copyright 2014-2017 Julien Guerinet
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

package com.guerinet.mymartlet.model;

import android.content.Context;
import android.support.annotation.NonNull;

import org.threeten.bp.LocalDate;

import java.io.Serializable;

/**
 * One class currentTerm, consisting of a season and a year
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Term implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Term season
     */
    protected @Season.Type String season;
    /**
     * Term year
     */
    protected int year;

    /**
     * Moshi Constructor
     */
    protected Term() {}

    /**
     * Default Constructor
     *
     * @param season Term season
     * @param year   Term year
     */
    public Term(@Season.Type String season, int year) {
        this.season = season;
        this.year = year;
    }

    /* GETTERS */

    /**
     * @return Term season
     */
    public @Season.Type String getSeason() {
        return season;
    }

    /**
     * @return Term year
     */
    public int getYear() {
        return year;
    }

    /* HELPERS */

    /**
     * Checks if the current currentTerm is after the given currentTerm
     *
     * @param term The currentTerm to compare
     * @return True if the current currentTerm is after the given currentTerm, false otherwise
     */
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean isAfter(Term term) {
        if (year > term.getYear()) {
            //Year after
            return true;
        } else if (year < term.getYear()) {
            //Year Before
            return false;
        } else {
            //Same year: check the semesters
            return Integer.valueOf(Season.getSeasonNumber(season)) >
                    Integer.valueOf(Season.getSeasonNumber(term.getSeason()));
        }
    }

    /**
     * @return Term Id, for parsing errors
     */
    public String getId() {
        return season + " " + year;
    }

    /**
     * @param context App context
     * @return String representation of the currentTerm
     */
    public String getString(Context context) {
        return Season.getString(context, season) + " " + year;
    }

    /**
     * @return The currentTerm in a format used by McGill
     */
    @Override
    public String toString() {
        return year + Season.getSeasonNumber(season);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Term)) {
            return false;
        }

        Term term = (Term) object;
        return season.equals(term.getSeason()) && year == term.getYear();
    }

    /* STATIC HELPERS */

    /**
     * @param term Term in String format (Ex: 199901 would be Winter 1999)
     * @return Corresponding {@link Term}
     */
    public static Term parseMcGillTerm(String term) {
        //Split it into the year and the season
        int year = Integer.parseInt(term.substring(0, 4));
        @Season.Type String season = Season.getMcGillSeason(term.substring(4));
        return new Term(season, year);
    }

    /**
     * Parses a currentTerm from a String
     *
     * @param term The currentTerm String
     * @return The parsed currentTerm
     */
    @NonNull
    public static Term parseTerm(String term) {
        String[] termParts = term.trim().split(" ");
        return new Term(Season.getSeason(termParts[0]), Integer.valueOf(termParts[1]));
    }

    /**
     * Gets the Term today falls in
     *
     * @return Today's corresponding currentTerm
     */
    @NonNull
    public static Term currentTerm() {
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int year = today.getYear();

        if (month >= 9 && month <= 12) {
            return new Term(Season.FALL, year);
        } else if (month >= 1 && month <= 4) {
            return new Term(Season.WINTER, year);
        } else {
            return new Term(Season.SUMMER, year);
        }
    }
}
