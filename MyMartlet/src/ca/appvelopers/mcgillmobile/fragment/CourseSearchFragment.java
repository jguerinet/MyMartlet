package ca.appvelopers.mcgillmobile.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import ca.appvelopers.mcgillmobile.activity.SearchResultsActivity;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;
import ca.appvelopers.mcgillmobile.view.TermAdapter;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 4:15 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class CourseSearchFragment extends BaseFragment {
    private Spinner mTermSpinner;
    private TermAdapter mTermAdapter;
    private TimePicker mStartTime, mEndTime;
    private EditText mCourseSubject, mCourseNumber, mCourseTitle, mMinCredits, mMaxCredits;
    private CheckBox mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday, mSunday;

    private boolean mMoreOptions = false;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = View.inflate(mActivity, R.layout.fragment_course_search, null);

        GoogleAnalytics.sendScreen(mActivity, "Registration");

        //Title
        mActivity.setTitle(getString(R.string.title_registration));

        //Set up the term spinner
        mTermSpinner = (Spinner) view.findViewById(R.id.registration_semester);
        mTermAdapter = new TermAdapter(mActivity, App.getRegisterTerms());
        mTermSpinner.setAdapter(mTermAdapter);

        mStartTime = (TimePicker)view.findViewById(R.id.registration_start_time);
        mStartTime.setIs24HourView(false);
        mStartTime.setCurrentHour(0);
        mStartTime.setCurrentMinute(0);

        mEndTime = (TimePicker)view.findViewById(R.id.registration_end_time);
        mEndTime.setIs24HourView(false);
        mEndTime.setCurrentHour(0);
        mEndTime.setCurrentMinute(0);

        //Get the other views
        mCourseSubject = (EditText)view.findViewById(R.id.registration_subject);
        mCourseNumber = (EditText)view.findViewById(R.id.registration_course_number);
        mCourseTitle = (EditText)view.findViewById(R.id.registration_course_title);
        mMinCredits = (EditText)view.findViewById(R.id.registration_credits_min);
        mMaxCredits = (EditText)view.findViewById(R.id.registration_credits_max);
        mMonday = (CheckBox)view.findViewById(R.id.registration_monday);
        mTuesday = (CheckBox)view.findViewById(R.id.registration_tuesday);
        mWednesday = (CheckBox)view.findViewById(R.id.registration_wednesday);
        mThursday = (CheckBox)view.findViewById(R.id.registration_thursday);
        mFriday = (CheckBox)view.findViewById(R.id.registration_friday);
        mSaturday = (CheckBox)view.findViewById(R.id.registration_saturday);
        mSunday = (CheckBox)view.findViewById(R.id.registration_sunday);

        //Set up the more options button
        final LinearLayout moreOptionsContainer = (LinearLayout)view.findViewById(R.id.more_options_container);
        final TextView moreOptions = (TextView)view.findViewById(R.id.more_options);
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

        //Set up the search button
        Button searchButton = (Button)view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCourses();
            }
        });

        return view;
    }

    //Searches for the selected courses
    public void searchCourses(){
        //Get the selected term
        Term term = mTermAdapter.getItem(mTermSpinner.getSelectedItemPosition());

        //Subject Input
        String courseSubject = mCourseSubject.getText().toString().toUpperCase().trim();

        if(courseSubject.isEmpty()){
            Toast.makeText(mActivity, getString(R.string.registration_error_no_faculty), Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!courseSubject.matches("[A-Za-z]{4}")){
            Toast.makeText(mActivity, getString(R.string.registration_invalid_subject), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mActivity, getString(R.string.registration_error_credits), Toast.LENGTH_SHORT)
                    .show();
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
        Log.e("Registration options", startHour + ":" + startMinute + " " + endHour + ":" + endMinute + " " + days);
        new CoursesGetter(term, Connection.getCourseURL(term, courseSubject, courseNumber,
                courseTitle, minCredits, maxCredits, startHour, startMinute, startAMPM, endHour,
                endMinute, endAMPM, days)).execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.reset, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_reset){
            //Reset all of the views
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
            mDialog = new ProgressDialog(mActivity);
            mDialog.setMessage(getResources().getString(R.string.please_wait));
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();
        }

        //Retrieve courses obtained from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            Log.e("Class search URL",  mClassSearchURL);
            String classesString = Connection.getInstance().getUrl(mActivity, mClassSearchURL);

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
                    DialogHelper.showNeutralAlertDialog(mActivity, getResources().getString(R.string.error),
                            getResources().getString(R.string.error_other));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //Go to the CoursesListActivity with the parsed courses
            else{
                Intent intent = new Intent(mActivity, SearchResultsActivity.class);
                intent.putExtra(Constants.TERM, mTerm);
                startActivity(intent);
            }
        }
    }
}