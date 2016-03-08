/*
 * Copyright 2014-2016 Julien Guerinet
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

/**
 * Entirely manages the schedule (list of {@link Course}s) lifecycle
 * @author Julien Guerinet
 * @since 2.2.0
 */
@SuppressWarnings("unchecked")
@Singleton
public class ScheduleManager {
    /**
     * File name
     */
    private static final String COURSES = "courses";
    /**
     * {@link Context} instance
     */
    private final Context context;
    /**
     * List of {@link Course}s
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
            courses = (List<Course>) StorageUtils.loadObject(context, COURSES, "Courses");
            //If they are still null, use an empty list
            if (courses == null) {
                courses = new ArrayList<>();
            }
        }
        return courses;
    }

    /**
     * @param term {@link Term} that the courses should be in
     * @return All {@link Course}s for the given term
     */
    public synchronized List<Course> getTermCourses(Term term) {
        List<Course> courses = new ArrayList<>();
        for (Course course : get()) {
            if (course.getTerm().equals(term)) {
                courses.add(course);
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
        StorageUtils.saveObject(context, courses, COURSES, "Courses");
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
        for (Course course : get()) {
            if (course.getTerm().equals(term)) {
                coursesToDelete.add(course);
            }
        }

        //Delete all of the old courses, add the new ones
        get().removeAll(coursesToDelete);
        get().addAll(courses);

        //Save it to internal storage
        StorageUtils.saveObject(context, get(), COURSES, "Courses");
    }

    /**
     * Clears the stored {@link Course}s
     */
    public synchronized void clear() {
        //Clear both the local instance and the stored one
        courses = new ArrayList<>();
        context.deleteFile(COURSES);
    }
}
