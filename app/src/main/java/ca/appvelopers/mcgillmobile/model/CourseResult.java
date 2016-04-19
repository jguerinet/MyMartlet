/*
 * Copyright 2014-2016 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.model;

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
public class CourseResult extends Course implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * The course total capacity (for registration)
     */
    private final int capacity;
    /**
     * The number of seats remaining (for registration)
     */
    private final int seatsRemaining;
    /**
     * The number of waitlist spots remaining
     */
    private final int waitlistRemaining;

    /**
     * Default Constructor
     *
     * @param term              Current {@link Term}
     * @param subject           The course subject
     * @param number            The course number
     * @param title             The course title
     * @param crn               The course CRN
     * @param section           The course section
     * @param startTime         The course's ending time
     * @param endTime           The course's starting time
     * @param days              The days this course is on
     * @param type              The course type
     * @param location          The course location
     * @param instructor        The course instructor
     * @param credits           The number of credits
     * @param startDate         THe course's start date
     * @param endDate           The course's end date
     * @param capacity          The course capacity
     * @param seatsRemaining    The number of seats remaining
     * @param waitlistRemaining The number of waitlist seats remaining
     */
    public CourseResult(Term term, String subject, String number, String title, int crn,
            String section, LocalTime startTime, LocalTime endTime, List<DayOfWeek> days,
            String type, String location, String instructor, double credits, LocalDate startDate,
            LocalDate endDate, int capacity, int seatsRemaining, int waitlistRemaining) {

        super(subject, number, title, crn, section, startTime, endTime, days, type, location,
                instructor, credits, startDate, endDate);

        setTerm(term);
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