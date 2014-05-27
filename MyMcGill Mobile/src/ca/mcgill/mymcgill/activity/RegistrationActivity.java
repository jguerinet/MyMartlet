package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.courseslist.CoursesListActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.DialogHelper;

/**
 * Created by Ryan Singzon on 19/05/14.
 * Takes user input from RegistrationActivity and obtains a list of courses from Minerva
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

    //Searches for the selected courses
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

        //Obtain courses
        new CoursesGetter().execute();
    }

    //Connects to Minerva in a new thread
    private class CoursesGetter extends AsyncTask<Void, Void, Boolean> {
        private String mCoursesString;

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve courses obtained from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            mCoursesString = Connection.getInstance().getUrl(RegistrationActivity.this, mCourseSearchUrl);

            if(mCoursesString == null){
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

            return true;
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean loadInfo){
            setProgressBarIndeterminateVisibility(false);

            //Go to the CoursesListActivity with the parsed courses
            Intent intent = new Intent(RegistrationActivity.this, CoursesListActivity.class);
            intent.putExtra(Constants.COURSES, mCoursesString);
            startActivity(intent);
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

    public void wish(View v) {
        Intent intent = new Intent(this, WishlistActivity.class);
        //startActivityForResult(intent, CHANGE_SEMESTER_CODE);
        startActivity(intent);
    }

}
