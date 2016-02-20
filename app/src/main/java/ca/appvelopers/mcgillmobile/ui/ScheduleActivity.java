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

package ca.appvelopers.mcgillmobile.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.BooleanPreference;

import junit.framework.Assert;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.DateUtils;
import ca.appvelopers.mcgillmobile.util.DayUtils;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Represents the user's schedule
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class ScheduleActivity extends DrawerActivity {
    /**
     * Timetable container used in the landscape orientation
     */
    @Nullable @Bind(R.id.container_timetable)
    protected LinearLayout timetableContainer;
    /**
     * Schedule container used in the landscape orientation
     */
    @Nullable @Bind(R.id.container_schedule)
    protected LinearLayout scheduleContainer;
    /**
     * {@link ViewPager} used in the portrait orientation
     */
    @Nullable @Bind(R.id.view_pager)
    protected ViewPager viewPager;
    /**
     * The first open {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.FIRST_OPEN)
    protected BooleanPreference firstOpenPrefs;
    /**
     * TODO
     * SCHEDULE_24HR {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.SCHEDULE_24HR)
    protected BooleanPreference TwentyFourHourPrefs;
    /**
     * Current {@link Term}
     */
    private Term term;
    /**
     * List of courses for the current term
     */
    private List<Course> courses;
    /**
     * Current date (to know which week to show in the landscape orientation)
     */
    private LocalDate date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ButterKnife.bind(this);
        App.component(this).inject(this);

        //TODO Use the SavedInstanceState to get the term and courses
        term = App.getDefaultTerm();
        courses = new ArrayList<>();
        //TODO
        this.date = getStartingDate();

        //Title
        setTitle(term.getString(this));

        //Render the right view based on the orientation
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            renderLandscapeView();
        } else {
            renderPortraitView();
        }


        //Check if this is the first time the user is using the app
        if (firstOpenPrefs.get()) {
            //Show them the walkthrough if it is
            Intent intent = new Intent(this, WalkthroughActivity.class)
                    .putExtra(Constants.FIRST_OPEN, true);
            startActivity(intent);
            //Save the fact that the walkthrough has been seen at least once
            firstOpenPrefs.set(false);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Only show the menu in portrait mode
        return getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        getMenuInflater().inflate(R.menu.change_semester, menu);
        Utils.setTint(menu.findItem(R.id.action_refresh).getIcon(), Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_semester:
                DialogHelper.changeSemester(this, term, false, new DialogHelper.TermCallback() {
                            @Override
                            public void onTermSelected(Term term) {
                                ScheduleActivity.this.term = term;

                                //TODO
//                                //Restart the schedule view builder with the right date
//                                mViewBuilder = new ScheduleViewBuilder(ScheduleActivity.this,
//                                        getStartingDate());

                                //Refresh the content
                                refreshCourses();
                            }
                        }, analytics);
                return true;
            case R.id.action_refresh:
                refreshCourses();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected @HomepageManager.Homepage int getCurrentPage() {
        return HomepageManager.SCHEDULE;
    }

    /**
     * Gets the starting date based on the term and get the concerned classes
     *
     * @return The starting date
     */
    private LocalDate getStartingDate() {
        fillCourses();

        //Date is by default set to today
        LocalDate date = LocalDate.now();
        //Check if we are in the current semester
        if (!term.equals(Term.getCurrentTerm())) {
            //If not, find the starting date of this semester instead of using today
            for (Course classItem : courses) {
                if (classItem.getStartDate().isBefore(date)) {
                    date = classItem.getStartDate();
                }
            }
        }

        return date;
    }

    /**
     * Refreshes the list of courses for the given term and the user's transcript
     *  (only available in portrait mode)
     */
    private void refreshCourses() {
        //Show the user we are refreshing
        showToolbarProgress(true);

        //Download the courses for this term
        new DownloaderThread(this, mcGillService.schedule(term))
                .execute(new DownloaderThread.Callback() {
                    @Override
                    public void onDownloadFinished(String result) {
                        //Parse the courses if there are any
                        if (result != null) {
                            Parser.parseCourses(term, result);

                            //Download the Transcript
                            //  (if ever the user has new semesters on their transcript)
                            new DownloaderThread(ScheduleActivity.this, mcGillService.transcript())
                                    .execute(new DownloaderThread.Callback() {
                                        @Override
                                        public void onDownloadFinished(String result) {
                                            //Parse the transcript if possible
                                            if (result != null) {
                                                Parser.parseTranscript(result);
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Assert.assertNotNull(viewPager);
                                                    viewPager.getAdapter().notifyDataSetChanged();
                                                    showToolbarProgress(false);
                                                }
                                            });
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Fills the class list with the current term's classes
     */
    private void fillCourses() {
        //Clear the current course list, add the courses that are for this semester
        courses.clear();
        for (Course classItem : App.getCourses()) {
            if (classItem.getTerm().equals(term)) {
                courses.add(classItem);
            }
        }
    }

    /**
     * Renders the landscape view
     */
    private void renderLandscapeView() {
        //Make sure that the necessary views are present in the layout
        Assert.assertNotNull(timetableContainer);
        Assert.assertNotNull(scheduleContainer);

        //Leave space at the top for the day names
        View dayView = View.inflate(this, R.layout.fragment_day_name, null);
        //Black line to separate the timetable from the schedule
        View dayViewLine = dayView.findViewById(R.id.day_line);
        dayViewLine.setVisibility(View.VISIBLE);

        //Add the day view to the top of the timetable
        timetableContainer.addView(dayView);

        //Find the index of the given date
        int currentDayIndex = date.getDayOfWeek().getValue();

        //Go through the 7 days of the week
        for (int i = 1; i < 8; i ++) {
            DayOfWeek day = DayOfWeek.of(i);

            //Set up the day name
            dayView = View.inflate(this, R.layout.fragment_day_name, null);
            TextView dayViewTitle = (TextView) dayView.findViewById(R.id.day_name);
            dayViewTitle.setText(DayUtils.getString(this, day));
            scheduleContainer.addView(dayView);

            //Set up the schedule container for that one day
            LinearLayout scheduleContainer = new LinearLayout(this);
            scheduleContainer.setOrientation(LinearLayout.VERTICAL);
            scheduleContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.cell_landscape_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            //Fill the schedule for the current day
            fillSchedule(this.timetableContainer, scheduleContainer, date.plusDays(i - currentDayIndex), false);

            //Add the current day to the schedule container
            this.scheduleContainer.addView(scheduleContainer);

            //Line
            View line = new View(this);
            line.setBackgroundColor(Color.BLACK);
            line.setLayoutParams(new ViewGroup.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.schedule_line),
                    ViewGroup.LayoutParams.MATCH_PARENT));
            this.scheduleContainer.addView(line);
        }
    }

    /**
     * Renders the portrait view
     */
    private void renderPortraitView() {
        //Make sure the views are there
        Assert.assertNotNull(viewPager);

        final ScheduleAdapter adapter = new ScheduleAdapter();

        //Set up the ViewPager
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(adapter.firstDayIndex);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {}

            @Override
            public void onPageSelected(int i) {
                //Update the date every time the page is turned to have the right
                //  week if ever the user rotates his device
                date = adapter.getDate(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });
    }

    /**
     * Fills the schedule based on given data
     *
     * @param timetableContainer The container for the timetable
     * @param scheduleContainer  The container for the schedule
     * @param clickable          True if the user can click on the courses (portrait),
     *                              false otherwise (landscape)
     */
    public void fillSchedule(LinearLayout timetableContainer, LinearLayout scheduleContainer,
            LocalDate date, boolean clickable) {
        //Go through the list of courses, find which ones have the same day and
        //  are for the given date
        List<Course> courses = new ArrayList<>();
        for (Course course : this.courses) {
            if (course.isForDate(date)) {
                courses.add(course);
            }
        }

        //This will be used of an end time of a course when it is added to the schedule container
        LocalTime currentCourseEndTime = null;

        //Cycle through the hours
        for (int hour = 8; hour < 22; hour++) {
            //Start inflating a timetable cell
            View timetableCell = View.inflate(this, R.layout.item_day_timetable, null);

            //Put the correct time
            TextView time = (TextView) timetableCell.findViewById(R.id.cell_time);

            if(TwentyFourHourPrefs.get()){
                time.setText(DateUtils.getHourStringTwentyFourHrFmt(hour));
            }else {
                time.setText(DateUtils.getHourString(hour));
            }

            //Add it to the right container
            timetableContainer.addView(timetableCell);

            //Cycle through the half hours
            for(int min = 0; min < 31; min+= 30){
                //Initialize the current course to null
                Course currentCourse = null;

                //Get the current time
                LocalTime currentTime = LocalTime.of(hour, min);

                //if currentCourseEndTime = null (no course is being added) or it is equal to
                //the current time in min (end of a course being added) we need to add a new view
                if(currentCourseEndTime == null || currentCourseEndTime.equals(currentTime)){
                    //Reset currentCourseEndTime
                    currentCourseEndTime = null;

                    //Check if there is a course at this time
                    for(Course course : courses){
                        //If there is, set the current course to that time, and calculate the
                        //ending time of this course
                        if(course.getRoundedStartTime().equals(currentTime)){
                            currentCourse = course;
                            currentCourseEndTime = course.getRoundedEndTime();
                            break;
                        }
                    }

                    View scheduleCell;

                    //There is a course at this time
                    if(currentCourse != null){
                        //Inflate the right view
                        scheduleCell = View.inflate(this, R.layout.item_day_class, null);

                        //Quick check
                        assert(scheduleCell != null);

                        //Set up all of the info
                        TextView code = (TextView)scheduleCell.findViewById(R.id.course_code);
                        code.setText(currentCourse.getCode());

                        TextView type = (TextView)scheduleCell.findViewById(R.id.course_type);
                        type.setText(currentCourse.getType());

                        TextView courseTime = (TextView)scheduleCell.findViewById(R.id.course_time);
                        courseTime.setText(currentCourse.getTimeString());

                        TextView location =
                                (TextView)scheduleCell.findViewById(R.id.course_location);
                        location.setText(currentCourse.getLocation());

                        //Find out how long this course is in terms of blocks of 30 min
                        int length = (int) ChronoUnit.MINUTES.between(
                                currentCourse.getRoundedStartTime(),
                                currentCourse.getRoundedEndTime()) / 30;

                        //Set the height of the view depending on this height
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) getResources()
                                        .getDimension(R.dimen.cell_30min_height) * length);
                        scheduleCell.setLayoutParams(lp);

                        //Check if we need to make the course clickable
                        if(clickable){
                            //We need a final variable for the onClick listener
                            final Course course = currentCourse;
                            //OnClick: CourseActivity (for a detailed description of the course)
                            scheduleCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogHelper.showCourseDialog(ScheduleActivity.this, course,
                                            analytics);
                                }
                            });
                        }
                        else{
                            scheduleCell.setClickable(false);
                        }

                    }
                    else{
                        //Inflate the empty view
                        scheduleCell = View.inflate(this, R.layout.item_day_empty, null);
                    }

                    //Add the given view to the schedule container
                    scheduleContainer.addView(scheduleCell);
                }
            }
        }
    }

    /**
     * The adapter used for the ViewPager in the portrait view of the schedule
     */
    protected class ScheduleAdapter extends PagerAdapter {
        /**
         * Day title
         */
        @Bind(R.id.day_title)
        protected TextView dayTitle;
        /**
         * Date title
         */
        @Bind(R.id.day_date)
        protected TextView dateTitle;
        @Bind(R.id.container_timetable)
        protected LinearLayout timetableContainer;
        @Bind(R.id.container_schedule)
        protected LinearLayout scheduleContainer;
        /**
         * The index of the first day
         */
        private int firstDayIndex;
        private LocalDate startingDate;

        public ScheduleAdapter() {
            super();
            //Get the first day (offset of 500001 to get the right day)
            firstDayIndex = 500001 + date.getDayOfWeek().getValue();
            //Set the starting date
            startingDate = date;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            Context context = ScheduleActivity.this;
            View view = View.inflate(context, R.layout.fragment_day, null);
            ButterKnife.bind(this, view);

            //Get the date for this view
            LocalDate currentDate = getDate(position);

            //Set the titles
            dayTitle.setText(DayUtils.getString(context, currentDate.getDayOfWeek()));
            dateTitle.setText(com.guerinet.utils.DateUtils.getLongDateString(currentDate));

            //Fill the schedule up
            fillSchedule(timetableContainer, scheduleContainer, currentDate, true);

            collection.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 1000000;
        }

        public LocalDate getDate(int position) {
            return startingDate.plusDays(position - firstDayIndex);
        }

        //TODO Do we need this
//        @Override
//        public int getItemPosition(Object object) {
//            //This is to force the refreshing of all of the views when the view is reloaded
//            return POSITION_NONE;
//        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }
}