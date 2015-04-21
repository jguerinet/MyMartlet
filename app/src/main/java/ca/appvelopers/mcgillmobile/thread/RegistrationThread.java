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

package ca.appvelopers.mcgillmobile.thread;

import android.app.Activity;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;

/**
 * Attempts to (un)register the user to the given classes
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class RegistrationThread extends InfoDownloader{
    private static final String TAG = "RegistrationThread";
    /**
     * The activity instance
     */
    private Activity mActivity;
    /**
     * The term the user is (un)registering in
     */
    private Term mTerm;
    /**
     * The list of courses to (un)register for
     */
    private List<Course> mCourses;
    /**
     * True if the user is registering, false if they are unregistering
     */
    private boolean mRegister;
    /**
     * A map of the possible (un)registration errors, with the key being the course with the error
     */
    private Map<String, String> mErrors;

    /**
     * Default Constructor
     *
     * @param activity The calling activity
     * @param term     The term the user is registering in
     * @param courses  The list of courses the user is registering for
     * @param register True if the user is registering, false if they are unregistering
     */
    public RegistrationThread(Activity activity, Term term, List<Course> courses, boolean register){
        this.mActivity = activity;
        this.mCourses = courses;
        this.mTerm = term;
        this.mRegister = register;
        this.mErrors = new HashMap<>();
    }

    @Override
    public void run(){
        synchronized(this){
            //Make the request
            String result =
                    get(TAG, mActivity, Connection.getRegistrationURL(mTerm, mCourses, mRegister));

            //If there is a body response, parse it
            if(result != null){
                mErrors = Parser.parseRegistrationErrors(result);
            }

            //Log the errors
            for(String crn : mErrors.keySet()){
                Log.e(TAG, "Error for " + crn + ": " + mErrors.get(crn));
            }
        }
        notify();
    }

    /* GETTERS */

    /**
     * @return The registration errors
     */
    public Map<String, String> getErrors(){
        return this.mErrors;
    }
}