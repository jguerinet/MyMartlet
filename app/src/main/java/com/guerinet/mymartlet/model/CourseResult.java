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

import com.guerinet.mymartlet.util.dbflow.databases.WishlistDB;
import com.raizlabs.android.dbflow.annotation.Table;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.io.Serializable;
import java.util.List;

/**
 * A course in the user's schedule or one that a user can register for
 * @author Quang Dao
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Table(database = WishlistDB.class, allFields = true)
public class CourseResult extends Course implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The course total capacity (for registration)
     */
    int capacity;
    /**
     * The number of seats remaining (for registration)
     */
    int seatsRemaining;
    /**
     * The number of waitlist spots remaining
     */
    int waitlistRemaining;

    /**
     * DB Constructor
     */
    CourseResult() {}

    /**
     * Default Constructor
     *
     * @param term              Current {@link Term}
     * @param subject           Course subject
     * @param number            Course number
     * @param title             Course title
     * @param crn               Course CRN
     * @param section           Course section
     * @param startTime         Course's ending time
     * @param endTime           Course's starting time
     * @param days              Days this course is on
     * @param type              Course type
     * @param location          Course location
     * @param instructor        Course instructor
     * @param credits           Number of credits
     * @param startDate         Course's start date
     * @param endDate           Course's end date
     * @param capacity          Course capacity
     * @param seatsRemaining    Number of seats remaining
     * @param waitlistRemaining Number of waitlist seats remaining
     */
    public CourseResult(Term term, String subject, String number, String title, int crn,
            String section, LocalTime startTime, LocalTime endTime, List<DayOfWeek> days,
            String type, String location, String instructor, double credits, LocalDate startDate,
            LocalDate endDate, int capacity, int seatsRemaining, int waitlistRemaining) {
        super(subject, number, title, crn, section, startTime, endTime, days, type, location,
                instructor, credits, startDate, endDate);
        this.term = term;
        // Set the Id now
        prepareForDB();
        this.capacity = capacity;
        this.seatsRemaining = seatsRemaining;
        this.waitlistRemaining = waitlistRemaining;
    }

	/* GETTERS */

    /**
     * @return The number of spots remaining
     */
    public int getSeatsRemaining() {
        return seatsRemaining;
    }

    /**
     * @return The number of waitlist spots remaining
     */
    public int getWaitlistRemaining() {
        return waitlistRemaining;
    }
}