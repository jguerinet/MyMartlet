/*
 * Copyright 2014-2016 Appvelopers
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
import java.util.Collections;
import java.util.List;

/**
 * The user's unofficial transcript
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Transcript implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * User's cumulative GPA
     */
    private double cgpa;
    /**
     * User's total number of credits
     */
    private double totalCredits;
    /**
     * List of semesters
     */
    private List<Semester> semesters;

    /**
     * Default Constructor
     *
     * @param cgpa         CGPA
     * @param totalCredits Total number of credits
     * @param semesters    List of semesters
     */
    public Transcript(double cgpa, double totalCredits, List<Semester> semesters) {
        this.cgpa = cgpa;
        this.totalCredits = totalCredits;
        this.semesters = semesters;
        //Store the semesters in reverse chronological order
        Collections.reverse(this.semesters);
    }

    /* GETTERS */

    /**
     * @return The CGPA
     */
    public double getCGPA() {
        return cgpa;
    }

    /**
     * @return The total number of credits
     */
    public double getTotalCredits() {
        return totalCredits;
    }

    /**
     * @return The semesters (in reverse chronological order)
     */
    public List<Semester> getSemesters() {
        return semesters;
    }
}
