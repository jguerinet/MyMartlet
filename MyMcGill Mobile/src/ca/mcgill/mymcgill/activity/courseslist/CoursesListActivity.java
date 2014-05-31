package ca.mcgill.mymcgill.activity.courseslist;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.DialogHelper;

/**
 * Author : Julien
 * Date :  2014-05-26 7:09 PM
 * Shows a list of searchedCourses
 */
public class CoursesListActivity extends DrawerActivity {
    public boolean wishlist;

    private List<Course> mCourses;
    private ListView mListView;
    private CoursesAdapter mAdapter;
    private String mRegistrationUrl;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_courseslist);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        wishlist = getIntent().getBooleanExtra(Constants.WISHLIST, true);

        super.onCreate(savedInstanceState);

        // Views
        mListView = (ListView)findViewById(R.id.courses_list);
        mListView.setEmptyView(findViewById(R.id.courses_empty));

        //Check if we need to load the wishlist
        if(wishlist){
            mCourses = App.getCourseWishlist();
        }
        //If not, get the searched courses
        else{
            mCourses = Constants.searchedCourses;
        }

        //Register button
        TextView register = (TextView)findViewById(R.id.course_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get checked courses from adapter
                List<Course> registerCoursesList = mAdapter.getCheckedCourses();

                //Get term
                if (registerCoursesList.size() > 10){
                    String toastMessage = getResources().getString(R.string.registration_error_too_many_courses);
                    Toast.makeText(CoursesListActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }
                else if(registerCoursesList.size() > 0){
                    //Set up registration url
                    mRegistrationUrl = "https://horizon.mcgill.ca/pban1/bwckcoms.P_Regs?term_in=";

                    //Add term
                    //TODO: Add term to registration url
                    //mRegistrationUrl += <TERM>

                    //Add weird random Minerva code
                    mRegistrationUrl += "&RSTS_IN=DUMMY&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&REG_BTN=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&MESG=DUMMY&RSTS_IN=&assoc_term_in=DUMMY&CRN_IN=DUMMY&start_date_in=DUMMY&end_date_in=DUMMY&SUBJ=DUMMY&CRSE=DUMMY&SEC=DUMMY&LEVL=DUMMY&CRED=DUMMY&GMOD=DUMMY&TITLE=DUMMY&RSTS_IN=RW";

                    //Loop through the checked courses and add them to the string
                    int courseCount = registerCoursesList.size();
                    for (Course course : registerCoursesList){
                        mRegistrationUrl += "&RSTS_IN=RW&CRN_IN=";
                        mRegistrationUrl += course.getCrn();
                        mRegistrationUrl += "&assoc_term_in=&start_date_in=&end_date_in=";
                    }

                    //Add dummy strings to the url until there are 10 registration strings in total
                    //This might actually not be necessary
                    for(int i = courseCount; i <= 10; i++){
                        mRegistrationUrl += "&RSTS_IN=RW&CRN_IN=&assoc_term_in=&start_date_in=&end_date_in=";
                    }

                    mRegistrationUrl += "&regs_row=9&wait_row=0&add_row=10&REG_BTN=Submit+Changes";

                    //Obtain searchedCourses
                    new Registration().execute();

                }
                else{
                    Toast.makeText(CoursesListActivity.this, "No courses selected", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(CoursesListActivity.this, "Feature not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        //Add/Remove to/from Wishlist Button
        TextView wishlist = (TextView)findViewById(R.id.course_wishlist);
        wishlist.setText(this.wishlist ? getResources().getString(R.string.courses_remove_wishlist) :
            getResources().getString(R.string.courses_add_wishlist));
        wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get the checked list of courses from the adapter
                List<Course> checkedCourses = mAdapter.getCheckedCourses();

                String toastMessage;

                //If there are none, display error message
                if(checkedCourses.isEmpty()){
                    toastMessage = getResources().getString(R.string.wishlist_error_empty);
                }
                //If we are in the wishlist, this button is to remove a course
                else if(CoursesListActivity.this.wishlist){
                    toastMessage = getResources().getString(R.string.wishlist_remove, checkedCourses.size());
                    mCourses.removeAll(checkedCourses);

                    //Save the courses to the App context
                    App.setCourseWishlist(mCourses);

                    //Reload the adapter
                    loadInfo();

                }
                //If not, it's to add a course to the wishlist
                else{
                    //Get the wishlist courses
                    List<Course> wishlist = App.getCourseWishlist();

                    //Only add it if it's not already part of the wishlist
                    int coursesAdded = 0;
                    for(Course course : checkedCourses){
                        if(!wishlist.contains(course)){
                            wishlist.add(course);
                            coursesAdded ++;
                        }
                    }

                    //Save the courses to the App context
                    App.setCourseWishlist(wishlist);

                    toastMessage = getResources().getString(R.string.wishlist_add, coursesAdded);
                }

                //Visual feedback of what was just done
                Toast.makeText(CoursesListActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Connects to Minerva in a new thread to register for courses
    private class Registration extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve page that contains registration status from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            String resultString = Connection.getInstance().getUrl(CoursesListActivity.this, mRegistrationUrl);

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
                //TODO: Parse result of registration and check for errors
                Document document = Jsoup.parse(resultString, "UTF-8");

                //Find rows of HTML by class
                Elements dataRows = document.getElementsByClass("dddefault");
                return true;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean loadInfo){
            setProgressBarIndeterminateVisibility(false);

            if(loadInfo){
                //Display whether the user was successfully registered
                //TODO: Add message for registration success or fail
            }
        }
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(CoursesListActivity.this, App.getHomePage().getHomePageClass()));
        super.onBackPressed();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    private void loadInfo(){
        mAdapter = new CoursesAdapter(this, mCourses);
        mListView.setAdapter(mAdapter);
    }

}