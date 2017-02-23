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

package ca.appvelopers.mcgillmobile.model.transcript;

import java.io.Serializable;

import ca.appvelopers.mcgillmobile.model.Term;

/**
 * A course that is part of the transcript
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class TranscriptCourse implements Serializable{
    private static final long serialVersionUID = 1L;
    /**
     * Course term
     */
    private Term term;
    /**
     * Course code (e.g. ECSE 428)
     */
    private String code;
    /**
     * Course title
     */
    private String title;
    /**
     * Course credits
     */
    private double credits;
    /**
     * User's grade in this course
     */
    private String userGrade;
    /**
     * Average grade in this course
     */
    private String averageGrade;

    /**
     * Default Constructor
     *
     * @param term         Course term
     * @param code         Course code
     * @param title        Course title
     * @param credits      Course credits
     * @param userGrade    User's grade
     * @param averageGrade Course average grade
     */
    public TranscriptCourse(Term term, String code, String title, double credits, String userGrade,
            String averageGrade) {
        this.term = term;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.userGrade = userGrade;
        this.averageGrade = averageGrade;
    }

    /* GETTERS */

    /**
     * @return Course term
     */
    public Term getTerm() {
        return term;
    }

    /**
     * @return Course code
     */
    public String getCourseCode() {
        return code;
    }

    /**
     * @return Course title
     */
    public String getCourseTitle() {
        return title;
    }

    /**
     * @return Course credits
     */
    public double getCredits() {
        return credits;
    }

    /**
     * @return User's grade
     */
    public String getUserGrade() {
        return userGrade;
    }

    /**
     * @return Average grade
     */
    public String getAverageGrade() {
        return averageGrade;
    }
}
