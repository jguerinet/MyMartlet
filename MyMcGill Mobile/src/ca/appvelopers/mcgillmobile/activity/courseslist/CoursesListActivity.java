package ca.appvelopers.mcgillmobile.activity.courseslist;


import android.app.Activity;
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

import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.ChangeSemesterActivity;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.ClassItem;
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
    public CourseListType listType;

    private List<ClassItem> mClasses;
    private ListView mListView;
    private String mRegistrationError = "NULL";
    private ClassAdapter mAdapter;
    private Term mTerm;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_courseslist);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        listType = (CourseListType)getIntent().getSerializableExtra(Constants.LIST_TYPE);

        super.onCreate(savedInstanceState);

        if(listType == CourseListType.WISHLIST){
            GoogleAnalytics.sendScreen(this, "Wishlist");
        }
        else if(listType == CourseListType.VIEW_COURSES){
            GoogleAnalytics.sendScreen(this, "View Courses");
        }
        else{
            GoogleAnalytics.sendScreen(this, "Search Results");
        }

        // Views
        mListView = (ListView)findViewById(R.id.courses_list);
        mListView.setEmptyView(findViewById(R.id.courses_empty));

        mTerm = App.getDefaultTerm();

        //Check if we need to load the wishlist
        if(listType == CourseListType.WISHLIST){
            mClasses = App.getClassWishlist();
        }
        //Check if we need to get the searched courses
        else if (listType == CourseListType.SEARCH_COURSES){
            mClasses = Constants.searchedClassItems;
        }
        //If not, just load all of the courses
        else{
            mClasses = App.getClasses();
        }

        //Register button
        TextView register = (TextView)findViewById(R.id.course_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get checked courses from adapter
                List<ClassItem> registerCoursesList = mAdapter.getCheckedClasses();

                //Too many courses
                if (registerCoursesList.size() > 10){
                    Toast.makeText(CoursesListActivity.this, getResources().getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                }
                else if(registerCoursesList.isEmpty()){
                    Toast.makeText(CoursesListActivity.this, getResources().getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                }
                else if(registerCoursesList.size() > 0) {
                    //Execute registration of checked classes in a new thread
                    new RegistrationThread(Connection.getRegistrationURL(mTerm, registerCoursesList, false)).execute();
                }
            }
        });

        //Add/Remove to/from Wishlist Button
        TextView wishlist = (TextView)findViewById(R.id.course_wishlist);
        if(listType == CourseListType.WISHLIST){
            wishlist.setText(getResources().getString(R.string.courses_remove_wishlist));
        }
        else if(listType == CourseListType.SEARCH_COURSES){
            wishlist.setText(getResources().getString(R.string.courses_add_wishlist));
        }
        else{
            //TODO
        }

        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the checked list of courses from the adapter
                List<ClassItem> checkedClasses = mAdapter.getCheckedClasses();

                String toastMessage;

                //If there are none, display error message
                if(checkedClasses.isEmpty()){
                    toastMessage = getResources().getString(R.string.wishlist_error_empty);
                }
                //If we are in the wishlist, this button is to remove a course
                else if(listType == CourseListType.WISHLIST){
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
                else{
                    //Get the wishlist courses
                    List<ClassItem> wishlist = App.getClassWishlist();

                    //Only add it if it's not already part of the wishlist
                    int coursesAdded = 0;
                    for(ClassItem classItem : checkedClasses){
                        if(!wishlist.contains(classItem)){
                            wishlist.add(classItem);
                            coursesAdded ++;
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
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    private void loadInfo(){
        mAdapter = new ClassAdapter(this, mTerm, mClasses);
        mListView.setAdapter(mAdapter);
    }

    /*@Override
    public void onBackPressed(){
        startActivity(new Intent(CoursesListActivity.this, App.getHomePage().getHomePageClass()));
        super.onBackPressed();
    }*/

//    @Override
//    public void onBackPressed(){
//        super.onBackPressed();
//        overridePendingTransition(R.anim.left_in, R.anim.right_out);
//    }

    // JDAlfaro
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //Change Semester Menu Item - Not for Search Results
        if(listType != CourseListType.SEARCH_COURSES){
            menu.add(Menu.NONE, Constants.MENU_ITEM_CHANGE_SEMESTER, Menu.NONE, R.string.schedule_change_semester);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == Constants.MENU_ITEM_CHANGE_SEMESTER){
            Intent intent = new Intent(this, ChangeSemesterActivity.class);
            boolean registerTerms = listType == CourseListType.WISHLIST;
            intent.putExtra(Constants.REGISTER_TERMS, registerTerms);
            startActivityForResult(intent, CHANGE_SEMESTER_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CHANGE_SEMESTER_CODE){
            if(resultCode == RESULT_OK){
                mTerm = ((Term)data.getSerializableExtra(Constants.TERM));
                loadInfo();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public enum CourseListType{
        SEARCH_COURSES,
        VIEW_COURSES,
        WISHLIST
    }

    //Connects to Minerva in a new thread to register for courses
    private class RegistrationThread extends AsyncTask<Void, Void, Boolean> {
        private String mRegistrationURL;

        public RegistrationThread(String registrationURL){
            this.mRegistrationURL = registrationURL;
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
                        Activity activity = CoursesListActivity.this;
                        try {
                            DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                    activity.getResources().getString(R.string.error_other));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
            //Otherwise, check for errors
            else{
                mRegistrationError = Parser.parseRegistrationErrors(resultString);
                return true;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean success){
            setProgressBarIndeterminateVisibility(false);

            if(success){
                //Display whether the user was successfully registered
                if(mRegistrationError.equals("NULL")){
                    Toast.makeText(CoursesListActivity.this, R.string.registration_success, Toast.LENGTH_LONG).show();
                }

                //Display a message if a registration error has occurred
                else{
                    Toast.makeText(CoursesListActivity.this, getResources().getString(R.string.registration_error,
                            mRegistrationError), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}