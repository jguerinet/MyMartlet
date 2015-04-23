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

import android.content.Intent;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Day;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.view.TermAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Allows a user to search for courses that they can register for
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class CourseSearchFragment extends BaseFragment {
    private static final String TAG = "Course Search";
    /**
     * Adapter for the term spinner
     */
    private TermAdapter mTermAdapter;
    /**
     * Spinner to choose the term
     */
    @InjectView(R.id.search_term)
    private Spinner mTermSpinner;
    /**
     * Course start time
     */
    @InjectView(R.id.search_start)
    private TimePicker mStartTime;
    /**
     * Course end time
     */
    @InjectView(R.id.search_end)
    private TimePicker mEndTime;
    /**
     * Course subject
     */
    @InjectView(R.id.search_subject)
    private EditText mSubject;
    /**
     * Course number
     */
    @InjectView(R.id.search_number)
    private EditText mNumber;
    /**
     * Course title
     */
    @InjectView(R.id.search_title)
    private EditText mTitle;
    /**
     * Course min credits
     */
    @InjectView(R.id.search_min)
    private EditText mMinCredits;
    /**
     * Course max credits
     */
    @InjectView(R.id.search_max)
    private EditText mMaxCredits;
    /**
     * Course on Monday
     */
    @InjectView(R.id.search_monday)
    private CheckBox mMonday;
    /**
     * Course on Tuesday
     */
    @InjectView(R.id.search_tuesday)
    private CheckBox mTuesday;
    /**
     * Course on Wednesday
     */
    @InjectView(R.id.search_wednesday)
    private CheckBox mWednesday;
    /**
     * Course on Thursday
     */
    @InjectView(R.id.search_thursday)
    private CheckBox mThursday;
    /**
     * Course on Friday
     */
    @InjectView(R.id.search_friday)
    private CheckBox mFriday;
    /**
     * Course on Saturday
     */
    @InjectView(R.id.search_saturday)
    private CheckBox mSaturday;
    /**
     * Course on Sunday
     */
    @InjectView(R.id.search_sunday)
    private CheckBox mSunday;
    /**
     * True if the user sees al of the options, false otherwise
     */
    private boolean mMoreOptions = false;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        View view = View.inflate(mActivity, R.layout.fragment_course_search, null);
        ButterKnife.inject(this, view);
        lockPortraitMode();
        Analytics.getInstance().sendScreen("Registration");
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
            TextView noSemesters = (TextView)view.findViewById(R.id.search_empty);
            noSemesters.setVisibility(View.VISIBLE);

            LinearLayout container = (LinearLayout)view.findViewById(R.id.search_container);
            container.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);

            //Hide the loading indicator
            hideLoadingIndicator();

            return view;
        }

        mTermAdapter = new TermAdapter(mActivity, registerTerms);
        mTermSpinner.setAdapter(mTermAdapter);

        mStartTime.setIs24HourView(false);
        mStartTime.setCurrentHour(0);
        mStartTime.setCurrentMinute(0);

        mEndTime.setIs24HourView(false);
        mEndTime.setCurrentHour(0);
        mEndTime.setCurrentMinute(0);

        //Set up the more options button
        final LinearLayout moreOptionsContainer =
                (LinearLayout)view.findViewById(R.id.more_options_container);
        final TextView moreOptions = (TextView)view.findViewById(R.id.more_options);
        moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inverse the more options boolean
                mMoreOptions = !mMoreOptions;

                moreOptionsContainer.setVisibility(mMoreOptions ? View.VISIBLE : View.GONE);
                moreOptions.setText(mMoreOptions ? getString(R.string.registration_hide_options) :
                    getString(R.string.registration_show_options));
            }
        });

        //Hide the loading indicator
        hideLoadingIndicator();

        return view;
    }

    /**
     * Searches for courses based on the given information
     */
    private void searchCourses(){
        //Get the selected term
        Term term = mTermAdapter.getItem(mTermSpinner.getSelectedItemPosition());

        //Subject Input
        String subject = mSubject.getText().toString().toUpperCase().trim();

        if(subject.isEmpty()){
            Toast.makeText(mActivity, getString(R.string.registration_error_no_faculty),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!subject.matches("[A-Za-z]{4}")){
            Toast.makeText(mActivity, getString(R.string.registration_invalid_subject),
                    Toast.LENGTH_SHORT).show();
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
            Toast.makeText(mActivity, getString(R.string.registration_error_credits),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Show the user we are downloading new information
        mActivity.showToolbarProgress(true);

        Connection.SearchURLBuilder builder = new Connection.SearchURLBuilder(term, subject)
                .courseNumber(mNumber.getText().toString())
                .title(mTitle.getText().toString())
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

        String searchURL = builder.build();

        //Retrieve courses obtained from Minerva
        Log.d(TAG, "URL: " +  searchURL);

        //Execute the request
        String html = new DownloaderThread(mActivity, TAG, searchURL).execute();

        //If there is a result, parse it
        if(html != null){
            List<Course> courses = Parser.parseClassResults(term, html);
            Intent intent = new Intent(mActivity, SearchResultsActivity.class)
                    .putExtra(Constants.TERM, term)
                    .putExtra(Constants.CLASSES, (ArrayList<Course>)courses);
            startActivity(intent);
        }

        //Show the user we are downloading new information
        mActivity.showToolbarProgress(false);
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
            mSubject.setText("");
            mNumber.setText("");
            mTitle.setText("");
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
}