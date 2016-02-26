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

package ca.appvelopers.mcgillmobile.util.manager;

import android.content.Context;

import com.guerinet.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;

/**
 * Entirely manages the schedule (list of {@link Course}s) lifecycle
 * @author Julien Guerinet
 * @since 2.2.0
 */
@SuppressWarnings("unchecked")
@Singleton
public class ScheduleManager {
    /**
     * {@link Context} instance
     */
    private final Context context;
    /**
     * {@link Transcript} instance
     */
    private List<Course> courses;

    /**
     * Default Injectable Constructor
     *
     * @param context App Context
     */
    @Inject
    protected ScheduleManager(Context context) {
        this.context = context;
    }

    /**
     * @return List of {@link Course}s
     */
    public synchronized List<Course> get() {
        //If it's null, load it from internal storage
        if (courses == null) {
            courses = (List<Course>) StorageUtils.loadObject(context, "courses", "Courses");
            //If they are still null, use an empty list
            if (courses == null) {
                courses = new ArrayList<>();
            }
        }
        return courses;
    }

    /**
     * @param courses List of {@link Course}s to save
     */
    public synchronized void set(List<Course> courses) {
        //Don't save a null object
        if (courses == null) {
            return;
        }
        //Set the local instance
        this.courses = courses;
        //Save it to internal storage
        StorageUtils.saveObject(context, courses, "courses", "Courses");
    }

    /**
     * Saves a list of courses for the given term, replacing all courses for this term
     *
     * @param courses List of {@link Course}s
     * @param term    Course {@link Term}
     */
    public synchronized void set(List<Course> courses, Term term) {
        //Don't continue if we don't have all the necessary info
        if (courses == null || term == null) {
            return;
        }

        //Set the term on the passed list of courses
        for (Course course : courses) {
            course.setTerm(term);
        }

        //Get the courses for the current term in the current list of courses
        List<Course> coursesToDelete = new ArrayList<>();
        for (Course course : this.courses) {
            if (course.getTerm().equals(term)) {
                coursesToDelete.add(course);
            }
        }

        //Delete all of the old courses, add the new ones
        this.courses.removeAll(coursesToDelete);
        this.courses.addAll(courses);

        //Save it to internal storage
        StorageUtils.saveObject(context, this.courses, "courses", "Courses");
    }

    /**
     * Clears the stored {@link Course}s
     */
    public synchronized void clear() {
        //Clear both the local instance and the stored one
        courses = new ArrayList<>();
        context.deleteFile("courses");
    }
}
