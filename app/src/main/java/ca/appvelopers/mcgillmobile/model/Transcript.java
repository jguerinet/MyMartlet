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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will retrieve and parse transcript data from Minerva and hold all the information
 * that is shown on the transcript
 *
 */
public class Transcript implements Serializable{
    private static final long serialVersionUID = 1L;

    private double mCGPA;
    private double mTotalCredits;
    private List<Semester> mSemesters;

    //Constructor for the Transcript object
    public Transcript(double cgpa, double totalCredits, List<Semester> semesters){
        this.mCGPA = cgpa;
        this.mTotalCredits = totalCredits;
        this.mSemesters = semesters;
    }

    //Getter for CGPA
    public double getCgpa(){
        return mCGPA;
    }

    //Getter for totalCredits
    public double getTotalCredits(){
        return mTotalCredits;
    }

    //Getter for semesters
    public List<Semester> getSemesters(){
        //Return semesters in reverse chronological order
        ArrayList<Semester> reversedSemesters = new ArrayList<Semester>(mSemesters);
        Collections.reverse(reversedSemesters);
        return reversedSemesters;
    }
}
