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

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

/**
 * Models an error that occurred during registration
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class RegistrationError implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Course CRN with the error
     */
    private final int crn;
    /**
     * Error message
     */
    private final String message;

    /**
     * Default Constructor
     *
     * @param crn     Course CRN with the error
     * @param message Error message
     */
    public RegistrationError(int crn, String message) {
        this.crn = crn;
        this.message = message;
    }

    /**
     * @param courses List of {@link Course}s in order to find the course that the error is for
     * @return String to show to the user explaining the error message
     */
    public String getString(List<Course> courses) {
        Course course = null;

        //Find the course this error is for
        for (Course aCourse : courses) {
            if (aCourse.getCRN() == crn) {
                course = aCourse;
            }
        }

        //If the course is null, don't continue
        if (course == null) {
            Timber.e(new IllegalStateException(), "No course for the registration error");
            return "";
        }

        return course.getCode() + " (" + course.getType() + ") - " + message;
    }
}
