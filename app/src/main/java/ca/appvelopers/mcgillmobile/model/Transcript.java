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
import java.util.Collections;
import java.util.List;

/**
 * The user's unofficial transcript
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0.0
 */
public class Transcript implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * The user's cumulative GPA
     */
    private double mCGPA;
    /**
     * The user's total number of credits
     */
    private double mTotalCredits;
    /**
     * The list of semesters
     */
    private List<Semester> mSemesters;

    /**
     * Default Constructo
     *
     * @param cgpa         The CGPA
     * @param totalCredits The total number of credits
     * @param semesters    The semesters
     */
    public Transcript(double cgpa, double totalCredits, List<Semester> semesters){
        this.mCGPA = cgpa;
        this.mTotalCredits = totalCredits;
        this.mSemesters = semesters;
        //Store the semesters in reverse chronological order
        Collections.reverse(this.mSemesters);
    }

    /* GETTERS */

    /**
     * @return The CGPA
     */
    public double getCgpa(){
        return this.mCGPA;
    }

    /**
     * @return The total number of credits
     */
    public double getTotalCredits(){
        return this.mTotalCredits;
    }

    /**
     * @return The semesters (in reverse chronological order)
     */
    public List<Semester> getSemesters(){
        return this.mSemesters;
    }
}
