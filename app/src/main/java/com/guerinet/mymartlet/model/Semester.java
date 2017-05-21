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

import com.guerinet.mymartlet.util.dbflow.databases.SemesterDB;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

/**
 * Contains information pertaining to each semester such as current program, term credits,
 *  term GPA, and full time status
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Table(database = SemesterDB.class, allFields = true)
public class Semester extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Id if this semester
     */
    @PrimaryKey
    int id;
    /**
     * The semester term
     */
    Term term;
    /**
     * The user's program for this semester
     */
    String program;
    /**
     * The user's bachelor name for this semester
     */
    String bachelor;
    /**
     * The number of credits for this semester
     */
    double credits;
    /**
     * The semester GPA
     */
    double gpa;
    /**
     * True if the user was a full-time student during this semester, false otherwise
     */
    boolean fullTime;

    /**
     * DB Constructor
     */
    Semester() {}

    /**
     * Default Constructor
     *
     * @param semesterId   Id of the current semester
     * @param term         Semester term
     * @param program      Semester's program name
     * @param bachelor     Semester's bachelor name
     * @param credits      Semester credits
     * @param gpa          Semester GPA
     * @param fullTime     True if the user was a full-time student during this semester,
     *                     false otherwise
     */
    public Semester(int semesterId, Term term, String program, String bachelor, double credits,
            double gpa, boolean fullTime) {
        this.id = semesterId;
        this.term = term;
        this.program = program;
        this.bachelor = bachelor;
        this.credits = credits;
        this.gpa = gpa;
        this.fullTime = fullTime;
    }

    /* GETTERS */

    /**
     * @return Semester Id
     */
    public int getId() {
        return id;
    }

    /**
     * @return Semester term
     */
    public Term getTerm() {
        return term;
    }

    /**
     * @param context App context
     * @return Semester name
     */
    public String getSemesterName(Context context) {
        return term.getString(context);
    }

    /**
     * @return Program name
     */
    public String getProgram() {
        return program;
    }

    /**
     * @return Bachelor name
     */
    public String getBachelor() {
        return bachelor;
    }

    /**
     * @return Semester credits
     */
    public double getCredits() {
        return credits;
    }

    /**
     * @return Semester GPA
     */
    public double getGPA() {
        return gpa;
    }

    /**
     * @return True if the user was full time during this semester, false otherwise
     */
    public boolean isFullTime() {
        return fullTime;
    }
}
