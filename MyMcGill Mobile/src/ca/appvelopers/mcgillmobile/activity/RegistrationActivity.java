package ca.appvelopers.mcgillmobile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
        mStartTime.setCurrentHour(0);
        mStartTime.setCurrentMinute(0);

        mEndTime = (TimePicker)findViewById(R.id.registration_end_time);
        mEndTime.setCurrentHour(0);
        mEndTime.setCurrentMinute(0);

        //Set up the more options button
        final LinearLayout moreOptionsContainer = (LinearLayout)findViewById(R.id.more_options_container);
        //TODO Hardocded String
        final TextView moreOptions = (TextView)findViewById(R.id.more_options);
        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inverse the more options boolean
                mMoreOptions = !mMoreOptions;

                //If it is false, hide the options and set the show options text
                if(!mMoreOptions){
                    moreOptionsContainer.setVisibility(View.GONE);
                    //TODO Hardcoded
                    moreOptions.setText("Show More Options");
                }
                //Do the inverse if true
                else{
                    moreOptionsContainer.setVisibility(View.VISIBLE);
                    //TODO Hardcoded
                    moreOptions.setText("Hide More Options");
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
        EditText courseSubjectView = (EditText) findViewById(R.id.registration_subject);
        String courseSubject = courseSubjectView.getText().toString().toUpperCase();
        if(!courseSubject.matches("[A-Za-z]{4}")){
            String toastMessage = getResources().getString(R.string.registration_invalid_subject);
            Toast.makeText(RegistrationActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO Check if there is either a faculty or a subject inputted

        //Course Number
        EditText courseNumberView = (EditText) findViewById(R.id.registration_course_number);
        String courseNumber = courseNumberView.getText().toString();

        //Course Title
        EditText courseTitleView = (EditText)findViewById(R.id.registration_course_title);
        String courseTitle = courseTitleView.getText().toString();

        //Credits
        EditText minCreditsView = (EditText)findViewById(R.id.registration_credits_min);
        int minCredits = Integer.valueOf(minCreditsView.getText().toString());
        EditText maxCreditsView = (EditText)findViewById(R.id.registration_credits_max);
        int maxCredits = Integer.valueOf(maxCreditsView.getText().toString());

        if(maxCredits < minCredits){
            //TODO Hardcoded String
            Toast.makeText(this, "The maximum hours must be bigger than the minimum hours", Toast.LENGTH_SHORT).show();
            return;
        }

        //Start time
        int startHour = mStartTime.getCurrentHour();
        int startMinute = mStartTime.getCurrentMinute();

        //End Time
        int endHour = mEndTime.getCurrentHour();
        int endMinute = mEndTime.getCurrentMinute();

        //Days
        List<Day> days = new ArrayList<Day>();
        CheckBox checkbox = (CheckBox)findViewById(R.id.registration_monday);
        if(checkbox.isChecked()){
            days.add(Day.MONDAY);
        }
        checkbox = (CheckBox)findViewById(R.id.registration_tuesday);
        if(checkbox.isChecked()){
            days.add(Day.TUESDAY);
        }
        checkbox = (CheckBox)findViewById(R.id.registration_wednesday);
        if(checkbox.isChecked()){
            days.add(Day.WEDNESDAY);
        }
        checkbox = (CheckBox)findViewById(R.id.registration_thursday);
        if(checkbox.isChecked()){
            days.add(Day.THURSDAY);
        }
        checkbox = (CheckBox)findViewById(R.id.registration_friday);
        if(checkbox.isChecked()){
            days.add(Day.FRIDAY);
        }
        checkbox = (CheckBox)findViewById(R.id.registration_saturday);
        if(checkbox.isChecked()){
            days.add(Day.SATURDAY);
        }
        checkbox = (CheckBox)findViewById(R.id.registration_sunday);
        if(checkbox.isChecked()){
            days.add(Day.SUNDAY);
        }

        //Obtain courses
        new CoursesGetter(term, Connection.getCourseURL(term, courseSubject, faculty, courseNumber,
                courseTitle, minCredits, maxCredits, startHour, startMinute, endHour, endMinute, days)).execute();
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
