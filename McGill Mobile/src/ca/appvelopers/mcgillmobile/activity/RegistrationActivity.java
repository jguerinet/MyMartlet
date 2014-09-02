package ca.appvelopers.mcgillmobile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.courseslist.CoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.object.Faculty;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;
import ca.appvelopers.mcgillmobile.view.FacultyAdapter;
import ca.appvelopers.mcgillmobile.view.TermAdapter;

/**
 * Created by Ryan Singzon on 19/05/14.
 * Takes user input from RegistrationActivity and obtains a list of courses from Minerva
 */
public class RegistrationActivity extends DrawerActivity{
    private Spinner mTermSpinner, mFacultySpinner;
    private TermAdapter mTermAdapter;
    private FacultyAdapter mFacultyAdapter;
    private TimePicker mStartTime, mEndTime;
    private EditText mCourseSubject, mCourseNumber, mCourseTitle, mMinCredits, mMaxCredits;
    private CheckBox mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday, mSunday;

    private boolean mMoreOptions = false;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_registration);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Registration");

        //Set up the term spinner
        mTermSpinner = (Spinner) findViewById(R.id.registration_semester);
        mTermAdapter = new TermAdapter(this, App.getRegisterTerms());
        mTermSpinner.setAdapter(mTermAdapter);

        //Set up the faculty spinner
        mFacultySpinner = (Spinner)findViewById(R.id.registration_faculty);
        mFacultyAdapter = new FacultyAdapter(this, true);
        mFacultySpinner.setAdapter(mFacultyAdapter);

        mStartTime = (TimePicker)findViewById(R.id.registration_start_time);
        mStartTime.setIs24HourView(false);
        mStartTime.setCurrentHour(0);
        mStartTime.setCurrentMinute(0);

        mEndTime = (TimePicker)findViewById(R.id.registration_end_time);
        mEndTime.setIs24HourView(false);
        mEndTime.setCurrentHour(0);
        mEndTime.setCurrentMinute(0);

        //Get the other views
        mCourseSubject = (EditText) findViewById(R.id.registration_subject);
        mCourseNumber = (EditText) findViewById(R.id.registration_course_number);
        mCourseTitle = (EditText)findViewById(R.id.registration_course_title);
        mMinCredits = (EditText)findViewById(R.id.registration_credits_min);
        mMaxCredits = (EditText)findViewById(R.id.registration_credits_max);
        mMonday = (CheckBox)findViewById(R.id.registration_monday);
        mTuesday = (CheckBox)findViewById(R.id.registration_tuesday);
        mWednesday = (CheckBox)findViewById(R.id.registration_wednesday);
        mThursday = (CheckBox)findViewById(R.id.registration_thursday);
        mFriday = (CheckBox)findViewById(R.id.registration_friday);
        mSaturday = (CheckBox)findViewById(R.id.registration_saturday);
        mSunday = (CheckBox)findViewById(R.id.registration_sunday);

        //Set up the more options button
        final LinearLayout moreOptionsContainer = (LinearLayout)findViewById(R.id.more_options_container);
        final TextView moreOptions = (TextView)findViewById(R.id.more_options);
        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inverse the more options boolean
                mMoreOptions = !mMoreOptions;

                //If it is false, hide the options and set the show options text
                if(!mMoreOptions){
                    moreOptionsContainer.setVisibility(View.GONE);
                    moreOptions.setText(getString(R.string.registration_show_options));
                }
                //Do the inverse if true
                else{
                    moreOptionsContainer.setVisibility(View.VISIBLE);
                    moreOptions.setText(getString(R.string.registration_hide_options));
                }
            }
        });
    }

    //Searches for the selected courses
    public void searchCourses(View v){
        //Get the selected term
        Term term = mTermAdapter.getItem(mTermSpinner.getSelectedItemPosition());

        //Get the selected faculty
        Faculty faculty = mFacultyAdapter.getItem(mFacultySpinner.getSelectedItemPosition());

        //Subject Input
        String courseSubject = mCourseSubject.getText().toString().toUpperCase().trim();

        if(faculty == null && courseSubject.isEmpty()){
            Toast.makeText(this, getString(R.string.registration_error_no_faculty), Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!courseSubject.isEmpty() && !courseSubject.matches("[A-Za-z]{4}")){
            Toast.makeText(this, getString(R.string.registration_invalid_subject), Toast.LENGTH_SHORT).show();
            return;
        }

        //Course Number
        String courseNumber = mCourseNumber.getText().toString();

        //Course Title
        String courseTitle = mCourseTitle.getText().toString();

        //Credits
        int minCredits, maxCredits ;
        try {
            minCredits = Integer.valueOf(mMinCredits.getText().toString());
        } catch (NumberFormatException e){
            minCredits = 0;
        }
        try {
            maxCredits = Integer.valueOf(mMaxCredits.getText().toString());
        } catch (NumberFormatException e){
            maxCredits = 0;
        }

        if(maxCredits < minCredits){
            Toast.makeText(this, getString(R.string.registration_error_credits), Toast.LENGTH_SHORT).show();
            return;
        }

        //Start time
        int startHour = mStartTime.getCurrentHour();
        int startMinute = mStartTime.getCurrentMinute();
        char startAMPM = 'a';
        if(startHour == 12){
            startAMPM = 'p';
        }
        else if(startHour > 12){
            startAMPM = 'p';
            startHour = startHour - 12;
        }


        //End Time
        int endHour = mEndTime.getCurrentHour();
        int endMinute = mEndTime.getCurrentMinute();
        char endAMPM = 'a';
        if(endHour == 12){
            endAMPM = 'p';
        }
        else if(endHour > 12){
            endAMPM = 'p';
            endHour = endHour - 12;
        }


        //Days
        List<String> days = new ArrayList<String>();
        if(mMonday.isChecked()){
            days.add("m");
        }
        if(mTuesday.isChecked()){
            days.add("t");
        }
        if(mWednesday.isChecked()){
            days.add("w");
        }
        if(mThursday.isChecked()){
            days.add("r");
        }
        if(mFriday.isChecked()){
            days.add("f");
        }
        if(mSaturday.isChecked()){
            days.add("s");
        }
        if(mSunday.isChecked()){
            days.add("u");
        }

        //Obtain courses
        Log.e("Registration options", startHour + ":" + startMinute + " " + endHour + ":" + endMinute + " " + days );
        new CoursesGetter(term, Connection.getCourseURL(term, courseSubject, faculty, courseNumber,
                courseTitle, minCredits, maxCredits, startHour, startMinute, startAMPM, endHour,
                endMinute, endAMPM, days)).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.reset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_reset){
            //Reset all of the views
            mFacultySpinner.setSelection(0);
            mStartTime.setCurrentHour(0);
            mStartTime.setCurrentMinute(0);
            mEndTime.setCurrentHour(0);
            mEndTime.setCurrentMinute(0);
            mCourseSubject.setText("");
            mCourseNumber.setText("");
            mCourseTitle.setText("");
            mMinCredits.setText("");
            mMaxCredits.setText("");
            mMonday.setChecked(false);
            mTuesday.setChecked(false);
            mWednesday.setChecked(false);
            mThursday.setChecked(false);
            mFriday.setChecked(false);
            mSaturday.setChecked(false);
            mSunday.setChecked(false);
        }
        return super.onOptionsItemSelected(item);
    }

    //Connects to Minerva in a new thread
    private class CoursesGetter extends AsyncTask<Void, Void, Boolean> {
        private Term mTerm;
        private String mClassSearchURL;

        private ProgressDialog mDialog;

        public CoursesGetter(Term term, String classSearchURL){
            this.mTerm = term;
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
            Log.e("Class search URL",  mClassSearchURL);
            String classesString = Connection.getInstance().getUrl(RegistrationActivity.this, mClassSearchURL);

            //There was an error
            if(classesString == null){
                return false;
            }
            //Parse
            else{
                Constants.searchedClassItems = Parser.parseClassResults(mTerm, classesString);
                return true;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean coursesParsed){
            mDialog.dismiss();

            //There was an error
            if(!coursesParsed){
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
                intent.putExtra(Constants.TERM, mTerm);
                startActivity(intent);
            }
        }
    }
}
