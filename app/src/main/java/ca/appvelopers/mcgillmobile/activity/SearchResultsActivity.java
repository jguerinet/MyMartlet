package ca.appvelopers.mcgillmobile.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.BaseActivity;
import ca.appvelopers.mcgillmobile.fragment.wishlist.WishlistSearchCourseAdapter;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.thread.RegistrationThread;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-20 10:10 AM
 * Copyright (c) 2015 Appvelopers. All rights reserved.
 * This will show the results of the search from the CourseSearchFragment
 */

public class SearchResultsActivity extends BaseActivity {
    private WishlistSearchCourseAdapter mAdapter;

    private Term mTerm;
    private List<ClassItem> mClasses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresults);

        GoogleAnalytics.sendScreen(this, "Search Results");

        //Set up the toolbar
        Toolbar toolbar = setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the term from the intent
        mTerm = (Term)getIntent().getSerializableExtra(Constants.TERM);

        //Get the list of classes from the Constants file
        mClasses = Constants.searchedClassItems;

        //Set the title
        toolbar.setTitle(mTerm.toString(this));

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

                    GoogleAnalytics.sendEvent(SearchResultsActivity.this, "Search Results",
                            "Add to Wishlist", "" + coursesAdded, null);

                    toastMessage = getResources().getString(R.string.wishlist_add, coursesAdded);
                }

                //Visual feedback of what was just done
                Toast.makeText(SearchResultsActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Registers to the given courses
    private void executeRegistrationThread(List<ClassItem> courses){
        new RegistrationThread(this, mTerm, courses){
            @Override
            protected void onPreExecute(){
                //Show the user we are refreshing
                showToolbarProgress(true);
            }

            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(Boolean success) {
                showToolbarProgress(false);

                if(success) {
                    //Display whether the user was successfully registered
                    if (mRegistrationErrors.isEmpty()) {
                        Toast.makeText(SearchResultsActivity.this, R.string.registration_success,
                                Toast.LENGTH_LONG).show();
                    }

                    //Display a message if a registration error has occurred
                    else {
                        List<ClassItem> unregisteredCourses = new ArrayList<ClassItem>();
                        String errorMessage = "";

                        //Go through the list of errors and create the error message
                        for (String crn : mRegistrationErrors.keySet()) {
                            //Find the right class
                            for (ClassItem classItem : mRegistrationCourses) {
                                if (classItem.getCRN() == Integer.valueOf(crn)) {
                                    //Add it to the list of registered courses
                                    unregisteredCourses.add(classItem);

                                    //Add this class to the error message
                                    errorMessage += classItem.getCourseCode() + " ("
                                            + classItem.getSectionType() + ") - " + mRegistrationErrors.get(crn) + "\n";

                                    break;
                                }
                            }
                        }

                        //Remove all of the unregistered courses from the list of registered courses
                        mRegistrationCourses.removeAll(unregisteredCourses);

                        //Show success messages for the correctly registered courses
                        for (ClassItem classItem : mRegistrationCourses) {
                            errorMessage += classItem.getCourseCode() + " (" +
                                    classItem.getSectionType() + ") - " + getString(R.string.registration_success) + "\n";
                        }

                        //Show an alert dialog with the errors
                        DialogHelper.showNeutralAlertDialog(SearchResultsActivity.this,
                                getString(R.string.registration_error), errorMessage);
                    }

                    //Remove the courses from the wishlist if they were there
                    List<ClassItem> wishlist = App.getClassWishlist();
                    wishlist.removeAll(mRegistrationCourses);

                    //Set the new wishlist
                    App.setClassWishlist(mClasses);
                }
            }
        }.execute();
    }
}