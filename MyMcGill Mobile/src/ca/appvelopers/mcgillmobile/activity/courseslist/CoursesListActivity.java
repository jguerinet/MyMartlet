package ca.appvelopers.mcgillmobile.activity.courseslist;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.ChangeSemesterActivity;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Course;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author : Julien
 * Date :  2014-05-26 7:09 PM
 * Shows a list of courses
 */
public class CoursesListActivity extends DrawerActivity {
    public static final int CHANGE_SEMESTER_CODE = 100;
    public boolean wishlist;

    private ListView mListView;
    private ClassAdapter mAdapter;

    private List<ClassItem> mClasses;
    private Term mTerm;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_courseslist);

        wishlist = getIntent().getBooleanExtra(Constants.WISHLIST, true);

        super.onCreate(savedInstanceState);

        if(wishlist){
            GoogleAnalytics.sendScreen(this, "Wishlist");
        }
        else{
            GoogleAnalytics.sendScreen(this, "Search Results");
        }

        // Views
        mListView = (ListView)findViewById(R.id.courses_list);
        mListView.setEmptyView(findViewById(R.id.courses_empty));

        //Get the term from the intent (From the course search
        mTerm = (Term)getIntent().getSerializableExtra(Constants.TERM);
        //If it's null, just load the default term
        if(mTerm == null){
            mTerm = App.getDefaultTerm();
        }

        //Check if we need to load the wishlist
        if(wishlist){
            mClasses = App.getClassWishlist();
        }
        //Get the searched courses
        else{
            mClasses = Constants.searchedClassItems;
        }

        //Register button
        TextView registerButton = (TextView) findViewById(R.id.course_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get checked courses from adapter
                List<ClassItem> registerCoursesList = mAdapter.getCheckedClasses();

                //Too many courses
                if (registerCoursesList.size() > 10) {
                    Toast.makeText(CoursesListActivity.this, getResources().getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                } else if (registerCoursesList.isEmpty()) {
                    Toast.makeText(CoursesListActivity.this, getResources().getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                } else if (registerCoursesList.size() > 0) {
                    //Execute registration of checked classes in a new thread
                    new RegistrationThread(registerCoursesList).execute();
                }
            }
        });

        //Add/Remove to/from Wishlist Button
        TextView wishlistButton = (TextView)findViewById(R.id.course_wishlist);
        if(wishlist){
            wishlistButton.setText(getResources().getString(R.string.courses_remove_wishlist));
        }
        else{
            wishlistButton.setText(getResources().getString(R.string.courses_add_wishlist));
        }
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
                //If we are in the wishlist, this button is to remove a course
                else if (wishlist) {
                    toastMessage = getResources().getString(R.string.wishlist_remove, checkedClasses.size());
                    mClasses.removeAll(checkedClasses);

                    //Save the courses to the App context
                    App.setClassWishlist(mClasses);

                    GoogleAnalytics.sendEvent(CoursesListActivity.this, "Wishlist", "Remove",
                            "" + checkedClasses.size(), null);

                    //Reload the adapter
                    loadInfo();
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

                    GoogleAnalytics.sendEvent(CoursesListActivity.this, "Search Results", "Add to Wishlist",
                            "" + coursesAdded, null);

                    toastMessage = getResources().getString(R.string.wishlist_add, coursesAdded);
                }

                //Visual feedback of what was just done
                Toast.makeText(CoursesListActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        if(wishlist){
            //Update the wishlist
            new WishlistThread().execute();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    @Override
    public void onBackPressed(){
        if(!wishlist){
            finish();
        }
        else{
            super.onBackPressed();
        }
    }

    private void loadInfo(){
        //Set the title
        setTitle(mTerm.toString(this));

        //Reload the adapter
        mAdapter = new ClassAdapter(this, mTerm, mClasses);
        mListView.setAdapter(mAdapter);
    }

    // JDAlfaro
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Inflate the refresh button only if we are in the wishlist
        if(wishlist){
            getMenuInflater().inflate(R.menu.refresh, menu);
        }

        //Change Semester Menu Item - Not for Search Results
        if(wishlist){
            menu.add(Menu.NONE, Constants.MENU_ITEM_CHANGE_SEMESTER, Menu.NONE, R.string.schedule_change_semester);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == Constants.MENU_ITEM_CHANGE_SEMESTER){
            Intent intent = new Intent(this, ChangeSemesterActivity.class);
            intent.putExtra(Constants.REGISTER_TERMS, true);
            intent.putExtra(Constants.TERM, mTerm);
            startActivityForResult(intent, CHANGE_SEMESTER_CODE);
            return true;
        }
        else if(item.getItemId() == R.id.action_refresh){
            new WishlistThread().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CHANGE_SEMESTER_CODE){
            if(resultCode == RESULT_OK){
                mTerm = (Term)data.getSerializableExtra(Constants.TERM);
                loadInfo();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Connects to Minerva in a new thread to register for courses
    private class RegistrationThread extends AsyncTask<Void, Void, Boolean> {
        private String mRegistrationURL;
        private List<ClassItem> mRegistrationCourses;
        private Map<String, String> mRegistrationErrors = null;

        public RegistrationThread(List<ClassItem> courses){
            this.mRegistrationCourses = courses;
            this.mRegistrationURL = Connection.getRegistrationURL(mTerm, mRegistrationCourses, false);
            this.mRegistrationErrors = null;
        }

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve page that contains registration status from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            String resultString = Connection.getInstance().getUrl(CoursesListActivity.this, mRegistrationURL);

            //If result string is null, there was an error
            if(resultString == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DialogHelper.showNeutralAlertDialog(CoursesListActivity.this, getString(R.string.error),
                                    getString(R.string.error_other));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
            //Otherwise, check for errors
            else{
                mRegistrationErrors = Parser.parseRegistrationErrors(resultString);
                return true;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean success){
            setProgressBarIndeterminateVisibility(false);

            if(success){
                //Display whether the user was successfully registered
                if(mRegistrationErrors.isEmpty()){
                    Toast.makeText(CoursesListActivity.this, R.string.registration_success, Toast.LENGTH_LONG).show();
                }

                //Display a message if a registration error has occurred
                else{
                    List<ClassItem> unregisteredCourses = new ArrayList<ClassItem>();
                    String errorMessage = "";

                    //Go through the list of errors
                    for(String crn : mRegistrationErrors.keySet()){
                        //Find the right class
                        for(ClassItem classItem : mRegistrationCourses){
                            if(classItem.getCRN() == Integer.valueOf(crn)){
                                //Add it to the list of registered courses
                                unregisteredCourses.add(classItem);

                                //Add this class to the error message
                                errorMessage += classItem.getCourseCode() +  " ("
                                        + classItem.getSectionType() + ") - " + mRegistrationErrors.get(crn) + "\n";

                                break;
                            }
                        }
                    }

                    //Remove all of the unregistered courses from the list of registered courses
                    mRegistrationCourses.removeAll(unregisteredCourses);

                    //Show success messages for the correctly registered courses
                    for(ClassItem classItem : mRegistrationCourses){
                        errorMessage += classItem.getCourseCode() + " (" +
                                classItem.getSectionType() + ") - " + getString(R.string.registration_success) + "\n";
                    }

                    //Show an alert dialog with the errors
                    DialogHelper.showNeutralAlertDialog(CoursesListActivity.this, getString(R.string.registration_error),
                            errorMessage);
                }

                //Get the list of wishlist classes
                List<ClassItem> wishlistClasses = wishlist ? mClasses : App.getClassWishlist();

                //Remove the courses from the wishlist if they were there
                wishlistClasses.removeAll(mRegistrationCourses);

                //Set the new wishlist
                App.setClassWishlist(wishlistClasses);

                //Reload the adapter
                loadInfo();
            }
        }
    }

    //Update the wishlist
    private class WishlistThread extends AsyncTask<Void, Void, Boolean> {

        public WishlistThread(){}

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve page that contains registration status from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            //Get list of courses in wishlist

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
                            wishlistClass.getCourseCode(), 99, "N/A", "N/A"));
                }
            }

            //For each course, obtain its Minerva registration page
            for(Course course : coursesList){

                //Get the course registration URL
                String courseCode[] = course.getCourseCode().split(" ");
                String courseSubject = "";
                String courseNumber = "";

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
                        "", 0, 0, 0, 0, 0, 0, null);

                String classesString = Connection.getInstance().getUrl(CoursesListActivity.this, registrationUrl);

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
            setProgressBarIndeterminateVisibility(false);

            if(success){
                //Set the new wishlist
                App.setClassWishlist(mClasses);
                //Reload the adapter
                loadInfo();
            }
        }
    }
}