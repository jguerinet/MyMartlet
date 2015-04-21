/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.ui.search;

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
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.model.ClassItem;
import ca.appvelopers.mcgillmobile.model.Day;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.view.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.view.TermAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Parser;

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

        lockPortraitMode();

        Analytics.getInstance().sendScreen("Registration");

        //Title
        mActivity.setTitle(getString(R.string.title_registration));

        //Set up the search button
        Button searchButton = (Button)view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCourses();
            }
        });

        //Check if there are any terms to register for
        List<Term> registerTerms = App.getRegisterTerms();
        if(registerTerms.isEmpty()){
            //Hide all of the search related stuff, show explanatory text, and return the view
            TextView noSemesters = (TextView)view.findViewById(R.id.registration_no_semesters);
            noSemesters.setVisibility(View.VISIBLE);

            LinearLayout registrationContainer = (LinearLayout)view.findViewById(
                    R.id.registration_search);
            registrationContainer.setVisibility(View.GONE);

            searchButton.setVisibility(View.GONE);

            //Hide the loading indicator
            hideLoadingIndicator();

            return view;
        }

        //Set up the term spinner
        mTermSpinner = (Spinner) view.findViewById(R.id.registration_semester);
        mTermAdapter = new TermAdapter(mActivity, registerTerms);
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

        //Hide the loading indicator
        hideLoadingIndicator();

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

        //Check that the credits are valid
        int minCredits, maxCredits;
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

        Connection.SearchURLBuilder builder = new Connection.SearchURLBuilder(term, courseSubject)
                .courseNumber(mCourseNumber.getText().toString())
                .title(mCourseTitle.getText().toString())
                .minCredits(minCredits)
                .maxCredits(maxCredits)
                .startHour(mStartTime.getCurrentHour() % 12)
                .startMinute(mStartTime.getCurrentMinute())
                .startAM(mStartTime.getCurrentHour() < 12)
                .endHour(mStartTime.getCurrentHour() % 12)
                .endMinute(mStartTime.getCurrentMinute())
                .endAM(mEndTime.getCurrentHour() < 12);

        //Days
        if(mMonday.isChecked()){
            builder.addDay(Day.MONDAY);
        }
        if(mTuesday.isChecked()){
            builder.addDay(Day.TUESDAY);
        }
        if(mWednesday.isChecked()){
            builder.addDay(Day.WEDNESDAY);
        }
        if(mThursday.isChecked()){
            builder.addDay(Day.THURSDAY);
        }
        if(mFriday.isChecked()){
            builder.addDay(Day.FRIDAY);
        }
        if(mSaturday.isChecked()){
            builder.addDay(Day.SATURDAY);
        }
        if(mSunday.isChecked()){
            builder.addDay(Day.SUNDAY);
        }

        //Obtain courses
        new CoursesGetter(term, builder.build());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //Only inflate the menu if there are semesters to register for
        if(!App.getRegisterTerms().isEmpty()){
            inflater.inflate(R.menu.reset, menu);
        }
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

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Connects to Minerva in a new thread
    private class CoursesGetter extends AsyncTask<Void, Void, Boolean> {
        private Term mTerm;
        private String mClassSearchURL;
        private List<ClassItem> mClasses;
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
            try{
                String classesString = Connection.getInstance().get(mClassSearchURL);
                this.mClasses = Parser.parseClassResults(mTerm, classesString);
                return true;
            } catch(MinervaLoggedOutException e){
                //TODO Broadcast message here
                return false;
            } catch(Exception e){
                return false;
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
                Intent intent = new Intent(mActivity, SearchResultsActivity.class)
                        .putExtra(Constants.TERM, mTerm)
                        .putExtra(Constants.CLASSES, (ArrayList<ClassItem>)mClasses);
                startActivity(intent);
            }
        }
    }
}