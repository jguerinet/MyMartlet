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

package ca.appvelopers.mcgillmobile.ui.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.RegistrationError;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistSearchCourseAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
import ca.appvelopers.mcgillmobile.util.retrofit.McGillService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Shows the results of the search from the SearchActivity
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class RegistrationView extends LinearLayout {
    /**
     * Empty view
     */
    @BindView(android.R.id.empty)
    TextView emptyView;
    /**
     * Courses list
     */
    @BindView(android.R.id.list)
    RecyclerView listView;
    McGillService mcGillService;
    Analytics analytics;
    private final BaseActivity activity;
    private final boolean add;
    /**
     * The adapter for the list of results
     */
    private final WishlistSearchCourseAdapter adapter;
    /**
     * The current term
     */
    private Term term;

    public RegistrationView(BaseActivity activity, boolean add) {
        super(activity);
        this.activity = activity;
        this.add = add;
        // TODO
//        setContentView(R.layout.activity_searchresults);
        ButterKnife.bind(this);

        // ListView
        listView.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new WishlistSearchCourseAdapter(activity);
        listView.setAdapter(adapter);
    }

    public void update(Term term, List<CourseResult> courses) {
        this.term = term;
        adapter.update(term, courses);
    }

    @OnClick(R.id.course_register)
    void registerButton() {
        List<CourseResult> courses = adapter.getCheckedCourses();
        if (courses.size() > 10) {
            // Too many courses
            Utils.toast(getContext(), R.string.courses_too_many_courses);
        } else if (courses.isEmpty()) {
            // No Courses
            Utils.toast(getContext(), R.string.courses_none_selected);
        } else if (courses.size() > 0) {
            // Execute registration of checked classes in a new thread
            if (!activity.canRefresh()) {
                // Check that we can continue
                return;
            }

            // Confirm with the user before continuing
            DialogUtils.alert(getContext(), R.string.warning, R.string.registration_disclaimer,
                    (dialogInterface, i) -> {
                        if (i == DialogInterface.BUTTON_POSITIVE) {
                            register(courses);
                        } else {
                            activity.showToolbarProgress(false);
                        }
                    });
        }
    }

    @OnClick(R.id.course_wishlist)
    void wishlistButton() {
        addToWishlist(adapter.getCheckedCourses());
    }

    private void register(final List<CourseResult> courses) {
        List<Course> theCourses = new ArrayList<>();
        theCourses.addAll(courses);
        mcGillService.registration(McGillManager.getRegistrationURL(term, theCourses, false))
                .enqueue(new Callback<List<RegistrationError>>() {
                    @Override
                    public void onResponse(Call<List<RegistrationError>> call,
                            Response<List<RegistrationError>> response) {
                        activity.showToolbarProgress(false);

                        // If there are no errors, show the success message
                        if (response.body() == null || response.body().isEmpty()) {
                            Utils.toast(getContext(), R.string.registration_success);

                            // Remove the courses from the wishlist if they were there
                            List<CourseResult> wishlist = App.getWishlist();
                            wishlist.removeAll(courses);

                            // Set the new wishlist
                            App.setWishlist(wishlist);
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

                        DialogHelper.error(getContext(), errorMessage);
                    }

                    @Override
                    public void onFailure(Call<List<RegistrationError>> call, Throwable t) {
                        Timber.e(t, "Error (un)registering for courses");
                        activity.showToolbarProgress(false);
                        // If this is a MinervaException, broadcast it
                        if (t instanceof MinervaException) {
                            LocalBroadcastManager.getInstance(getContext())
                                    .sendBroadcast(new Intent(Constants.BROADCAST_MINERVA));
                        } else {
                            DialogHelper.error(getContext(), R.string.error_other);
                        }
                    }
                });
    }

    /**
     * Adds/removes the given courses to/from the wishlist
     *
     * @param courses List of {@link CourseResult}s to add/remove
     */
    private void addToWishlist(List<CourseResult> courses) {
        String toastMessage;
        if (courses.isEmpty()) {
            // If there are none, display error message
            toastMessage = activity.getString(R.string.courses_none_selected);
        } else {
            // If not, it's to add a course to the wishlist
            //  Get the wishlist courses
            List<CourseResult> wishlist = App.getWishlist();

            if (add) {
                // Only add it if it's not already part of the wishlist
                int coursesAdded = 0;
                for (CourseResult course : courses) {
                    if (!wishlist.contains(course)) {
                        wishlist.add(course);
                        coursesAdded ++;
                    }
                }

                analytics.sendEvent("Search Results", "Add to Wishlist",
                        String.valueOf(coursesAdded));

                toastMessage = getContext().getString(R.string.wishlist_add, coursesAdded);
            } else {
                toastMessage = getContext().getString(R.string.wishlist_remove, courses.size());
                wishlist.removeAll(courses);

                analytics.sendEvent("Wishlist", "Remove", String.valueOf(courses.size()));
            }

            // Save the courses to the App context
            App.setWishlist(wishlist);
        }

        // Visual feedback of what was just done
        Utils.toast(getContext(), toastMessage);
    }
}