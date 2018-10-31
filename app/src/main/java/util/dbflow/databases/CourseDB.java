/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.util.dbflow.databases;

import com.guerinet.mymartlet.model.Course;
import com.guerinet.mymartlet.model.Course_Table;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.util.dbflow.DBUtils;
import com.raizlabs.android.dbflow.annotation.Database;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Database that holds a list of {@link Course}s that comprise the user's schedule
 * @author Julien Guerinet
 * @since 2.4.0
 */
@Database(name = CourseDB.NAME, version = CourseDB.VERSION)
public class CourseDB {
    public static final String NAME = "Course";
    public static final String FULL_NAME = NAME + ".db";
    static final int VERSION = 1;

    /**
     * Saves the {@link Course}s for the given {@link Term}
     *
     * @param term     {@link Term} these courses are for
     * @param courses  List of {@link Course}s to save
     * @param callback Optional callback called when the transaction is finished
     */
    public static void setCourses(Term term, List<Course> courses,
            @Nullable DBUtils.Callback callback) {
        if (courses == null) {
            courses = new ArrayList<>();
        }

        // Set the currentTerm on the passed list of courses
        for (Course course : courses) {
            course.setTerm(term);
        }

        DBUtils.updateDB(Course.class, courses, Course_Table.term.eq(term), CourseDB.class,
                (object, oldObject) -> {
                    // Save the new object, delete the old one
                    //  If we don't do this, a duplicate will be created because they don't have the
                    //  same Id
                    object.save();
                    oldObject.delete();
                }, callback);
    }
}