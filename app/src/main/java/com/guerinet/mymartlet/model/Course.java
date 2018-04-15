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

package com.guerinet.mymartlet.model;

import com.guerinet.mymartlet.util.DayUtils;
import com.guerinet.mymartlet.util.dbflow.databases.CourseDB;
import com.guerinet.suitcase.date.extensions.DateUtils;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * A course in the user's schedule or one that a user can register for
 * @author Quang Dao
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Table(database = CourseDB.class, allFields = true)
public class Course extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Unique Id for this course
     */
    @PrimaryKey
    String id;
    /**
     * The currentTerm this class is for
     */
    Term term;
    /**
     * The course's 4-letter subject (ex: MATH)
     */
    String subject;
    /**
     * The course's number (ex: 263)
     */
    String number;
    /**
     * The course title
     */
    String title;
    /**
     * The course CRN number
     */
    int crn;
    /**
     * The course section (ex: 001)
     */
    String section;
    /**
     * The course's start time
     */
    LocalTime startTime;
    /**
     * The course's end time
     */
    LocalTime endTime;
    /**
     * Days in the String format for the DB
     */
    String daysString;
    /**
     * The days this course is on
     */
    @ColumnIgnore
    private List<DayOfWeek> days;
    /**
     * The course type (ex: lecture, tutorial...)
     */
    String type;
    /**
     * The course location (generally building and room number)
     */
    String location;
    /**
     * The course's instructor's name
     */
    String instructor;
    /**
     * The number of credits for this course
     */
    double credits;
    /**
     * The course start date
     */
    LocalDate startDate;
    /**
     * The course end date
     */
    LocalDate endDate;

    /**
     * DB Constructor
     */
    Course() {}

    /**
     * Constructor used for the user's already registered classes
     *
     * @param subject    The course subject
     * @param number     The course number
     * @param title      The course title
     * @param crn        The course CRN
     * @param section    The course section
     * @param startTime  The course's ending time
     * @param endTime    The course's starting time
     * @param days       The days this course is on
     * @param type       The course type
     * @param location   The course location
     * @param instructor The course instructor
     * @param credits    The number of credits
     * @param startDate  The starting date for this course
     * @param endDate    The ending date for this course
     */
    public Course(String subject, String number, String title, int crn, String section,
            LocalTime startTime, LocalTime endTime, List<DayOfWeek> days, String type,
            String location, String instructor, double credits, LocalDate startDate,
            LocalDate endDate) {
        id = UUID.randomUUID().toString();
        this.subject = subject;
        this.number = number;
        this.title = title;
        this.crn = crn;
        this.section = section;
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
        this.type = type;
        this.location = location;
        this.instructor = instructor;
        this.credits = credits;
        this.startDate = startDate;
        this.endDate = endDate;
    }

	/* GETTERS */

    /**
     * @return The course currentTerm
     */
    public Term getTerm() {
        return term;
    }

    /**
     * @return The course code
     */
    public String getCode() {
        return subject + " " + number;
    }

    /**
     * @return The course subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return The course number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @return The course title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return The course CRN
     */
    public int getCRN() {
        return crn;
    }

    /**
     * @return The course section
     */
    public String getSection() {
        return section;
    }

    /**
     * @return The course start time
     */
    public LocalTime getStartTime() {
        return startTime;
    }


    /**
     * @return The course end time
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * @return The days this course is on
     */
    public List<DayOfWeek> getDays() {
        if (days == null) {
            days = new ArrayList<>();
            if (daysString != null) {
                for (char day : daysString.toCharArray()) {
                    days.add(DayUtils.getDay(day));
                }
            }
        }
        return days;
    }

    /**
     * @return The course type
     */
    public String getType() {
        return type;
    }

    /**
     * @return The course's location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return The course's instructor
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * @return The course credits
     */
    public double getCredits() {
        return credits;
    }

    /**
     * @return The starting date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @return The ending date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /* SETTERS */

    /**
     * @param term Course {@link Term}
     */
    public void setTerm(Term term) {
        this.term = term;
        prepareForDB();
    }

    /* HELPERS */

    /**
     * Returns the start time of the course, rounded off to the nearest half hour
     *
     * @return The course rounded start time
     */
    public LocalTime getRoundedStartTime() {
        //Check if the start time is already a half hour increment
        if (startTime.getMinute() == 0 || startTime.getMinute() == 30) {
            return startTime;
        }

        //If not, remove 5 minutes to the start time to get round numbers
        //  (McGill start times are always 5 minutes after the nearest half hour)
        return startTime.minusMinutes(5);
    }

    /**
     * Returns the end time of the course, rounded off the the nearest half hour
     *
     * @return The course rounded end time
     */
    public LocalTime getRoundedEndTime() {
        //Check if the end time is already a half hour increment
        if (endTime.getMinute() == 0 || endTime.getMinute() == 30) {
            return endTime;
        }

        //If not, add 5 minutes to the end time to get round numbers
        //  (McGill end times are always 5 minutes before the nearest half hour
        return endTime.plusMinutes(5);
    }

    /**
     * @param date The given date
     * @return True if the course if for the given date, false otherwise
     */
    public boolean isForDate(LocalDate date) {
        //Check if the date is within the date range and the course is offered on that day
        return !date.isBefore(startDate) && !date.isAfter(endDate) &&
                getDays().contains(date.getDayOfWeek());
    }

    /**
     * @return The course times in String format
     */
    public String getTimeString() {
        //No time associated, therefore no time string
        if (startTime.getHour() == 0 && startTime.getMinute() == 0) {
            Timber.i("No time associated when getting String");
            return "";
        }

        return DateUtils.getShortTimeString(startTime) + " - " +
                DateUtils.getShortTimeString(endTime);
    }

    /**
     * @return The course dates in String format
     */
    public String getDateString() {
        return DateUtils.getMediumDateString(startDate) + " - " +
                DateUtils.getMediumDateString(endDate);
    }

    /**
     * Prepares the object for the DB
     */
    void prepareForDB() {
        // Create the day String from the days
        daysString = DayUtils.getDayStrings(getDays());
    }

    @Override
    public boolean save() {
        prepareForDB();
        return super.save();
    }

    /**
     * 2 ClassItems are equal if they have the same CRN and are for the same currentTerm
     *
     * @param object The course to check
     * @return True if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Course)) {
            return false;
        }
        Course course = (Course) object;
        return course.term.equals(term) && course.crn == crn &&
                DayUtils.getDayStrings(course.getDays()).equals(DayUtils.getDayStrings(getDays()))
                && course.getLocation().equals(location) && course.startDate.equals(startDate) &&
                course.endDate.equals(endDate);
    }
}