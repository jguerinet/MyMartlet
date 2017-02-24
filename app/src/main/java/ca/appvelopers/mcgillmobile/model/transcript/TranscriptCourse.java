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

import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.TranscriptCourseDB;

/**
 * A course that is part of the transcript
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Table(database = TranscriptCourseDB.class, allFields = true)
public class TranscriptCourse extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Self-managed Id, used as a primary key
     */
    @PrimaryKey(autoincrement = true)
    int id;
    /**
     * Course term
     */
    Term term;
    /**
     * Course code (e.g. ECSE 428)
     */
    String code;
    /**
     * Course title
     */
    String title;
    /**
     * Course credits
     */
    double credits;
    /**
     * User's grade in this course
     */
    String userGrade;
    /**
     * Average grade in this course
     */
    String averageGrade;

    /**
     * DB Constructor
     */
    TranscriptCourse() {}

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
