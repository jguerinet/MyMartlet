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

package ca.appvelopers.mcgillmobile.ui.courses;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.RegistrationError;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.dialog.list.TermDialogHelper;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.DefaultTermPreference;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
import ca.appvelopers.mcgillmobile.util.manager.ScheduleManager;
import ca.appvelopers.mcgillmobile.util.manager.TranscriptManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
    @BindView(android.R.id.list)
    protected RecyclerView mList;
    /**
     * The button to unregister from a course
     */
    @BindView(R.id.course_register)
    protected Button mUnregisterButton;
    /**
     * The empty list view
     */
    @BindView(R.id.courses_empty)
    protected TextView mEmptyView;
    /**
     * {@link DefaultTermPreference} instance
     */
    @Inject
    DefaultTermPreference defaultTermPref;
    /**
     * {@link TranscriptManager} instance
     */
    @Inject
    protected TranscriptManager transcriptManager;
    /**
     * {@link ScheduleManager} instance
     */
    @Inject
    protected ScheduleManager scheduleManager;
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
        App.component(this).inject(this);
        analytics.sendScreen("View Courses");

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_semester:
                DialogUtils.list(this, R.string.title_change_semester,
                        new TermDialogHelper(this, mTerm, false) {
                            @Override
                            public void onTermSelected(Term term) {
                                // Set the default term
                                defaultTermPref.setTerm(term);

                                //Set the instance term
                                mTerm = term;

                                update();
                                refresh();
                            }
                        });
                return true;
            case R.id.action_refresh:
                refresh();
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
        List<Course> courses = scheduleManager.getTermCourses(mTerm);

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
    private void refresh() {
        if (!canRefresh()) {
            return;
        }

        //Download the courses for this term
        mcGillService.schedule(mTerm).enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                //Set the courses
                scheduleManager.set(response.body(), mTerm);

                //Download the transcript (if ever the user has new semesters on their transcript)
                mcGillService.transcript().enqueue(new Callback<Transcript>() {
                    @Override
                    public void onResponse(Call<Transcript> call, Response<Transcript> response) {
                        transcriptManager.set(response.body());
                        //Update the view
                        update();
                        showToolbarProgress(false);
                    }

                    @Override
                    public void onFailure(Call<Transcript> call, Throwable t) {
                        Timber.e(t, "Error refreshing the transcript");
                        showToolbarProgress(false);

                        //If this is a MinervaException, broadcast it
                        if (t instanceof MinervaException) {
                            LocalBroadcastManager.getInstance(CoursesActivity.this)
                                    .sendBroadcast(new Intent(Constants.BROADCAST_MINERVA));
                        } else {
                            DialogHelper.error(CoursesActivity.this, R.string.error_other);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Timber.e(t, "Error refreshing courses");
                showToolbarProgress(false);
                //If this is a MinervaException, broadcast it
                if (t instanceof MinervaException) {
                    LocalBroadcastManager.getInstance(CoursesActivity.this)
                            .sendBroadcast(new Intent(Constants.BROADCAST_MINERVA));
                } else {
                    DialogHelper.error(CoursesActivity.this, R.string.error_other);
                }
            }
        });
    }

    /**
     * Tries to unregister from the given courses
     */
    @OnClick(R.id.course_register)
    protected void unregister() {
        //Get checked courses from adapter
        final List<Course> courses = mAdapter.getCheckedCourses();

        if (courses.size() > 10) {
            //Too many courses
            Utils.toast(this, R.string.courses_too_many_courses);
        } else if (courses.isEmpty()) {
            //No courses
            Utils.toast(this, R.string.courses_none_selected);
        } else {
            DialogUtils.alert(this, R.string.unregister_dialog_title,
                    R.string.unregister_dialog_message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            //Don't continue if the positive button has not been clicked on
                            if (which != DialogInterface.BUTTON_POSITIVE) {
                                return;
                            }

                            //Make sure we are connected to the internet
                            if (!Utils.isConnected(CoursesActivity.this)) {
                                DialogHelper.error(CoursesActivity.this,
                                        R.string.error_no_internet);
                                return;
                            }

                            //Show the user we are loading
                            showToolbarProgress(true);

                            //Run the registration thread
                            mcGillService.registration(
                                    McGillManager.getRegistrationURL(mTerm, courses, true))
                                    .enqueue(new Callback<List<RegistrationError>>() {
                                        @Override
                                        public void onResponse(Call<List<RegistrationError>> call,
                                                Response<List<RegistrationError>> response) {
                                            showToolbarProgress(false);

                                            //If there are no errors, show the success message
                                            if (response.body() == null ||
                                                    response.body().isEmpty()) {
                                                Utils.toast(CoursesActivity.this,
                                                        R.string.unregistration_success);
                                                return;
                                            }

                                            //Prepare the error message String
                                            String errorMessage = "";
                                            for (RegistrationError error : response.body()) {
                                                errorMessage += error.getString(courses);
                                                errorMessage += "\n";
                                            }

                                            DialogHelper.error(CoursesActivity.this, errorMessage);

                                            //Refresh the courses
                                            refresh();
                                        }

                                        @Override
                                        public void onFailure(Call<List<RegistrationError>> call,
                                                Throwable t) {
                                            Timber.e(t, "Error unregistering for courses");
                                            showToolbarProgress(false);
                                            //If this is a MinervaException, broadcast it
                                            if (t instanceof MinervaException) {
                                                LocalBroadcastManager
                                                        .getInstance(CoursesActivity.this)
                                                        .sendBroadcast(new Intent(
                                                                Constants.BROADCAST_MINERVA));
                                            } else {
                                                DialogHelper.error(CoursesActivity.this,
                                                        R.string.error_other);
                                            }
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.COURSES;
    }
}