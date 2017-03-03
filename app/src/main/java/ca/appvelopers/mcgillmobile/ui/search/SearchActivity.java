/*
 * Copyright 2014-2017 Julien Guerinet
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.guerinet.utils.Device;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.list.TermDialogHelper;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.DayUtils;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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
    @BindView(R.id.search_term)
    protected TextView mTermSelector;
    /**
     * Container for the term selection
     */
    @BindView(R.id.term_container)
    protected LinearLayout termContainer;
    /**
     * Course start time
     */
    @BindView(R.id.search_start)
    protected TimePicker mStartTime;
    /**
     * Course end time
     */
    @BindView(R.id.search_end)
    protected TimePicker mEndTime;
    /**
     * Course subject
     */
    @BindView(R.id.search_subject)
    protected EditText mSubject;
    /**
     * Course number
     */
    @BindView(R.id.search_number)
    protected EditText mNumber;
    /**
     * Course title
     */
    @BindView(R.id.search_title)
    protected EditText mTitle;
    /**
     * Course min credits
     */
    @BindView(R.id.search_min)
    protected EditText mMinCredits;
    /**
     * Course max credits
     */
    @BindView(R.id.search_max)
    protected EditText mMaxCredits;
    /**
     * Course on Monday
     */
    @BindView(R.id.search_monday)
    protected CheckBox mMonday;
    /**
     * Course on Tuesday
     */
    @BindView(R.id.search_tuesday)
    protected CheckBox mTuesday;
    /**
     * Course on Wednesday
     */
    @BindView(R.id.search_wednesday)
    protected CheckBox mWednesday;
    /**
     * Course on Thursday
     */
    @BindView(R.id.search_thursday)
    protected CheckBox mThursday;
    /**
     * Course on Friday
     */
    @BindView(R.id.search_friday)
    protected CheckBox mFriday;
    /**
     * Course on Saturday
     */
    @BindView(R.id.search_saturday)
    protected CheckBox mSaturday;
    /**
     * Course on Sunday
     */
    @BindView(R.id.search_sunday)
    protected CheckBox mSunday;
    /**
     * The more options container
     */
    @BindView(R.id.more_options_container)
    protected LinearLayout mMoreOptionsContainer;
    /**
     * The more options button
     */
    @BindView(R.id.more_options)
    protected Button mMoreOptionsButton;
    /**
     * {@link Term} selected
     */
    protected Term term;
    /**
     * True if the user sees all of the options, false otherwise
     */
    private boolean mAllOptions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        analytics.sendScreen("Registration");

        //Check if there are any terms to register for
        List<Term> registerTerms = App.getRegisterTerms();
        if (registerTerms.isEmpty()) {
            //Hide all of the search related stuff, show explanatory text, and return
            findViewById(R.id.search_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.search_container).setVisibility(View.GONE);
            findViewById(R.id.search_button).setVisibility(View.GONE);
            return;
        }

        //Set the term to the first one
        term = registerTerms.get(0);
        mTermSelector.setText(term.getString(this));
        termContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.list(SearchActivity.this, R.string.title_change_semester,
                        new TermDialogHelper(SearchActivity.this, term, true) {
                            @Override
                            public void onTermSelected(Term term) {
                                SearchActivity.this.term = term;
                                mTermSelector.setText(term.getString(SearchActivity.this));
                            }
                        });
            }
        });

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
    @OnClick(R.id.search_button)
    @SuppressWarnings("deprecation, NewApi")
    protected void searchCourses() {
        //Subject Input
        String subject = mSubject.getText().toString().toUpperCase().trim();
        if (subject.isEmpty()) {
            Utils.toast(this, R.string.registration_error_no_faculty);
            return;
        } else if(!subject.matches("[A-Za-z]{4}")){
            Utils.toast(this, R.string.registration_invalid_subject);
            return;
        }

        //Check that the credits are valid
        int minCredits = 0;
        try {
            minCredits = Integer.valueOf(mMinCredits.getText().toString());
        } catch (NumberFormatException ignored) {}

        int maxCredits = 0;
        try {
            maxCredits = Integer.valueOf(mMaxCredits.getText().toString());
        } catch (NumberFormatException ignored) {}

        if (maxCredits < minCredits) {
            Utils.toast(this, R.string.registration_error_credits);
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

        if (Device.isAtLeastMarshmallow()) {
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

        //Days
        List<DayOfWeek> days = new ArrayList<>();

        if (mMonday.isChecked()) {
            days.add(DayOfWeek.MONDAY);
        }
        if (mTuesday.isChecked()) {
            days.add(DayOfWeek.TUESDAY);
        }
        if (mWednesday.isChecked()) {
            days.add(DayOfWeek.WEDNESDAY);
        }
        if (mThursday.isChecked()) {
            days.add(DayOfWeek.THURSDAY);
        }
        if (mFriday.isChecked()) {
            days.add(DayOfWeek.FRIDAY);
        }
        if (mSaturday.isChecked()) {
            days.add(DayOfWeek.SATURDAY);
        }
        if (mSunday.isChecked()) {
            days.add(DayOfWeek.SUNDAY);
        }

        List<Character> dayChars = new ArrayList<>(days.size());
        for (DayOfWeek day : days) {
            dayChars.add(DayUtils.getDayChar(day));
        }

        // Check if we can refresh
        if (!canRefresh()) {
            return;
        }

        // Execute the request
        mcGillService.search(term, subject, mNumber.getText().toString(),
                mTitle.getText().toString(), minCredits, maxCredits, startHour, startMinute,
                startAM ? "a" : "p", endHour, endMinute, endAM ? "a" : "p", dayChars)
                .enqueue(new Callback<List<CourseResult>>() {
            @Override
            public void onResponse(Call<List<CourseResult>> call,
                    Response<List<CourseResult>> response) {
                showToolbarProgress(false);
                if (response.body() != null) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class)
                            .putExtra(Constants.TERM, term)
                            .putExtra(Constants.COURSES, (ArrayList<CourseResult>) response.body());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<CourseResult>> call, Throwable t) {
                Timber.e(t, "Error searching for courses");
                showToolbarProgress(false);
                Help.handleException(SearchActivity.this, t);
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
    @SuppressWarnings("deprecation, NewApi")
    private void reset() {
        //Reset all of the views
        if (Device.isAtLeastMarshmallow()) {
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