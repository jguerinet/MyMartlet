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

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Day;
import ca.appvelopers.mcgillmobile.ui.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Date;

/**
 * Builds the schedule view based on the orientation and the current date
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class ScheduleViewBuilder {
    /**
     * The ScheduleActivity instance
     */
    private ScheduleActivity mActivity;
    /**
     * The starting date
     */
    private LocalDate mDate;

    /**
     * Default Constructor
     *
     * @param activity     {@link ScheduleActivity} instance
     * @param startingDate Starting date
     */
    public ScheduleViewBuilder(ScheduleActivity activity, LocalDate startingDate) {
        mActivity = activity;
        mDate = startingDate;
    }

    /**
     * Loads, fills and returns the view to use given the orientation
     *
     * @param orientation The current screen orientation
     * @return The view to use
     */
    public View renderView(int orientation) {
        //Inflate the view
        View view = View.inflate(mActivity, R.layout.activity_schedule, null);

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
        View dayView = View.inflate(mActivity, R.layout.fragment_day_name, null);
        //Black line to separate the timetable from the schedule
        View dayViewLine = dayView.findViewById(R.id.day_line);
        dayViewLine.setVisibility(View.VISIBLE);

        //Add the day view to the top of the timetable
        timetableContainer.addView(dayView);

        //Get the schedule container
        LinearLayout dayContainer = (LinearLayout)view.findViewById(R.id.schedule_container);

        //Find the index of the given date (days are offset by 1 in Jodatime)
        int currentDayIndex = mDate.getDayOfWeek() - 1;

        //Go through the 7 days of the week
        for(int i = 0; i < 7; i ++){
            Day day = Day.getDay(i);

            //Set up the day name
            dayView = View.inflate(mActivity, R.layout.fragment_day_name, null);
            TextView dayViewTitle = (TextView)dayView.findViewById(R.id.day_name);
            dayViewTitle.setText(day.toString());
            dayContainer.addView(dayView);

            //Set up the schedule container for that one day
            LinearLayout scheduleContainer = new LinearLayout(mActivity);
            scheduleContainer.setOrientation(LinearLayout.VERTICAL);
            scheduleContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    mActivity.getResources().getDimensionPixelSize(R.dimen.cell_landscape_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            //Get the classes for today
            List<Course> courses = mActivity.getCourses(mDate.plusDays(i - currentDayIndex));

            //Fill the schedule for the current day
            fillSchedule(mActivity, timetableContainer, scheduleContainer, courses, false);

            //Add the current day to the schedule container
            dayContainer.addView(scheduleContainer);

            //Line
            View line = new View(mActivity);
            line.setBackgroundColor(Color.BLACK);
            line.setLayoutParams(new ViewGroup.LayoutParams(
                    mActivity.getResources().getDimensionPixelSize(R.dimen.schedule_line),
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
                                    boolean clickableCourses){
        //This will be used of an end time of a course when it is added to the schedule container
        LocalTime currentCourseEndTime = null;

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Start inflating a timetable cell
            View timetableCell = View.inflate(activity, R.layout.item_day_timetable, null);

            //Put the correct time
            TextView time = (TextView)timetableCell.findViewById(R.id.cell_time);
            time.setText(Date.getHourString(hour));

            //Add it to the right container
            timetableContainer.addView(timetableCell);

            //Cycle through the half hours
            for(int min = 0; min < 31; min+= 30){
                //Initialize the current course to null
                Course currentCourse = null;

                //Get the current time
                LocalTime currentTime = new LocalTime(hour, min);

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
                        int length = Minutes.minutesBetween(currentCourse.getRoundedStartTime(),
                                currentCourse.getRoundedEndTime()).getMinutes() / 30;

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
                                    DialogHelper.showCourseDialog(activity, course);
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
                new SchedulePagerAdapter(mActivity.getSupportFragmentManager(), mDate);

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
                mDate = adapter.getDate(i);
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
        private LocalDate mStartingDate;
        /**
         * The index of the first day
         */
        private int mFirstDayIndex;

        /**
         * Default Constructor
         *
         * @param fm   The fragment manager
         * @param date The starting date
         */
        public SchedulePagerAdapter(FragmentManager fm, LocalDate date){
            super(fm);
            this.mStartingDate = date;
            //Get the first day (offset of 500002 to get the right day)
            this.mFirstDayIndex = 500002 + mDate.getDayOfWeek();
        }

        @Override
        public Fragment getItem(int i) {
            LocalDate date = getDate(i);
            return DayFragment.newInstance(date);
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
            return this.mFirstDayIndex;
        }

        /**
         * Gets the date at index i
         *
         * @param i The index
         * @return The corresponding date
         */
        public LocalDate getDate(int i){
            return mStartingDate.plusDays(i - mFirstDayIndex);
        }
    }
}
