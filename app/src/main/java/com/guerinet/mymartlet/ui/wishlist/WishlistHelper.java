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

package com.guerinet.mymartlet.ui.wishlist;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.Course;
import com.guerinet.mymartlet.model.CourseResult;
import com.guerinet.mymartlet.model.CourseResult_Table;
import com.guerinet.mymartlet.model.RegistrationError;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.model.exception.MinervaException;
import com.guerinet.mymartlet.ui.BaseActivity;
import com.guerinet.mymartlet.ui.dialog.DialogHelper;
import com.guerinet.mymartlet.util.Analytics;
import com.guerinet.mymartlet.util.Constants;
import com.guerinet.mymartlet.util.manager.McGillManager;
import com.guerinet.mymartlet.util.retrofit.McGillService;
import com.guerinet.suitcase.dialog.DialogUtils;
import com.guerinet.suitcase.util.Utils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
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
 * Shows the results of the search from the SearchActivity
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class WishlistHelper {
    /**
     * Empty view
     */
    @BindView(android.R.id.empty)
    TextView emptyView;
    /**
     * Courses list
     */
    @BindView(android.R.id.list)
    RecyclerView list;
    /**
     * Add/Remove to/from wishlist button
     */
    @BindView(R.id.course_wishlist)
    Button wishlistButton;
    /**
     * {@link McGillService} instance
     */
    @Inject
    McGillService mcGillService;
    /**
     * {@link Analytics} instance
     */
    @Inject
    Analytics analytics;
    /**
     * Calling activity instance
     */
    private final BaseActivity activity;
    /**
     * True if the user can add courses to the wishlist, false if they can remove them
     */
    private final boolean add;
    /**
     * The adapter for the list of results
     */
    private final WishlistAdapter adapter;

    /**
     * Default Constructor
     *
     * @param activity Calling activity instance
     * @param add      True if the user can add courses to the wishlist, false otherwise
     */
    public WishlistHelper(BaseActivity activity, View container, boolean add) {
        this.activity = activity;
        this.add = add;
        ButterKnife.bind(this, container);
        App.component(activity).inject(this);

        list.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new WishlistAdapter(emptyView);
        list.setAdapter(adapter);

        // Change the button text if this is to remove courses
        if (!add) {
            wishlistButton.setText(R.string.courses_remove_wishlist);
        }
    }

    /**
     * Update method for search results
     *
     * @param courses List of {@link CourseResult}s to display
     */
    public void update(List<CourseResult> courses) {
        adapter.update(courses);
    }

    /**
     * Update method for the wishlist
     *
     * @param term {@link Term} that the courses should be in, null if none
     */
    public void update(@Nullable Term term) {
        adapter.update(term);
    }

    @OnClick(R.id.course_register)
    void registerButton() {
        List<CourseResult> courses = adapter.getCheckedCourses();
        if (courses.size() > 10) {
            // Too many courses
            Utils.toast(activity, R.string.courses_too_many_courses);
        } else if (courses.isEmpty()) {
            // No Courses
            Utils.toast(activity, R.string.courses_none_selected);
        } else if (courses.size() > 0) {
            // Execute registration of checked classes in a new thread
            if (!activity.canRefresh()) {
                // Check that we can continue
                return;
            }

            // Confirm with the user before continuing
            DialogUtils.alert(activity, R.string.warning, R.string.registration_disclaimer,
                    (dialogInterface, i) -> {
                        if (i == DialogAction.POSITIVE) {
                            register(courses);
                        } else {
                            activity.showToolbarProgress(false);
                        }
                    });
        }
    }

    @OnClick(R.id.course_wishlist)
    void wishlistButton() {
        updateWishlist(adapter.getCheckedCourses());
    }

    private void register(final List<CourseResult> courses) {
        mcGillService.registration(McGillManager.getRegistrationURL(courses, false))
                .enqueue(new Callback<List<RegistrationError>>() {
                    @Override
                    public void onResponse(Call<List<RegistrationError>> call,
                            Response<List<RegistrationError>> response) {
                        activity.showToolbarProgress(false);

                        // If there are no errors, show the success message
                        if (response.body() == null || response.body().isEmpty()) {
                            Utils.toast(activity, R.string.registration_success);

                            // Remove the courses from the wishlist if they were there
                            for (CourseResult course : courses) {
                                course.delete();
                            }
                            return;
                        }

                        // Prepare the error message String
                        String errorMessage = "";
                        List<Course> errorCourses = new ArrayList<>();
                        errorCourses.addAll(courses);
                        for (RegistrationError error : response.body()) {
                            errorMessage += error.getString(errorCourses);
                            errorMessage += "\n";
                        }

                        DialogHelper.error(activity, errorMessage);
                    }

                    @Override
                    public void onFailure(Call<List<RegistrationError>> call, Throwable t) {
                        Timber.e(t, "Error (un)registering for courses");
                        activity.showToolbarProgress(false);
                        // If this is a MinervaException, broadcast it
                        if (t instanceof MinervaException) {
                            LocalBroadcastManager.getInstance(activity)
                                    .sendBroadcast(new Intent(Constants.BROADCAST_MINERVA));
                        } else {
                            DialogHelper.error(activity, R.string.error_other);
                        }
                    }
                });
    }

    /**
     * Adds/removes the given courses to/from the wishlist
     *
     * @param courses List of {@link CourseResult}s to add/remove
     */
    private void updateWishlist(List<CourseResult> courses) {
        String toastMessage;
        if (courses.isEmpty()) {
            // If there are none, display error message
            toastMessage = activity.getString(R.string.courses_none_selected);
        } else {
            if (add) {
                // Only add it if it's not already part of the wishlist
                int coursesAdded = 0;
                for (CourseResult course : courses) {
                    // Check if the course exists already
                    boolean exists = SQLite.selectCountOf()
                            .from(CourseResult.class)
                            .where(CourseResult_Table.term.eq(course.getTerm()))
                            .and(CourseResult_Table.crn.eq(course.getCRN()))
                            .hasData();
                    if (exists) {
                        coursesAdded ++;
                    }
                    course.save();
                }

                analytics.sendEvent("Search Results", "Add to Wishlist",
                        String.valueOf(coursesAdded));

                toastMessage = activity.getString(R.string.wishlist_add, coursesAdded);
            } else {
                toastMessage = activity.getString(R.string.wishlist_remove, courses.size());
                for (Course course : courses) {
                    course.delete();
                }
                // Get the term from the first course (they will all be in the same term)
                update(courses.get(0).getTerm());

                analytics.sendEvent("Wishlist", "Remove", String.valueOf(courses.size()));
            }
        }

        // Visual feedback of what was just done
        Utils.toast(activity, toastMessage);
    }
}