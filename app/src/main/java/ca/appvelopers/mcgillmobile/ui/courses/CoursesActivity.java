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

package ca.appvelopers.mcgillmobile.ui.courses;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guerinet.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Shows the user all of the courses the user has taken or is currently registered in
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @since 1.0.0
 */
public class CoursesActivity extends DrawerActivity {
    /**
     * The ListView for the courses
     */
    @Bind(android.R.id.list)
    protected RecyclerView mList;
    /**
     * The button to unregister from a course
     */
    @Bind(R.id.course_register)
    protected Button mUnregisterButton;
    /**
     * The empty list view
     */
    @Bind(R.id.courses_empty)
    protected TextView mEmptyView;
    /**
     * The ListView adapter
     */
    private CoursesAdapter mAdapter;
    /**
     * The current term shown
     */
    private Term mTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        ButterKnife.bind(this);
        Analytics.get().sendScreen("View Courses");

        mTerm = App.getDefaultTerm();

        mList.setLayoutManager(new LinearLayoutManager(this));

        //Remove this button
        findViewById(R.id.course_wishlist).setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        getMenuInflater().inflate(R.menu.change_semester, menu);
        Utils.setTint(menu.findItem(R.id.action_refresh).getIcon(), Color.WHITE);
        return true;
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
                                update();
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

    /**
     * Updates all of the info in the view
     */
    private void update() {
        //Set the title
        setTitle(mTerm.getString(this));

        //User can unregister if the current term is in the list of terms to register for
        boolean canUnregister = App.getRegisterTerms().contains(mTerm);

        //Change the text and the visibility if we are in the list of currently registered courses
        if (canUnregister) {
            mUnregisterButton.setVisibility(View.VISIBLE);
            mUnregisterButton.setText(R.string.courses_unregister);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mUnregisterButton.setLayoutParams(params);
        } else {
            mUnregisterButton.setVisibility(View.GONE);
        }

        //Get the list of courses for this term
        List<Course> courses = new ArrayList<>();
        for (Course course : App.getCourses()) {
            if (course.getTerm().equals(mTerm)) {
                courses.add(course);
            }
        }

        //Set up the list
        mAdapter = new CoursesAdapter(courses, canUnregister);
        mList.setAdapter(mAdapter);

        //Show the empty view if needed
        mList.setVisibility(courses.isEmpty() ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(courses.isEmpty() ? View.VISIBLE : View.GONE);
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
                                                    update();

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
     * Tries to unregister from the given courses
     */
    @OnClick(R.id.course_register)
    protected void unregister(){
        //Get checked courses from adapter
        final List<Course> courses = mAdapter.getCheckedCourses();

        if (courses.size() > 10) {
            //Too many courses
            Toast.makeText(this, R.string.courses_too_many_courses, Toast.LENGTH_SHORT).show();
        } else if (courses.isEmpty()) {
            //No courses
            Toast.makeText(this, R.string.courses_none_selected, Toast.LENGTH_SHORT).show();
        } else {
            //Ask for confirmation before unregistering
            new AlertDialog.Builder(this)
                    .setTitle(R.string.unregister_dialog_title)
                    .setMessage(R.string.unregister_dialog_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Show the user we are loading
                                    showToolbarProgress(true);

                                    //Run the registration thread
                                    new DownloaderThread(CoursesActivity.this,
                                            Connection.getRegistrationURL(mTerm, courses, true))
                                            .execute(new DownloaderThread.Callback() {
                                                @Override
                                                public void onDownloadFinished(String result) {
                                                    if (result != null) {
                                                        String error =
                                                                Parser.parseRegistrationErrors(
                                                                        result, courses);

                                                        if (error == null) {
                                                            //If there are no errors,
                                                            //  show the success message
                                                            Toast.makeText(CoursesActivity.this,
                                                                    R.string.unregistration_success,
                                                                    Toast.LENGTH_LONG).show();
                                                        } else {
                                                            //If not, show the error message
                                                            DialogHelper.neutral(
                                                                    CoursesActivity.this,
                                                                    R.string.unregistration_error,
                                                                    error);
                                                        }

                                                        //Refresh the courses
                                                        refreshCourses();
                                                    }
                                                }
                                            });
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.COURSES;
    }
}