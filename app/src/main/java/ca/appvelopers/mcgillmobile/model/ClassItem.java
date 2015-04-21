/*
 * Copyright 2014-2015 Appvelopers
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

import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

import ca.appvelopers.mcgillmobile.util.Date;

/**
 * Represents one class item in the user's schedule or one that a user can register for
 * @author Quang Dao
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class ClassItem implements Serializable{
    private static final String TAG = "ClassItem";
    private static final long serialVersionUID = 1L;
    /**
     * The term this class is for
     */
    private Term mTerm;
    /**
     * The course's 4-letter subject (ex: MATH)
     */
    private String mSubject;
    /**
     * The course's number (ex: 263)
     */
    private String mNumber;
    /**
     * The course title
     */
    private String mTitle;
    /**
     * The course CRN number
     */
    private int mCRN;
    /**
     * The course section (ex: 001)
     */
    private String mSection;
    /**
     * The course's start time
     */
    private LocalTime mStartTime;
    /**
     * The course's end time
     */
    private LocalTime mEndTime;
    /**
     * The days this course is on
     */
    private List<Day> mDays;
    /**
     * The course type (ex: lecture, tutorial...)
     */
    private String mType;
    /**
     * The course location (generally building and room number)
     */
    private String mLocation;
    /**
     * The course's instructor's name
     */
    private String mInstructor;
    /**
     * The course total capacity (for registration)
     */
    private int mCapacity;
    /**
     * The number of seats available (for registration)
     */
    private int mSeatsAvailable;
    /**
     * The number of seats remaining (for registration)
     */
    private int mSeatsRemaining;
    /**
     * The total number of waitlist spots
     */
    private int mWaitlistCapacity;
    /**
     * The number of waitlist spots available
     */
    private int mWaitlistAvailable;
    /**
     * The number of waitlist spots remaining
     */
    private int mWaitlistRemaining;
    /**
     * The number of credits for this course
     */
    private double mCredits;
    /**
     * The course start date
     */
    private LocalDate mStartDate;
    /**
     * The course end date
     */
    private LocalDate mEndDate;

    /**
     * Constructor used for the user's already registered classes
     *
     * @param term       The term that this class is for
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
    public ClassItem(Term term, String subject, String number, String title, int crn,
                     String section, LocalTime startTime, LocalTime endTime, List<Day> days,
                     String type, String location, String instructor, double credits,
                     LocalDate startDate, LocalDate endDate){
        this.mTerm = term;
        this.mNumber = number;
        this.mSubject = subject;
        this.mNumber = number;
        this.mTitle = title;
        this.mCRN = crn;
        this.mSection = section;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mDays = days;
        this.mType = type;
        this.mLocation = location;
        this.mInstructor = instructor;
        this.mCredits = credits;
        this.mStartDate = startDate;
        this.mEndDate = endDate;

        //These fields are not needed (they are used for the search results)
        this.mCapacity = -1;
        this.mSeatsAvailable = -1;
        this.mSeatsRemaining = -1;
        this.mWaitlistCapacity = -1;
        this.mWaitlistAvailable = -1;
        this.mWaitlistRemaining = -1;
    }

    /**
     * Constructor for course search results
     *
     * @param term              The term that this class is for
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
     * @param seatsAvailable    The number of seats available
     * @param seatsRemaining    The number of seats remaining
     * @param waitlistCapacity  The waitlist capacity
     * @param waitlistAvailable The number of waitlist seats available
     * @param waitlistRemaining The number of waitlist seats remaining
     */
    public ClassItem(Term term, String subject, String number, String title, int crn,
                     String section, LocalTime startTime, LocalTime endTime, List<Day> days,
                     String type, String location, String instructor, double credits,
                     LocalDate startDate, LocalDate endDate, int capacity, int seatsAvailable,
                     int seatsRemaining, int waitlistCapacity, int waitlistAvailable,
                     int waitlistRemaining){
        this(term, subject, number, title, crn, section, startTime, endTime, days, type, location,
                instructor, credits, startDate, endDate);

        this.mCapacity = capacity;
        this.mSeatsAvailable = seatsAvailable;
        this.mSeatsRemaining = seatsRemaining;
        this.mWaitlistCapacity = waitlistCapacity;
        this.mWaitlistAvailable = waitlistAvailable;
        this.mWaitlistRemaining = waitlistRemaining;
    }

	/* GETTERS */

    /**
     * @return The course term
     */
    public Term getTerm(){
        return this.mTerm;
    }

    /**
     * @return The course code
     */
    public String getCode(){
        return this.mSubject + " " + this.mNumber;
    }

    /**
     * @return The course subject
     */
    public String getSubject(){
        return this.mSubject;
    }

    /**
     * @return The course number
     */
    public String getNumber(){
        return this.mNumber;
    }

    /**
     * @return The course title
     */
    public String getTitle() {
        return this.mTitle;
    }

    /**
     * @return The course CRN
     */
    public int getCRN(){
        return this.mCRN;
    }

    /**
     * @return The course section
     */
    public String getSection(){
        return this.mSection;
    }

    /**
     * @return The course start time
     */
    public LocalTime getStartTime(){
        return this.mStartTime;
    }


    /**
     * @return The course end time
     */
    public LocalTime getEndTime(){
        return this.mEndTime;
    }

    /**
     * @return The days this course is on
     */
    public List<Day> getDays(){
        return this.mDays;
    }

    /**
     * @return The course type
     */
    public String getType(){
        return this.mType;
    }

    /**
     * @return The course's location
     */
    public String getLocation(){
        return this.mLocation;
    }

    /**
     * @return The course's instructor
     */
    public String getInstructor(){
        return this.mInstructor;
    }

    /**
     * @return The course credits
     */
    public double getCredits(){
        return this.mCredits;
    }

    /**
     * @return The number of spots remaining
     */
    public int getSeatsRemaining(){
        return this.mSeatsRemaining;
    }

    /**
     * @return The number of waitlist spots remaining
     */
    public int getWaitlistRemaining(){
        return this.mWaitlistRemaining;
    }

    /**
     * @return The starting date
     */
    public LocalDate getStartDate(){
        return this.mStartDate;
    }

    /**
     * @return The ending date
     */
    public LocalDate getEndDate(){
        return this.mEndDate;
    }

    /* SETTERS */

    /**
     * @param time The start time of the course
     */
    public void setStartTime(LocalTime time){
        this.mStartTime = time;
    }

    /**
     * @param time The end time of the course
     */
    public void setEndTime(LocalTime time){
        this.mEndTime = time;
    }

    /**
     * @param days The days this course is on
     */
    public void setDays(List<Day> days){
        this.mDays = days;
    }

    /**
     * @param location The course's location
     */
    public void setLocation(String location){
        this.mLocation = location;
    }

    /**
     * @param instructor The instructor for this course
     */
    public void setInstructor(String instructor){
        this.mInstructor = instructor;
    }

    /**
     * @param seatsRemaining The number of spots remaining
     */
    public void setSeatsRemaining(int seatsRemaining){
        this.mSeatsRemaining = seatsRemaining;
    }

    /**
     * @param waitlistRemaining The number of waitlist spots remaining
     */
    public void setWaitlistRemaining(int waitlistRemaining){
        this.mWaitlistRemaining = waitlistRemaining;
    }

    /* HELPERS */

    /**
     * Get the start time of the course, rounded off to the nearest half hour
     *
     * @return The course rounded start time
     */
    public LocalTime getRoundedStartTime(){
        //Check if the start time is already a half hour increment
        if(mStartTime.getMinuteOfHour() == 0 || mStartTime.getMinuteOfHour() == 30){
            return mStartTime;
        }

        //If not, remove 5 minutes to the start time to get round numbers
        //  (McGill start times are always 5 minutes after the nearest half hour
        return mStartTime.minusMinutes(5);
    }



    /**
     * Gets the end time of the course, rounded off the the nearest half hour
     *
     * @return The course rounded end time
     */
    public LocalTime getRoundedEndTime(){
        //Check if the end time is already a half hour increment
        if(mEndTime.getMinuteOfHour() == 0 || mEndTime.getMinuteOfHour() == 30){
            return mEndTime;
        }

        //If not, add 5 minutes to the end time to get round numbers
        //  (McGill end times are always 5 minutes before the nearest half hour
        return mEndTime.plusMinutes(5);
    }

    /**
     * Checks if the course is for the given date
     *
     * @param date The given date
     * @return True if it is, false otherwise
     */
    public boolean isForDate(LocalDate date){
        return !date.isBefore(mStartDate) && !date.isAfter(mEndDate);
    }

    /**
     * Gets the String representing the course times
     *
     * @return The course times in String format
     */
    public String getTimeString(){
        //No time associated, therefore no time string
        if(mStartTime.getHourOfDay() == 0 && mStartTime.getMinuteOfHour() == 0){
            Log.d(TAG, "No time associated when getting String");
            return "";
        }

        return Date.getTimeString(mStartTime) + " - " + Date.getTimeString(mEndTime);
    }

    /**
     * Returns a String representing the dates for this course
     *
     * @return The course dates in String format
     */
    public String getDateString(){
        return Date.getShortDateString(mStartDate) + " - " + Date.getShortDateString(mEndDate);
    }

    /**
     * 2 ClassItems are equal if they have the same CRN and are for the same term
     *
     * @param object The course to check
     * @return True if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object object){
        if(!(object instanceof ClassItem)){
            return false;
        }
        ClassItem classItem = (ClassItem)object;

        //Check if they have the same season, year, and CRN
        return this.mCRN == classItem.getCRN() && this.mTerm.equals(classItem.getTerm());
    }
}