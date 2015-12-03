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

package ca.appvelopers.mcgillmobile.ui.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistSearchCourseAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Shows the results of the search from the CourseSearchFragment
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SearchResultsActivity extends BaseActivity {
    /**
     * The courses list
     */
    @Bind(android.R.id.list)
    RecyclerView mListView;
    /**
     * The empty view
     */
    @Bind(R.id.courses_empty)
    TextView mEmptyView;
    /**
     * The adapter for the list of results
     */
    private WishlistSearchCourseAdapter mAdapter;
    /**
     * The current term
     */
    private Term mTerm;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);
        ButterKnife.bind(this);
        Analytics.getInstance().sendScreen("Search Results");
        setUpToolbar(true);

        //Get the info from the intent
        mTerm = (Term)getIntent().getSerializableExtra(Constants.TERM);
        List<Course> courses =
                (ArrayList<Course>)getIntent().getSerializableExtra(Constants.COURSES);

        //Set the title
        setTitle(mTerm.toString());

        //ListView
        mAdapter = new WishlistSearchCourseAdapter(this, mTerm, courses);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(mAdapter);
        if(mAdapter.isEmpty()){
            mListView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.course_register)
    void registerButton(){
        register(this, mTerm, mAdapter.getCheckedCourses());
    }

    @OnClick(R.id.course_wishlist)
    void wishlistButton(){
        addToWishlist(this, mAdapter.getCheckedCourses(), true);
    }

    /**
     * Tries to register the users to the given courses
     *
     * @param activity The calling activity
     * @param term     The concerned term
     * @param courses  The list of courses
     */
    public static void register(final BaseActivity activity, Term term, final List<Course> courses){
        //Too many courses
        if(courses.size() > 10){
            Toast.makeText(activity, activity.getString(R.string.courses_too_many_courses),
                    Toast.LENGTH_SHORT).show();
        }
        //No Courses
        else if(courses.isEmpty()){
            Toast.makeText(activity, activity.getString(R.string.courses_none_selected),
                    Toast.LENGTH_SHORT).show();
        }
        //Execute registration of checked classes in a new thread
        else if(courses.size() > 0){
            //Show the user we are refreshing
            activity.showToolbarProgress(true);

            new DownloaderThread(activity, Connection.getRegistrationURL(term, courses, false))
                    .execute(new DownloaderThread.Callback() {
                        @Override
                        public void onDownloadFinished(final String result){
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run(){
                                    if(result != null){
                                        String error =
                                                Parser.parseRegistrationErrors(result, courses);

                                        //If there are no errors, show the success message
                                        if(error == null){
                                            Toast.makeText(activity, R.string.registration_success,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        //If not, show the error message
                                        else{
                                            //Show success messages for the correctly registered
                                            // courses
                                            for(Course course : courses){
                                                error += course.getCode() + " (" + course.getType()
                                                        + ") - " + activity.getString(
                                                        R.string.registration_success) + "\n";
                                            }

                                            //Show an alert dialog with the errors
                                            DialogHelper.showNeutralDialog(activity,
                                                    activity.getString(R.string.registration_error),
                                                    error);
                                        }

                                        //Remove the courses from the wishlist if they were there
                                        List<Course> wishlist = App.getWishlist();
                                        wishlist.removeAll(courses);

                                        //Set the new wishlist
                                        App.setWishlist(wishlist);
                                    }

                                    //Stop the refreshing
                                    activity.showToolbarProgress(false);
                                }
                            });
                        }
                    });
        }
    }

    /**
     * Adds/removes the given courses to/from the wishlist
     *
     * @param activity The calling activity
     * @param courses  The courses to add/remove
     * @param add      True if we are adding courses, false if we're removing
     */
    public static void addToWishlist(BaseActivity activity, List<Course> courses, boolean add){
        String toastMessage;
        //If there are none, display error message
        if (courses.isEmpty()) {
            toastMessage = activity.getString(R.string.courses_none_selected);
        }
        //If not, it's to add a course to the wishlist
        else {
            //Get the wishlist courses
            List<Course> wishlist = App.getWishlist();

            if(add){
                //Only add it if it's not already part of the wishlist
                int coursesAdded = 0;
                for (Course course : courses) {
                    if (!wishlist.contains(course)) {
                        wishlist.add(course);
                        coursesAdded++;
                    }
                }

                Analytics.getInstance().sendEvent("Search Results", "Add to Wishlist",
                        String.valueOf(coursesAdded));

                toastMessage = activity.getString(R.string.wishlist_add, coursesAdded);
            }
            else{
                toastMessage = activity.getString(R.string.wishlist_remove, courses.size());
                wishlist.removeAll(courses);

                Analytics.getInstance().sendEvent("Wishlist", "Remove",
                        String.valueOf(courses.size()));
            }

            //Save the courses to the App context
            App.setWishlist(wishlist);
        }

        //Visual feedback of what was just done
        Toast.makeText(activity, toastMessage, Toast.LENGTH_SHORT).show();
    }
}