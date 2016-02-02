/*
 * Copyright 2014-2016 Appvelopers
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.DayUtil;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.TermAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Device;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;
import timber.log.Timber;

/**
 * Allows a user to search for courses that they can register for
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SearchActivity extends DrawerActivity {
    /**
     * Spinner to choose the term
     */
    @Bind(R.id.search_term)
    protected Spinner mTermSpinner;
    /**
     * Course start time
     */
    @Bind(R.id.search_start)
    protected TimePicker mStartTime;
    /**
     * Course end time
     */
    @Bind(R.id.search_end)
    protected TimePicker mEndTime;
    /**
     * Course subject
     */
    @Bind(R.id.search_subject)
    protected EditText mSubject;
    /**
     * Course number
     */
    @Bind(R.id.search_number)
    protected EditText mNumber;
    /**
     * Course title
     */
    @Bind(R.id.search_title)
    protected EditText mTitle;
    /**
     * Course min credits
     */
    @Bind(R.id.search_min)
    protected EditText mMinCredits;
    /**
     * Course max credits
     */
    @Bind(R.id.search_max)
    protected EditText mMaxCredits;
    /**
     * Course on Monday
     */
    @Bind(R.id.search_monday)
    protected CheckBox mMonday;
    /**
     * Course on Tuesday
     */
    @Bind(R.id.search_tuesday)
    protected CheckBox mTuesday;
    /**
     * Course on Wednesday
     */
    @Bind(R.id.search_wednesday)
    protected CheckBox mWednesday;
    /**
     * Course on Thursday
     */
    @Bind(R.id.search_thursday)
    protected CheckBox mThursday;
    /**
     * Course on Friday
     */
    @Bind(R.id.search_friday)
    protected CheckBox mFriday;
    /**
     * Course on Saturday
     */
    @Bind(R.id.search_saturday)
    protected CheckBox mSaturday;
    /**
     * Course on Sunday
     */
    @Bind(R.id.search_sunday)
    protected CheckBox mSunday;
    /**
     * The more options container
     */
    @Bind(R.id.more_options_container)
    protected LinearLayout mMoreOptionsContainer;
    /**
     * The more options button
     */
    @Bind(R.id.more_options)
    protected Button mMoreOptionsButton;
    /**
     * Adapter for the term spinner
     */
    private TermAdapter mTermAdapter;
    /**
     * True if the user sees all of the options, false otherwise
     */
    private boolean mAllOptions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        Analytics.get().sendScreen("Registration");

        //Check if there are any terms to register for
        List<Term> registerTerms = App.getRegisterTerms();
        if (registerTerms.isEmpty()) {
            //Hide all of the search related stuff, show explanatory text, and return
            findViewById(R.id.search_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.search_container).setVisibility(View.GONE);
            findViewById(R.id.search_button).setVisibility(View.GONE);
            return;
        }

        mTermAdapter = new TermAdapter(registerTerms);
        mTermSpinner.setAdapter(mTermAdapter);

        mStartTime.setIs24HourView(false);
        mEndTime.setIs24HourView(false);

        reset();
    }

    /**
     * Shows or hides more search options
     */
    @OnClick(R.id.more_options)
    protected void showMoreOptions() {
        //Inverse the boolean
        mAllOptions = !mAllOptions;

        mMoreOptionsContainer.setVisibility(mAllOptions ? View.VISIBLE : View.GONE);
        mMoreOptionsButton.setText(mAllOptions ?
                R.string.registration_hide_options : R.string.registration_show_options);
    }

    /**
     * Searches for courses based on the given information
     */
    @OnClick(R.id.search_button) @SuppressWarnings("deprecation")
    protected void searchCourses() {
        //Get the selected term
        final Term term = mTermAdapter.getItem(mTermSpinner.getSelectedItemPosition());

        //Subject Input
        String subject = mSubject.getText().toString().toUpperCase().trim();
        if (subject.isEmpty()) {
            Toast.makeText(this, R.string.registration_error_no_faculty, Toast.LENGTH_SHORT).show();
            return;
        } else if(!subject.matches("[A-Za-z]{4}")){
            Toast.makeText(this, R.string.registration_invalid_subject, Toast.LENGTH_SHORT).show();
            return;
        }

        //Check that the credits are valid
        int minCredits = 0;
        int maxCredits = 0;
        try {
            minCredits = Integer.valueOf(mMinCredits.getText().toString());
        } catch (NumberFormatException ignored) {}

        try {
            maxCredits = Integer.valueOf(mMaxCredits.getText().toString());
        } catch (NumberFormatException ignored) {}

        if (maxCredits < minCredits) {
            Toast.makeText(this, R.string.registration_error_credits, Toast.LENGTH_SHORT).show();
            return;
        }

        //Show the user we are downloading new information
        showToolbarProgress(true);

        int startHour;
        int startMinute;
        boolean startAM;
        int endHour;
        int endMinute;
        boolean endAM;

        if (Device.isMarshmallow()) {
            startHour = mStartTime.getHour() % 12;
            startMinute = mStartTime.getMinute();
            startAM = mStartTime.getHour() < 12;
            endHour = mEndTime.getHour() % 12;
            endMinute = mEndTime.getMinute();
            endAM = mEndTime.getHour() < 12;
        } else {
            startHour = mStartTime.getCurrentHour() % 12;
            startMinute = mStartTime.getCurrentMinute();
            startAM = mStartTime.getCurrentHour() < 12;
            endHour = mEndTime.getCurrentHour() % 12;
            endMinute = mEndTime.getCurrentMinute();
            endAM = mEndTime.getCurrentHour() < 12;
        }

        Connection.SearchURLBuilder builder = new Connection.SearchURLBuilder(term, subject)
                .courseNumber(mNumber.getText().toString())
                .title(mTitle.getText().toString())
                .minCredits(minCredits)
                .maxCredits(maxCredits)
                .startHour(startHour)
                .startMinute(startMinute)
                .startAM(startAM)
                .endHour(endHour)
                .endMinute(endMinute)
                .endAM(endAM);

        //Days
        if(mMonday.isChecked()) {
            builder.addDay(DayUtil.MONDAY);
        }
        if(mTuesday.isChecked()) {
            builder.addDay(DayUtil.TUESDAY);
        }
        if(mWednesday.isChecked()) {
            builder.addDay(DayUtil.WEDNESDAY);
        }
        if(mThursday.isChecked()) {
            builder.addDay(DayUtil.THURSDAY);
        }
        if(mFriday.isChecked()) {
            builder.addDay(DayUtil.FRIDAY);
        }
        if(mSaturday.isChecked()) {
            builder.addDay(DayUtil.SATURDAY);
        }
        if(mSunday.isChecked()) {
            builder.addDay(DayUtil.SUNDAY);
        }

        String searchURL = builder.build();

        //Retrieve courses obtained from Minerva
        Timber.i("URL: %s", searchURL);

        //Execute the request
        new DownloaderThread(this, searchURL).execute(new DownloaderThread.Callback() {
            @Override
            public void onDownloadFinished(final String result) {
                //If there is a result, parse it
                final List<Course> courses = new ArrayList<>();
                if (result != null) {
                     courses.addAll(Parser.parseClassResults(term, result));
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null) {
                            Intent intent = new Intent(SearchActivity.this,
                                    SearchResultsActivity.class)
                                    .putExtra(Constants.TERM, term)
                                    .putExtra(Constants.COURSES, (ArrayList<Course>) courses);
                            startActivity(intent);
                        }

                        //Show the user we are downloading new information
                        showToolbarProgress(false);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Only inflate the menu if there are semesters to register for
        if (!App.getRegisterTerms().isEmpty()) {
            getMenuInflater().inflate(R.menu.reset, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                reset();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.SEARCH_COURSES;
    }

    /**
     * Resets all of the fields
     */
    @SuppressWarnings("deprecation")
    private void reset() {
        //Reset all of the views
        if (Device.isMarshmallow()) {
            mStartTime.setHour(0);
            mStartTime.setMinute(0);
            mEndTime.setHour(0);
            mEndTime.setMinute(0);
        } else {
            mStartTime.setCurrentHour(0);
            mStartTime.setCurrentMinute(0);
            mEndTime.setCurrentHour(0);
            mEndTime.setCurrentMinute(0);
        }
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
    }
}