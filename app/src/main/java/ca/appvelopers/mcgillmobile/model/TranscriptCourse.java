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

/**
 * A course that is part of the transcript
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class TranscriptCourse implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * The course term
     */
    private Term mTerm;
    /**
     * The course code (e.g. ECSE 428)
     */
    private String mCode;
    /**
     * The course title
     */
    private String mTitle;
    /**
     * The course credits
     */
    private double mCredits;
    /**
     * The user's grade in this course
     */
    private String mUserGrade;
    /**
     * The average grade in this course
     */
    private String mAverageGrade;

    /**
     * Default Constructor
     *
     * @param term         The course term
     * @param code         The course code
     * @param title        The course title
     * @param credits      The course credits
     * @param userGrade    The user's grade
     * @param averageGrade The course average grade
     */
    public TranscriptCourse(Term term, String code, String title, double credits,
                            String userGrade, String averageGrade){
        this.mTerm = term;
        this.mCode = code;
        this.mTitle = title;
        this.mCredits = credits;
        this.mUserGrade = userGrade;
        this.mAverageGrade = averageGrade;
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
    public String getCourseCode(){
        return this.mCode;
    }

    /**
     * @return The course title
     */
    public String getCourseTitle(){
        return this.mTitle;
    }

    /**
     * @return The course credits
     */
    public double getCredits(){
        return this.mCredits;
    }

    /**
     * @return The user's grade
     */
    public String getUserGrade(){
        return this.mUserGrade;
    }

    /**
     * @return The average grade
     */
    public String getAverageGrade(){
        return this.mAverageGrade;
    }
}
