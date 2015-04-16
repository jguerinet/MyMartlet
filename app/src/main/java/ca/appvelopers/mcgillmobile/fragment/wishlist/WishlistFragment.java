package ca.appvelopers.mcgillmobile.fragment.wishlist;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.dialog.ChangeSemesterDialog;
import ca.appvelopers.mcgillmobile.fragment.BaseFragment;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Course;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.thread.RegistrationThread;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 4:56 PM
 * Copyright (c) 2015 Appvelopers. All rights reserved.
 */

public class WishlistFragment extends BaseFragment {
    private ListView mListView;
    private WishlistSearchCourseAdapter mAdapter;

    private List<ClassItem> mClasses;
    private Term mTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(mActivity, R.layout.fragment_wishlist, null);

        lockPortraitMode();

        GoogleAnalytics.sendScreen(mActivity, "Wishlist");

        //Check if there are any terms to register for
        if(App.getRegisterTerms().isEmpty()){
            //Hide all of the main content, show explanatory text, and return the view
            TextView noSemesters = (TextView)view.findViewById(R.id.registration_no_semesters);
            noSemesters.setVisibility(View.VISIBLE);

            RelativeLayout registrationContainer = (RelativeLayout)view.findViewById(
                    R.id.main_container);
            registrationContainer.setVisibility(View.GONE);

            //Hide the loading indicator
            hideLoadingIndicator();

            return view;
        }

        // Views
        mListView = (ListView)view.findViewById(R.id.courses_list);
        mListView.setEmptyView(view.findViewById(R.id.courses_empty));

        //Load the first registration term
        mTerm = App.getRegisterTerms().get(0);

        //Load the wishlist
        mClasses = App.getClassWishlist();

        //Register button
        TextView registerButton = (TextView) view.findViewById(R.id.course_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get checked courses from adapter
                List<ClassItem> registerCoursesList = mAdapter.getCheckedClasses();

                //Too many courses
                if (registerCoursesList.size() > 10) {
                    Toast.makeText(mActivity, getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                }
                //No Courses
                else if (registerCoursesList.isEmpty()) {
                    Toast.makeText(mActivity, getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                }
                //Execute registration of checked classes in a new thread
                else if (registerCoursesList.size() > 0) {
                    executeRegistrationThread(registerCoursesList);
                }
            }
        });

        //Remove from Wishlist Button
        TextView wishlistButton = (TextView)view.findViewById(R.id.course_wishlist);
        wishlistButton.setText(getResources().getString(R.string.courses_remove_wishlist));
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
                else {
                    toastMessage = getResources().getString(R.string.wishlist_remove, checkedClasses.size());
                    mClasses.removeAll(checkedClasses);

                    //Save the courses to the App context
                    App.setClassWishlist(mClasses);

                    GoogleAnalytics.sendEvent(mActivity, "Wishlist", "Remove", "" + checkedClasses.size(),
                            null);

                    //Reload the adapter
                    loadInfo();
                }

                //Visual feedback of what was just done
                Toast.makeText(mActivity, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        //Update the wishlist
        new WishlistThread().execute();

        //Hide the loading indicator
        hideLoadingIndicator();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    private void loadInfo(){
        //Only load the info if there is info to load
        if(!App.getRegisterTerms().isEmpty()){
            //Set the title
            mActivity.setTitle(mTerm.toString(mActivity));

            //Reload the adapter
            mAdapter = new WishlistSearchCourseAdapter(mActivity, mTerm, mClasses);
            mListView.setAdapter(mAdapter);
        }
    }

    // JDAlfaro
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //Only inflate the menu with the change semester if there is more than 1 semester to
        //  register for
        if(App.getRegisterTerms().size() > 1){
            inflater.inflate(R.menu.refresh_change_semester, menu);
        }
        //If there is at least one semester to register for, show the refresh button
        else if(!App.getRegisterTerms().isEmpty()){
            inflater.inflate(R.menu.refresh, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_change_semester){
            final ChangeSemesterDialog dialog = new ChangeSemesterDialog(mActivity, true, mTerm);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Term term = dialog.getTerm();

                    //If there is a term selected, refresh the view
                    if(term != null){
                        mTerm = term;
                        loadInfo();
                    }
                }
            });
            dialog.show();
            return true;
        }
        else if(item.getItemId() == R.id.action_refresh){
            new WishlistThread().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    //Registers to the given courses
    private void executeRegistrationThread(List<ClassItem> courses){
        new RegistrationThread(mActivity, mTerm, courses){
            @Override
            protected void onPreExecute(){
                //Show the user we are refreshing
                mActivity.showToolbarProgress(true);
            }

            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(Boolean success) {
                mActivity.showToolbarProgress(false);

                if(success) {
                    //Display whether the user was successfully registered
                    if (mRegistrationErrors.isEmpty()) {
                        Toast.makeText(mActivity, R.string.registration_success, Toast.LENGTH_LONG).show();
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
                        DialogHelper.showNeutralAlertDialog(mActivity, getString(R.string.registration_error),
                                errorMessage);
                    }

                    //Remove the courses from the wishlist if they were there
                    mClasses.removeAll(mRegistrationCourses);

                    //Set the new wishlist
                    App.setClassWishlist(mClasses);

                    //Reload the adapter
                    loadInfo();
                }
            }
        }.execute();
    }

    //Update the wishlist
    private class WishlistThread extends AsyncTask<Void, Void, Boolean> {
        public WishlistThread(){}

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            mActivity.showToolbarProgress(true);
        }

        //Retrieve page that contains registration status from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            //Sort ClassItems into Courses
            List<Course> coursesList = new ArrayList<Course>();
            for(ClassItem wishlistClass : mClasses){
                boolean courseExists = false;
                //Check if course exists in list
                for(Course addedCourse : coursesList){
                    if(addedCourse.getCourseCode().equals(wishlistClass.getCourseCode())){
                        courseExists = true;
                    }
                }
                //Add course if it has not already been added
                if(!courseExists){
                    coursesList.add(new Course(wishlistClass.getTerm(), wishlistClass.getCourseTitle(),
                            wishlistClass.getCourseCode(), wishlistClass.getCredits(), "N/A", "N/A"));
                }
            }

            //For each course, obtain its Minerva registration page
            for(Course course : coursesList){
                //Get the course registration URL
                String courseCode[] = course.getCourseCode().split(" ");
                String courseSubject;
                String courseNumber;

                //Check that the course code has been split successfully
                if(courseCode.length > 1){
                    courseSubject = courseCode[0];
                    courseNumber = courseCode[1];
                } else{
                    //TODO: Return indication of failure
                    return false;
                }

                String registrationUrl = Connection.getCourseURL(course.getTerm(),
                        courseSubject, null, courseNumber,
                        0, 0, 0, 0, '0', 0, 0, '0', null);

                String classesString = Connection.getInstance().getUrl(mActivity, registrationUrl);

                //TODO: Figure out a way to parse only some course sections instead of re-parsing all course sections for a given Course
                //This parses all ClassItems for a given course
                List<ClassItem> updatedClassList = Parser.parseClassResults(course.getTerm(), classesString);

                //Update the course object with an updated class size
                for(ClassItem updatedClass : updatedClassList){

                    for(ClassItem wishlistClass : mClasses){

                        if(wishlistClass.getCRN() == updatedClass.getCRN()){
                            wishlistClass.setDays(updatedClass.getDays());
                            wishlistClass.setStartTime(updatedClass.getStartTime());
                            wishlistClass.setEndTime(updatedClass.getEndTime());
                            wishlistClass.setDates(updatedClass.getDates());
                            wishlistClass.setInstructor(updatedClass.getInstructor());
                            wishlistClass.setLocation(updatedClass.getLocation());
                            wishlistClass.setSeatsRemaining(updatedClass.getSeatsRemaining());
                            wishlistClass.setWaitlistRemaining(updatedClass.getWaitlistRemaining());
                        }
                    }
                }
            }

            return true;
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean success){
            mActivity.showToolbarProgress(false);

            if(success){
                //Set the new wishlist
                App.setClassWishlist(mClasses);
                //Reload the adapter
                loadInfo();
            }
        }
    }
}