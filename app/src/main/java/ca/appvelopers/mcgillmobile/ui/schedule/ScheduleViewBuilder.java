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

package ca.appvelopers.mcgillmobile.ui.schedule;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.utils.prefs.BooleanPreference;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.DateUtils;
import ca.appvelopers.mcgillmobile.util.DayUtils;

/**
 * Builds the schedule view based on the orientation and the current date
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class ScheduleViewBuilder {
    /**
     * The ScheduleActivity instance
     */
    private ScheduleActivity activity;
    /**
     * The starting date
     */
    private LocalDate date;

    /**
     * SCHEDULE_24HR {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.SCHEDULE_24HR)
    protected BooleanPreference TwentyFourHourPrefs;
    /**
     * {@link Analytics} instance
     */
    @Inject
    protected Analytics analytics;

    /**
     * Default Constructor
     *
     * @param activity {@link ScheduleActivity} instance
     * @param date     Starting date
     */
    public ScheduleViewBuilder(ScheduleActivity activity, LocalDate date) {
        this.activity = activity;
        this.date = date;
        App.component(activity).inject(this);
    }

    /**
     * Loads, fills and returns the view to use given the orientation
     *
     * @param orientation The current screen orientation
     * @return The view to use
     */
    public View renderView(int orientation) {
        //Inflate the view
        View view = View.inflate(activity, R.layout.activity_schedule, null);

        //Render the right view based on the orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            renderLandscapeView(view);
        } else {
            renderPortraitView(view);
        }

        return view;
    }

    /**
     * Renders the landscape view
     *
     * @param view The base view
     */
    private void renderLandscapeView(View view) {
        //Get the timetable container
        LinearLayout timetableContainer = (LinearLayout)view.findViewById(R.id.timetable_container);

        //Leave space at the top for the day names
        View dayView = View.inflate(activity, R.layout.fragment_day_name, null);
        //Black line to separate the timetable from the schedule
        View dayViewLine = dayView.findViewById(R.id.day_line);
        dayViewLine.setVisibility(View.VISIBLE);

        //Add the day view to the top of the timetable
        timetableContainer.addView(dayView);

        //Get the schedule container
        LinearLayout dayContainer = (LinearLayout)view.findViewById(R.id.schedule_container);

        //Find the index of the given date
        int currentDayIndex = date.getDayOfWeek().getValue();

        //Go through the 7 days of the week
        for(int i = 1; i < 8; i ++){
            DayOfWeek day = DayOfWeek.of(i);

            //Set up the day name
            dayView = View.inflate(activity, R.layout.fragment_day_name, null);
            TextView dayViewTitle = (TextView) dayView.findViewById(R.id.day_name);
            dayViewTitle.setText(DayUtils.getString(activity, day));
            dayContainer.addView(dayView);

            //Set up the schedule container for that one day
            LinearLayout scheduleContainer = new LinearLayout(activity);
            scheduleContainer.setOrientation(LinearLayout.VERTICAL);
            scheduleContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    activity.getResources().getDimensionPixelSize(R.dimen.cell_landscape_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            //Get the classes for today
            List<Course> courses = activity.getCourses(date.plusDays(i - currentDayIndex));

            //Fill the schedule for the current day
            fillSchedule(activity, timetableContainer, scheduleContainer, courses, false,
                    TwentyFourHourPrefs.get(), analytics);

            //Add the current day to the schedule container
            dayContainer.addView(scheduleContainer);

            //Line
            View line = new View(activity);
            line.setBackgroundColor(Color.BLACK);
            line.setLayoutParams(new ViewGroup.LayoutParams(
                    activity.getResources().getDimensionPixelSize(R.dimen.schedule_line),
                    ViewGroup.LayoutParams.MATCH_PARENT));
            dayContainer.addView(line);
        }
    }

    /**
     * Fills the schedule based on given data
     *
     * @param activity           The calling activity
     * @param timetableContainer The container for the timetable
     * @param scheduleContainer  The container for the schedule
     * @param courses            The list of courses
     * @param clickableCourses   True if the user can click on the courses (portrait,
     *                              false otherwise (landscape)
     */
    public static void fillSchedule(final Activity activity, LinearLayout timetableContainer,
                                    LinearLayout scheduleContainer, List<Course> courses,
                                    boolean clickableCourses, boolean TwentyFourHourPreference,
            final Analytics analytics) {
        //This will be used of an end time of a course when it is added to the schedule container
        LocalTime currentCourseEndTime = null;

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Start inflating a timetable cell
            View timetableCell = View.inflate(activity, R.layout.item_day_timetable, null);

            //Put the correct time
            TextView time = (TextView)timetableCell.findViewById(R.id.cell_time);

            if(TwentyFourHourPreference){
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
                        scheduleCell = View.inflate(activity, R.layout.item_day_class, null);

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
                                (int) activity.getResources()
                                        .getDimension(R.dimen.cell_30min_height) * length);
                        scheduleCell.setLayoutParams(lp);

                        //Check if we need to make the course clickable
                        if(clickableCourses){
                            //We need a final variable for the onClick listener
                            final Course course = currentCourse;
                            //OnClick: CourseActivity (for a detailed description of the course)
                            scheduleCell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DialogHelper.showCourseDialog(activity, course, analytics);
                                }
                            });
                        }
                        else{
                            scheduleCell.setClickable(false);
                        }

                    }
                    else{
                        //Inflate the empty view
                        scheduleCell = View.inflate(activity, R.layout.item_day_empty, null);
                    }

                    //Add the given view to the schedule container
                    scheduleContainer.addView(scheduleCell);
                }
            }
        }
    }

    /**
     * Renders the portrait view
     *
     * @param view The base view
     */
    private void renderPortraitView(View view){
        //Set up the adapter
        final SchedulePagerAdapter adapter =
                new SchedulePagerAdapter(activity.getSupportFragmentManager(), date);

        //Set up the ViewPager
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(adapter.getFirstDayIndex());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {}
            @Override
            public void onPageSelected(int i) {
                //Update the view builder's date every time the page is turned to have the right
                //  week if ever the user rotates his device
                date = adapter.getDate(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });
    }

    /**
     * The adapter used for the ViewPager in the portrait view of the schedule
     */
    private class SchedulePagerAdapter extends FragmentStatePagerAdapter {
        /**
         * The starting date
         */
        private LocalDate startingDate;
        /**
         * The index of the first day
         */
        private int firstDayIndex;

        /**
         * Default Constructor
         *
         * @param fm   The fragment manager
         * @param date The starting date
         */
        public SchedulePagerAdapter(FragmentManager fm, LocalDate date){
            super(fm);
            this.startingDate = date;
            //Get the first day (offset of 500001 to get the right day)
            this.firstDayIndex = 500001 + ScheduleViewBuilder.this.date.getDayOfWeek().getValue();
        }

        @Override
        public Fragment getItem(int i) {
            LocalDate date = getDate(i);
            return DayFragment.newInstance(date,TwentyFourHourPrefs.get());
        }

        @Override
        public int getCount() {
            return 1000000;
        }

        @Override
        public int getItemPosition(Object object) {
            //This is to force the refreshing of all of the views when the view is reloaded
            return POSITION_NONE;
        }

        /**
         * @return The first day index
         */
        public int getFirstDayIndex(){
            return this.firstDayIndex;
        }

        /**
         * Gets the date at index i
         *
         * @param i The index
         * @return The corresponding date
         */
        public LocalDate getDate(int i){
            return startingDate.plusDays(i - firstDayIndex);
        }
    }
}
