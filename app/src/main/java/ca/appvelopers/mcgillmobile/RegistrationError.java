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

package ca.appvelopers.mcgillmobile;

import java.io.Serializable;

import ca.appvelopers.mcgillmobile.model.Course;

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
    private final String crn;
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
    public RegistrationError(String crn, String message) {
        this.crn = crn;
        this.message = message;
    }

    /**
     * @return Course CRN with the error
     */
    public String getCRN() {
        return crn;
    }

    /**
     * @param course {@link Course} that this error is for
     * @return String to show to the user explaining the error message
     */
    public String getString(Course course) {
        return course.getCode() + " (" + course.getType() + ") - " + message + "\n";
    }
}
