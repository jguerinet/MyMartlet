package ca.mcgill.mymcgill.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.courseslist.CoursesListActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.object.Season;
import ca.mcgill.mymcgill.object.Season;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.DialogHelper;
import ca.mcgill.mymcgill.util.Parser;

/**
 * Created by Ryan Singzon on 19/05/14.
 * Takes user input from RegistrationActivity and obtains a list of courses from Minerva
 */
public class RegistrationActivity extends DrawerActivity{
    private List<String> mSemesterStrings;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_registration);
        super.onCreate(savedInstanceState);

        //Make a list with their strings
        mSemesterStrings = new ArrayList<String>();
        mSemesterStrings.add("Summer 2014");
        mSemesterStrings.add("Fall 2014");
        mSemesterStrings.add("Winter 2015");

        //Set up the semester adapter and declare "Winter is Coming"
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSemesterStrings);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set up the season spinner
        Spinner semester = (Spinner) findViewById(R.id.registration_semester);
        semester.setAdapter(semesterAdapter);

        //Set default semester to Fall 2014
        semester.setSelection(2);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(RegistrationActivity.this, App.getHomePage().getHomePageClass()));
        super.onBackPressed();
    }

    //Searches for the selected courses
    public void searchCourses(View v){
        Spinner semesterSpinner = (Spinner) findViewById(R.id.registration_semester);
        String semester = semesterSpinner.getSelectedItem().toString();
        Season season = null;
        int year = -1;

        //TODO Find out how to make this dynamicc
        if(semester.equals("Summer 2014")){
            season = Season.SUMMER;
            year = 2014;
        }
        else if(semester.equals("Fall 2014")){
            season = Season.FALL;
            year = 2014;
        }
        else if(semester.equals("Winter 2015")){
            season = Season.WINTER;
            year = 2015;
        }

        //Obtain user input from text boxes
        EditText subjectBox = (EditText) findViewById(R.id.registration_subject);
        String subject = subjectBox.getText().toString().toUpperCase();
        if(!subject.matches("[A-Za-z]{4}")){
            String toastMessage = getResources().getString(R.string.registration_invalid_subject);
            Toast.makeText(RegistrationActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        EditText courseNumBox = (EditText) findViewById(R.id.registration_course_number);
        String courseNumber = courseNumBox.getText().toString();

        String courseSearchURL = Connection.getCourseURL(season, year, subject, courseNumber);

        //Obtain courses
        new CoursesGetter(season, year, courseSearchURL).execute();
    }

    //Connects to Minerva in a new thread
    private class CoursesGetter extends AsyncTask<Void, Void, Boolean> {
        private Season mSeason;
        private int mYear;
        private String mClassSearchURL;

        private ProgressDialog mDialog;

        public CoursesGetter(Season season, int year, String classSearchURL){
            this.mSeason = season;
            this.mYear = year;
            this.mClassSearchURL = classSearchURL;
        }

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            mDialog = new ProgressDialog(RegistrationActivity.this);
            mDialog.setMessage(getResources().getString(R.string.please_wait));
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        //Retrieve courses obtained from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            String classesString = Connection.getInstance().getUrl(RegistrationActivity.this, mClassSearchURL);

            //There was an error
            if(classesString == null){
                return false;
            }
            //Parse
            else{
                Constants.searchedClassItems = Parser.parseClassResults(mSeason, mYear, classesString);
                return true;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean infoLoaded){
            mDialog.dismiss();

            //There was an error
            if(!infoLoaded){
                try {
                    DialogHelper.showNeutralAlertDialog(RegistrationActivity.this, getResources().getString(R.string.error),
                            getResources().getString(R.string.error_other));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Go to the CoursesListActivity with the parsed courses
            else{
                Intent intent = new Intent(RegistrationActivity.this, CoursesListActivity.class);
                intent.putExtra(Constants.WISHLIST, false);
                startActivity(intent);
            }
        }
    }
}
