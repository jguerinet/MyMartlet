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

package ca.appvelopers.mcgillmobile.ui.schedule;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.guerinet.utils.Utils;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.storage.Save;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Represents the user's schedule
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class ScheduleActivity extends DrawerActivity {
    /**
     * The list of courses
     */
    private List<Course> mCourses;
    /**
     * The current term
     */
    private Term mTerm;
    /**
     * The ScheduleViewBuilder
     */
    private ScheduleViewBuilder mViewBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTerm = App.getDefaultTerm();
        mCourses = new ArrayList<>();
        //Set up the ScheduleViewBuilder
        mViewBuilder = new ScheduleViewBuilder(this, getStartingDate());

        //Load the right view
        View view = loadView(getResources().getConfiguration().orientation);
        setContentView(view);
        ButterKnife.bind(this);

        //Check if this is the first time the user is using the app
        if (Load.firstOpen()) {
            //Show them the walkthrough if it is
            Intent intent = new Intent(this, WalkthroughActivity.class)
                    .putExtra(Constants.FIRST_OPEN, true);
            startActivity(intent);
            //Save the fact that the walkthrough has been seen at least once
            Save.firstOpen();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        //Only show the menu in portrait mode
        return getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Only load the menu in portrait mode
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getMenuInflater().inflate(R.menu.refresh, menu);
            getMenuInflater().inflate(R.menu.change_semester, menu);
            Utils.setTint(menu.findItem(R.id.action_refresh).getIcon(), Color.WHITE);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_semester:
                DialogHelper.changeSemester(this, mTerm, false,
                        new DialogHelper.TermCallback() {
                            @Override
                            public void onTermSelected(Term term) {
                                mTerm = term;

                                //Restart the schedule view builder with the right date
                                mViewBuilder = new ScheduleViewBuilder(ScheduleActivity.this,
                                        getStartingDate());

                                //Refresh the content
                                refreshCourses();
                            }
                        });
                return true;
            case R.id.action_refresh:
                refreshCourses();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(loadView(newConfig.orientation));
        invalidateOptionsMenu();
    }

    @Override
    protected @Homepage.Type int getCurrentPage() {
        return Homepage.SCHEDULE;
    }

    /**
     * Reloads the view
     *
     * @param orientation The current orientation
     * @return The view to load
     */
    private View loadView(int orientation) {
        //Title
        setTitle(mTerm.toString());

        //Return the view
        return mViewBuilder.renderView(orientation);
    }

    /**
     * Gets the starting date based on the term and get the concerned classes
     *
     * @return The starting date
     */
    private LocalDate getStartingDate() {
        fillCourses();

        //Date is by default set to today
        LocalDate date = LocalDate.now();
        //Check if we are in the current semester
        if (!mTerm.equals(Term.getCurrentTerm())) {
            //If not, find the starting date of this semester instead of using today
            for (Course classItem : mCourses) {
                if (classItem.getStartDate().isBefore(date)) {
                    date = classItem.getStartDate();
                }
            }
        }

        return date;
    }

    /**
     * Refreshes the list of courses for the given term and the user's transcript
     */
    private void refreshCourses() {
        //Show the user we are refreshing
        showToolbarProgress(true);

        //Download the courses for this term
        new DownloaderThread(this, Connection.getScheduleURL(mTerm))
                .execute(new DownloaderThread.Callback() {
                    @Override
                    public void onDownloadFinished(String result) {
                        //Parse the courses if there are any
                        if (result != null) {
                            Parser.parseCourses(mTerm, result);

                            //Download the Transcript
                            //  (if ever the user has new semesters on their transcript)
                            new DownloaderThread(null, Connection.TRANSCRIPT_URL)
                                    .execute(new DownloaderThread.Callback() {
                                        @Override
                                        public void onDownloadFinished(String result) {
                                            //Parse the transcript if possible
                                            if (result != null) {
                                                Parser.parseTranscript(result);
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //Update the view
                                                    setContentView(loadView(getResources().
                                                            getConfiguration().orientation));

                                                    //Done refreshing
                                                    showToolbarProgress(false);
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Fills the class list with the current term's classes
     */
    private void fillCourses() {
        //Clear the current course list, add the courses that are for this semester
        mCourses.clear();
        for (Course classItem : App.getCourses()) {
            if (classItem.getTerm().equals(mTerm)) {
                mCourses.add(classItem);
            }
        }
    }

    /**
     * Returns the list of courses for a given day and date
     *
     * @param date The date
     * @return The list of courses
     */
    public List<Course> getCourses(LocalDate date) {
        List<Course> courses = new ArrayList<>();

        //Go through the list of courses, find which ones have the same day and
        //  are for the given date
        for (Course course : mCourses) {
            if (course.isForDate(date)) {
                courses.add(course);
            }
        }
        return courses;
    }
}