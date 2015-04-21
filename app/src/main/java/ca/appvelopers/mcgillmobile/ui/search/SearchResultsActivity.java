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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.thread.DownloaderThread;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.view.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistSearchCourseAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Parser;

/**
 * Shows the results of the search from the CourseSearchFragment
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class SearchResultsActivity extends BaseActivity {
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

        Analytics.getInstance().sendScreen("Search Results");

        //Set up the toolbar
        setUpToolbar(true);

        //Get the info from the intent
        mTerm = (Term)getIntent().getSerializableExtra(Constants.TERM);
        List<Course> courses =
                (ArrayList<Course>)getIntent().getSerializableExtra(Constants.CLASSES);

        //Set the title
        setTitle(mTerm.toString(this));

        //ListView
        mAdapter = new WishlistSearchCourseAdapter(this, mTerm, courses);
        ListView listView = (ListView) findViewById(R.id.courses_list);
        listView.setEmptyView(findViewById(R.id.courses_empty));
        listView.setAdapter(mAdapter);

        //Register Button
        TextView registerButton = (TextView) findViewById(R.id.course_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Get checked courses from adapter
                List<Course> courses = mAdapter.getCheckedClasses();

                //Too many courses
                if(courses.size() > 10){
                    Toast.makeText(SearchResultsActivity.this,
                            getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                }
                //No Courses
                else if(courses.isEmpty()){
                    Toast.makeText(SearchResultsActivity.this,
                            getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                }
                //Execute registration of checked classes in a new thread
                else if(courses.size() > 0){
                    //Show the user we are refreshing
                    showToolbarProgress(true);

                    String html = new DownloaderThread(SearchResultsActivity.this, "Registration",
                            Connection.getRegistrationURL(mTerm, courses, false)).execute();

                    if(html != null){
                        String error = Parser.parseRegistrationErrors(html, courses);

                        //If there are no errors, show the success message
                        if(error == null){
                            Toast.makeText(SearchResultsActivity.this,
                                    R.string.registration_success, Toast.LENGTH_LONG).show();
                        }
                        //If not, show the error message
                        else{
                            //Show success messages for the correctly registered courses
                            for(Course course : courses){
                                error += course.getCode() + " (" +  course.getType() + ") - " +
                                        getString(R.string.registration_success) + "\n";
                            }

                            //Show an alert dialog with the errors
                            DialogHelper.showNeutralAlertDialog(SearchResultsActivity.this,
                                    getString(R.string.registration_error), error);
                        }

                        //Remove the courses from the wishlist if they were there
                        List<Course> wishlist = App.getClassWishlist();
                        wishlist.removeAll(courses);

                        //Set the new wishlist
                        App.setClassWishlist(wishlist);
                    }

                    //Stop the refreshing
                    showToolbarProgress(false);
                }
            }
        });

        //Add to Wishlist Button
        TextView wishlistButton = (TextView)findViewById(R.id.course_wishlist);
        wishlistButton.setText(getString(R.string.courses_add_wishlist));
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the checked list of courses from the adapter
                List<Course> checkedCourses = mAdapter.getCheckedClasses();

                String toastMessage;
                //If there are none, display error message
                if (checkedCourses.isEmpty()) {
                    toastMessage = getString(R.string.courses_none_selected);
                }
                //If not, it's to add a course to the wishlist
                else {
                    //Get the wishlist courses
                    List<Course> wishlist = App.getClassWishlist();

                    //Only add it if it's not already part of the wishlist
                    int coursesAdded = 0;
                    for (Course course : checkedCourses) {
                        if (!wishlist.contains(course)) {
                            wishlist.add(course);
                            coursesAdded++;
                        }
                    }

                    //Save the courses to the App context
                    App.setClassWishlist(wishlist);

                    Analytics.getInstance().sendEvent("Search Results", "Add to Wishlist",
                            String.valueOf(coursesAdded));

                    toastMessage = getString(R.string.wishlist_add, coursesAdded);
                }

                //Visual feedback of what was just done
                Toast.makeText(SearchResultsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}