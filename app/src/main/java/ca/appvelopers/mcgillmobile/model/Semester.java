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

import java.io.Serializable;
import java.util.List;

/**
 * Contains information pertaining to each semester such as current program, term credits,
 *  term GPA, and full time status
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class Semester implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * The semester term
     */
    private Term mTerm;
    /**
     * The user's program name for this semester
     */
    private String mProgramYear;
    /**
     * The user's bachelor name for this semester
     */
    private String mBachelor;
    /**
     * The year in the user's program that this semester is for
     */
    private int mYear;
    /**
     * The number of credits for this semester
     */
    private double mCredits;
    /**
     * The semester GPA
     */
    private double mGPA;
    /**
     * True if the user was a full-time student during this semester, false otherwise
     */
    private boolean mFullTime;
    /**
     * True if the user's standing was satisfactory during this semester, false otherwise
     */
    private boolean mSatisfactory;
    /**
     * The list of courses taken during this semester
     */
    private List<TranscriptCourse> mCourses;

    /**
     * Default Constructor
     *
     * @param term         The semester term
     * @param program      The semester's program name
     * @param bachelor     The semester's bachelor name
     * @param programYear  The program year
     * @param credits      The semester credits
     * @param gpa          The semester GPA
     * @param fullTime     True if the user was a full-time student during this semester,
     *                      false otherwise
     * @param satisfactory True if the user's standing was satisfactory during this semester,
     *                      false otherwise
     * @param courses      The list of mCourses taken during this semester
     */
    public Semester(Term term, String program, String bachelor, int programYear, double credits,
                    double gpa, boolean fullTime, boolean satisfactory,
                    List<TranscriptCourse> courses) {
        this.mTerm = term;
        this.mProgramYear = program;
        this.mBachelor = bachelor;
        this.mCredits = credits;
        this.mGPA = gpa;
        this.mYear = programYear;
        this.mFullTime = fullTime;
        this.mSatisfactory = satisfactory;
        this.mCourses = courses;
    }

    /* GETTERS */

    /**
     * @return The semester term
     */
    public Term getTerm(){
        return this.mTerm;
    }

    /**
     * @return The semester name
     */
    public String getSemesterName(){
        return this.mTerm.toString();
    }

    /**
     * @return The program name
     */
    public String getProgram() {
        return this.mProgramYear;
    }

    /**
     * @return The bachelor name
     */
    public String getBachelor(){
        return this.mBachelor;
    }

    /**
     * @return The semester credits
     */
    public double getCredits() {
        return this.mCredits;
    }

    /**
     * @return The semester GPA
     */
    public double getGPA() {
        return this.mGPA;
    }

    /**
     * @return True if the user was full time during this semester, false otherwise
     */
    public boolean isFullTime() {
        return this.mFullTime;
    }

    /**
     * @return The semester's courses
     */
    public List<TranscriptCourse> getCourses(){
        return this.mCourses;
    }
}
