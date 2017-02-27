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

package ca.appvelopers.mcgillmobile.model;

import android.content.Context;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.Serializable;
import java.util.List;

import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse;
import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse_Table;

/**
 * Contains information pertaining to each semester such as current program, term credits,
 *  term GPA, and full time status
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Semester implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Id if this semester
     */
    private int id;
    /**
     * The semester term
     */
    private Term term;
    /**
     * The user's program for this semester
     */
    private String program;
    /**
     * The user's bachelor name for this semester
     */
    private String bachelor;
    /**
     * The number of credits for this semester
     */
    private double credits;
    /**
     * The semester GPA
     */
    private double gpa;
    /**
     * True if the user was a full-time student during this semester, false otherwise
     */
    private boolean fullTime;
    /**
     * The list of courses taken during this semester
     */
    private transient List<TranscriptCourse> courses;

    /**
     * Default Constructor
     *
     * @param term         Semester term
     * @param program      Semester's program name
     * @param bachelor     Semester's bachelor name
     * @param credits      Semester credits
     * @param gpa          Semester GPA
     * @param fullTime     True if the user was a full-time student during this semester,
     *                     false otherwise
     */
    public Semester(Term term, String program, String bachelor, double credits, double gpa,
            boolean fullTime) {
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

    /**
     * @return The semester's courses
     */
    public List<TranscriptCourse> getCourses() {
        if (courses == null) {
            courses = SQLite.select()
                    .from(TranscriptCourse.class)
                    .where(TranscriptCourse_Table.semesterId.eq(id))
                    .queryList();
        }
        return courses;
    }
}
