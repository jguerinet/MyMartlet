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

package ca.appvelopers.mcgillmobile.util.thread;

import android.content.Context;
import android.net.ConnectivityManager;

import com.guerinet.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.model.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.util.storage.Save;
import timber.log.Timber;

/**
 * Downloads the user's information
 * @author Julien Guerinet
 * @since 2.2.0
 */
public abstract class UserDownloader extends Thread {
    /**
     * App context
     */
    @Inject
    protected Context context;
    /**
     * {@link ConnectivityManager} instance
     */
    @Inject
    protected ConnectivityManager connectivityManager;
    /**
     * The {@link McGillService} instance
     */
    @Inject
    protected McGillService mcGillService;
    /**
     * True if everything should be downloaded, false otherwise (defaults to false)
     */
    protected boolean downloadEverything = false;

    /**
     * Default Constructor
     *
     * @param context App context
     */
    public UserDownloader(Context context) {
        App.component(context).inject(this);
    }

    @Override
    public void run() {
        synchronized (this) {
            //If we're not connected to the internet, don't continue
            if (!Utils.isConnected(connectivityManager)) {
                return;
            }

            //Transcript
            if (downloadEverything) {
                update(context.getString(R.string.downloading_transcript));
            }

            try {
                App.setTranscript(mcGillService.transcript().execute().body());
            } catch (MinervaException e) {
                //TODO
            } catch(IOException e) {
                Timber.e(e, "Transcript Exception");
            }

            //The current term
            Term currentTerm = Term.currentTerm();
            //List of semesters
            List<Semester> semesters = App.getTranscript().getSemesters();

            //Go through the semesters
            for (Semester semester: semesters) {
                //Get the term of this semester
                Term term = semester.getTerm();

                //If we are not downloading everything, only download it if it's the
                //  current or future term
                if (downloadEverything || term.equals(currentTerm) ||
                        term.isAfter(currentTerm)) {
                    if (downloadEverything) {
                        update(context.getString(R.string.downloading_semester,
                                term.getString(context)));
                    }

                    //Download the schedule
                    try {
                        List<Course> courses = mcGillService.schedule(term).execute().body();

                        //Go through the courses and set the term
                        for (Course course : courses) {
                            course.setTerm(term);
                        }

                        List<Course> existingCourses = App.getCourses();
                        List<Course> coursesToRemove = new ArrayList<>();
                        //Delete all courses for this term
                        for (Course course : existingCourses) {
                            if (course.getTerm().equals(term)) {
                                coursesToRemove.add(course);
                            }
                        }

                        //Remove the old ones and add the new ones
                        existingCourses.removeAll(coursesToRemove);
                        existingCourses.addAll(courses);
                        Save.courses();
                    } catch (MinervaException e) {
                        //TODO
                    } catch (IOException e) {
                        Timber.e("Term: %s", term.getId());
                        Timber.e(e, "Term Exception");
                    }
                }
            }

            //TODO Move this
            //Set the default term if there is none set yet
            if (App.getDefaultTerm() == null) {
                App.setDefaultTerm(currentTerm);
            }

            //Ebill
            if (downloadEverything) {
                update(context.getString(R.string.downloading_ebill));
            }

            //Download the eBill and user info
            try {
                App.setEbill(mcGillService.ebill().execute().body());
            } catch (MinervaException e) {
                //TODO
            } catch(Exception e) {
                Timber.e(e, "Ebill Exception");
            }
            notify();
        }
    }

    /**
     * Executes the thread synchronously
     */
    public void execute() {
        //If we are downloading this synchronously, then we're downloading everything
        downloadEverything = true;

        //Start the download
        start();

        //Wait until the thread has been fully executed
        synchronized(this) {
            try {
                wait();
            } catch(InterruptedException ignored){}
        }
    }

    public abstract void update(String section);
}

