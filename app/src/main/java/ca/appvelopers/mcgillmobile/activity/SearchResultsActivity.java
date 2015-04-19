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

package ca.appvelopers.mcgillmobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.BaseActivity;
import ca.appvelopers.mcgillmobile.fragment.wishlist.WishlistSearchCourseAdapter;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.thread.RegistrationThread;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Shows the results of the search from the CourseSearchFragment
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
@SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter"})
public class SearchResultsActivity extends BaseActivity {
    /**
     * The adapter for the list of results
     */
    private WishlistSearchCourseAdapter mAdapter;
    /**
     * The current term
     */
    private Term mTerm;
    /**
     * The list of classes
     */
    private List<ClassItem> mClasses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);

        Analytics.getInstance().sendScreen("Search Results");

        //Set up the toolbar
        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the info from the intent
        mTerm = (Term)getIntent().getSerializableExtra(Constants.TERM);
        mClasses = (ArrayList<ClassItem>)getIntent().getSerializableExtra(Constants.CLASSES);

        //Set the title
        setTitle(mTerm.toString(this));

        //ListView
        mAdapter = new WishlistSearchCourseAdapter(this, mTerm, mClasses);
        ListView listView = (ListView) findViewById(R.id.courses_list);
        listView.setEmptyView(findViewById(R.id.courses_empty));
        listView.setAdapter(mAdapter);

        //Register Button
        TextView registerButton = (TextView) findViewById(R.id.course_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get checked courses from adapter
                List<ClassItem> registerCoursesList = mAdapter.getCheckedClasses();

                //Too many courses
                if (registerCoursesList.size() > 10) {
                    Toast.makeText(SearchResultsActivity.this, getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                }
                //No Courses
                else if (registerCoursesList.isEmpty()) {
                    Toast.makeText(SearchResultsActivity.this, getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                }
                //Execute registration of checked classes in a new thread
                else if (registerCoursesList.size() > 0) {
                    executeRegistrationThread(registerCoursesList);
                }
            }
        });

        //Add to Wishlist Button
        TextView wishlistButton = (TextView)findViewById(R.id.course_wishlist);
        wishlistButton.setText(getResources().getString(R.string.courses_add_wishlist));
        wishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the checked list of courses from the adapter
                List<ClassItem> checkedClasses = mAdapter.getCheckedClasses();

                String toastMessage;
                //If there are none, display error message
                if (checkedClasses.isEmpty()) {
                    toastMessage = getResources().getString(R.string.courses_none_selected);
                }
                //If not, it's to add a course to the wishlist
                else {
                    //Get the wishlist courses
                    List<ClassItem> wishlist = App.getClassWishlist();

                    //Only add it if it's not already part of the wishlist
                    int coursesAdded = 0;
                    for (ClassItem classItem : checkedClasses) {
                        if (!wishlist.contains(classItem)) {
                            wishlist.add(classItem);
                            coursesAdded++;
                        }
                    }

                    //Save the courses to the App context
                    App.setClassWishlist(wishlist);

                    Analytics.getInstance().sendEvent("Search Results", "Add to Wishlist",
                            String.valueOf(coursesAdded));

                    toastMessage = getResources().getString(R.string.wishlist_add, coursesAdded);
                }

                //Visual feedback of what was just done
                Toast.makeText(SearchResultsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Registers to the given courses
    private void executeRegistrationThread(List<ClassItem> courses){
        //Show the user we are refreshing
        showToolbarProgress(true);

        final RegistrationThread thread = new RegistrationThread(this, mTerm, courses);
        thread.start();

        synchronized(thread){
            //Wait for the thread to finish
            try{
                thread.wait();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        if(thread.success()) {
            Map<String, String> registrationErrors = thread.getRegistrationErrors();

            //Display whether the user was successfully registered
            if (registrationErrors.isEmpty()) {
                Toast.makeText(this, R.string.registration_success, Toast.LENGTH_LONG).show();
            }
            //Display a message if a registration error has occurred
            else {
                List<ClassItem> unregisteredCourses = new ArrayList<>();
                String errorMessage = "";

                //Go through the list of errors and create the error message
                for (String crn : registrationErrors.keySet()) {
                    //Find the right class
                    for (ClassItem classItem : mClasses) {
                        if (classItem.getCRN() == Integer.valueOf(crn)) {
                            //Add it to the list of registered courses
                            unregisteredCourses.add(classItem);

                            //Add this class to the error message
                            errorMessage += classItem.getCourseCode() + " ("
                                    + classItem.getSectionType() + ") - "
                                    + registrationErrors.get(crn) + "\n";

                            break;
                        }
                    }
                }

                //Remove all of the unregistered courses from the list of registered courses
                mClasses.removeAll(unregisteredCourses);

                //Show success messages for the correctly registered courses
                for (ClassItem classItem : mClasses) {
                    errorMessage += classItem.getCourseCode() + " (" +
                            classItem.getSectionType() + ") - " + getString(R.string.registration_success) + "\n";
                }

                //Show an alert dialog with the errors
                DialogHelper.showNeutralAlertDialog(this, getString(R.string.registration_error),
                        errorMessage);
            }

            //Remove the courses from the wishlist if they were there
            List<ClassItem> wishlist = App.getClassWishlist();
            wishlist.removeAll(mClasses);

            //Set the new wishlist
            App.setClassWishlist(mClasses);
        }

        //Stop the refreshing
        showToolbarProgress(false);
    }
}