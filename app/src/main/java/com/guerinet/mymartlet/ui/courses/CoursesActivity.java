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

package com.guerinet.mymartlet.ui.courses;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.Course;
import com.guerinet.mymartlet.model.RegistrationError;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.ui.DrawerActivity;
import com.guerinet.mymartlet.ui.dialog.DialogHelper;
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper;
import com.guerinet.mymartlet.util.Help;
import com.guerinet.mymartlet.util.dagger.prefs.DefaultTermPreference;
import com.guerinet.mymartlet.util.dagger.prefs.RegisterTermPreference;
import com.guerinet.mymartlet.util.dbflow.databases.CoursesDB;
import com.guerinet.mymartlet.util.dbflow.databases.TranscriptDB;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.mymartlet.util.manager.McGillManager;
import com.guerinet.mymartlet.util.retrofit.TranscriptConverter.TranscriptResponse;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
     * {@link Course}s list
     */
    @BindView(android.R.id.list)
    RecyclerView list;
    /**
     * Button to unregister from a course
     */
    @BindView(R.id.course_register)
    Button unregisterButton;
    /**
     * Empty list view
     */
    @BindView(android.R.id.empty)
    TextView emptyView;
    /**
     * {@link DefaultTermPreference} instance
     */
    @Inject
    DefaultTermPreference defaultTermPref;
    /**
     * {@link RegisterTermPreference} instance
     */
    @Inject
    RegisterTermPreference registerTermPref;
    /**
     * Adapter for the list of courses
     */
    private CoursesAdapter adapter;
    /**
     * Current {@link Term} shown
     */
    private Term term;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        analytics.sendScreen("View Courses");

        term = defaultTermPref.getTerm();

        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoursesAdapter(emptyView);
        list.setAdapter(adapter);

        // Format the unregister button
        unregisterButton.setText(R.string.courses_unregister);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        unregisterButton.setLayoutParams(params);

        // Remove this button
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
                        new TermDialogHelper(this, term, false) {
                            @Override
                            public void onTermSelected(Term term) {
                                // Set the default term
                                defaultTermPref.setTerm(term);

                                // Set the instance term
                                CoursesActivity.this.term = term;

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

    @HomepageManager.Homepage
    @Override
    protected int getCurrentPage() {
        return HomepageManager.COURSES;
    }

    /**
     * Updates all of the info in the view
     */
    private void update() {
        // Set the title
        setTitle(term.getString(this));

        //User can unregister if the current term is in the list of terms to register for
        boolean canUnregister = registerTermPref.getTerms().contains(term);

        // Change the text and the visibility if we are in the list of currently registered courses
        unregisterButton.setVisibility(canUnregister ? View.VISIBLE : View.GONE);

        // Update the list
        adapter.update(term, canUnregister);
    }

    /**
     * Refreshes the list of courses for the given term and the user's transcript
     */
    private void refresh() {
        if (!canRefresh()) {
            return;
        }

        // Download the courses for this term
        mcGillService.schedule(term).enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                // Set the courses
                CoursesDB.setCourses(term, response.body(), () -> {
                    handler.post(() -> {
                        // Update the view
                        update();
                        showToolbarProgress(false);
                    });
                });

                //Download the transcript (if ever the user has new semesters on their transcript)
                mcGillService.transcript().enqueue(new Callback<TranscriptResponse>() {
                    @Override
                    public void onResponse(Call<TranscriptResponse> call,
                            Response<TranscriptResponse> response) {
                        TranscriptDB.saveTranscript(CoursesActivity.this, response.body());
                    }

                    @Override
                    public void onFailure(Call<TranscriptResponse> call, Throwable t) {
                        Timber.e(t, "Error refreshing the transcript");
                        showToolbarProgress(false);
                        Help.handleException(CoursesActivity.this, t);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Timber.e(t, "Error refreshing courses");
                showToolbarProgress(false);
                Help.handleException(CoursesActivity.this, t);
            }
        });
    }

    /**
     * Tries to unregister from the given courses
     */
    @OnClick(R.id.course_register)
    protected void unregister() {
        // Get checked courses from adapter
        final List<Course> courses = adapter.getCheckedCourses();

        if (courses.size() > 10) {
            // Too many courses
            Utils.toast(this, R.string.courses_too_many_courses);
            return;
        }

        if (courses.isEmpty()) {
            // No courses
            Utils.toast(this, R.string.courses_none_selected);
            return;
        }

        DialogUtils.alert(this, R.string.unregister_dialog_title,
                R.string.unregister_dialog_message, (dialog, which) -> {
                    dialog.dismiss();

                    // Don't continue if the positive button has not been clicked on
                    if (which != DialogInterface.BUTTON_POSITIVE) {
                        return;
                    }

                    if (!canRefresh()) {
                        return;
                    }

                    // Run the registration thread
                    mcGillService.registration(McGillManager.getRegistrationURL(courses, true))
                            .enqueue(new Callback<List<RegistrationError>>() {
                                @Override
                                public void onResponse(Call<List<RegistrationError>> call,
                                        Response<List<RegistrationError>> response) {
                                    showToolbarProgress(false);

                                    // If there are no errors, show the success message
                                    if (response.body() == null || response.body().isEmpty()) {
                                        Utils.toast(CoursesActivity.this,
                                                R.string.unregistration_success);
                                        return;
                                    }

                                    // Prepare the error message String
                                    String errorMessage = "";
                                    for (RegistrationError error : response.body()) {
                                        errorMessage += error.getString(courses);
                                        errorMessage += "\n";
                                    }

                                    DialogHelper.error(CoursesActivity.this, errorMessage);

                                    // Refresh the courses
                                    refresh();
                                }

                                @Override
                                public void onFailure(Call<List<RegistrationError>> call,
                                        Throwable t) {
                                    Timber.e(t, "Error unregistering for courses");
                                    showToolbarProgress(false);
                                    Help.handleException(CoursesActivity.this, t);
                                }
                            });
                    });
    }
}