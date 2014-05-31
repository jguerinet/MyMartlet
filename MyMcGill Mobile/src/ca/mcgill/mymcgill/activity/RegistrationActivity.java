package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.courseslist.CoursesListActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.DialogHelper;

/**
 * Created by Ryan Singzon on 19/05/14.
 * Takes user input from RegistrationActivity and obtains a list of searchedCourses from Minerva
 */
public class RegistrationActivity extends DrawerActivity{

    private String mCourseSearchUrl;
    private String mMinervaUrl;

    private List<String> mSemesterStrings;
    private String mSemester;

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
        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the selected season
                mSemester = mSemesterStrings.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(RegistrationActivity.this, App.getHomePage().getHomePageClass()));
        super.onBackPressed();
    }

    //Searches for the selected searchedCourses
    public void searchCourses(View v){
        Spinner semesterSpinner = (Spinner) findViewById(R.id.registration_semester);
        String semester = semesterSpinner.getSelectedItem().toString();

        if(semester.equals("Summer 2014")){
            semester = "201405";
        }
        else if(semester.equals("Fall 2014")){
            semester = "201409";
        }
        else if(semester.equals("Winter 2015")){
            semester = "201501";
        }

        //Obtain user input from text boxes
        EditText subjectBox = (EditText) findViewById(R.id.registration_subject);
        String subject = subjectBox.getText().toString().toUpperCase();

        EditText courseNumBox = (EditText) findViewById(R.id.registration_course_number);
        String courseNumber = courseNumBox.getText().toString();

        //Insert user input into the appropriate location in the Minerva URL
        mCourseSearchUrl = "https://horizon.mcgill.ca/pban1/bwskfcls.P_GetCrse?term_in=";
        mCourseSearchUrl += semester;
        mCourseSearchUrl += "&sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy" +
                           "&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy&sel_subj=";
        mCourseSearchUrl += subject;
        mCourseSearchUrl += "&sel_crse=";
        mCourseSearchUrl += courseNumber;
        mCourseSearchUrl += "&sel_title=&sel_schd=%25&sel_from_cred=&sel_to_cred=&sel_levl=%25&sel_ptrm=%25" +
                           "&sel_instr=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a%20Response%20Headersview%20source";

        //Obtain searchedCourses
        new CoursesGetter().execute();
    }

    //Connects to Minerva in a new thread
    private class CoursesGetter extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve searchedCourses obtained from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            String coursesString = Connection.getInstance().getUrl(RegistrationActivity.this, mCourseSearchUrl);

            if(coursesString == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = RegistrationActivity.this;
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
            else{
                //If not, parse it
                Constants.searchedCourses = parseCourses(coursesString);
                return true;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean loadInfo){
            setProgressBarIndeterminateVisibility(false);

            if(loadInfo){
                //Go to the CoursesListActivity with the parsed searchedCourses
                Intent intent = new Intent(RegistrationActivity.this, CoursesListActivity.class);
                intent.putExtra(Constants.WISHLIST, false);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                //Refresh code here if necessary?
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void wish(View v) {
        Intent intent = new Intent(this, WishlistActivity.class);
        //startActivityForResult(intent, CHANGE_SEMESTER_CODE);
        startActivity(intent);
    }*/

    //Parses the HTML retrieved from Minerva and returns a list of searchedCourses
    //Only used if this activity is a result of a search, and not for the course wishlist
    private List<Course> parseCourses(String coursesString){
        List<Course> courses = new ArrayList<Course>();

        Document document = Jsoup.parse(coursesString, "UTF-8");

        //Find rows of HTML by class
        Elements dataRows = document.getElementsByClass("dddefault");

        int rowNumber = 0;
        int rowsSoFar = 0;
        boolean loop = true;

        while (loop) {

            // Create a new course object
            int credits = 99;
            String courseCode = "ERROR";
            String courseTitle = "ERROR";
            String sectionType = "";
            String days = "";
            int crn = 00000;
            String instructor = "";
            String location = "";
            String time = "";
            String dates = "";
            int capacity = 000000;
            int seatsAvailable = 00000;
            int seatsRemaining = 00000;
            int waitlistCapacity = 00000;
            int waitlistAvailable = 00000;
            int waitlistRemaining = 00000;

            int i = 0;
            while (true) {

                try {
                    // Get the HTML row
                    Element row = dataRows.get(rowNumber);
                    rowNumber++;

                    // End condition: Empty row encountered
                    if (row.toString().contains("&nbsp;") && rowsSoFar > 10) {
                        break;
                    }
                    else if(row.toString().contains("NOTES:")){
                        break;
                    }

                    switch (i) {
                        // CRN
                        case 1:
                            crn = Integer.parseInt(row.text());
                            break;

                        // Course code
                        case 2:
                            courseCode = row.text();
                            break;
                        case 3:
                            courseCode += " " + row.text();
                            break;

                        // Section type
                        case 5:
                            sectionType = row.text();
                            break;

                        // Number of credits
                        case 6:
                            credits = (int) Double.parseDouble(row.text());
                            break;

                        // Course title
                        case 7:
                            courseTitle = row.text();
                            break;

                        // Days of the week
                        case 8:
                            days = row.text();

                            if (days.equals("TBA")) {
                                time = "TBA";
                                i = 10;
                                rowNumber++;
                            }
                            break;

                        // Time
                        case 9:
                            time = row.text();
                            break;

                        // Capacity
                        case 10:
                            capacity = Integer.parseInt(row.text());
                            break;

                        // Seats available
                        case 11:
                            seatsAvailable = Integer.parseInt(row.text());
                            break;

                        // Seats remaining
                        case 12:
                            seatsRemaining = Integer.parseInt(row.text());
                            break;

                        // Waitlist capacity
                        case 13:
                            waitlistCapacity = Integer.parseInt(row.text());
                            break;

                        // Waitlist available
                        case 14:
                            waitlistAvailable = Integer.parseInt(row.text());
                            break;

                        // Waitlist remaining
                        case 15:
                            waitlistRemaining = Integer.parseInt(row.text());
                            break;

                        // Instructor
                        case 16:
                            instructor = row.text();
                            break;

                        // Start/end date
                        case 17:
                            dates = row.text();
                            break;

                        // Location
                        case 18:
                            location = row.text();
                            break;
                    }

                    i++;
                }
                catch (IndexOutOfBoundsException e){
                    loop = false;
                    break;
                }
                catch (Exception e) {

                }
            }
            rowsSoFar = 0;

            if( !courseCode.equals("ERROR")){

                //Create a new course object and add it to list
                Course newCourse = new Course(credits, courseCode, courseTitle, sectionType, days,
                        crn, instructor, location, time, dates, capacity, seatsAvailable,
                        seatsRemaining, waitlistCapacity, waitlistAvailable, waitlistRemaining);
                courses.add(newCourse);
            }
        }
        return courses;
    }
}
