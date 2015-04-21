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

package ca.appvelopers.mcgillmobile.ui.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Date;

/**
 * Represents one day in the schedule in portrait mode
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class DayFragment extends Fragment{
    /**
     * The day this represents
     */
    private Day mDay;
    /**
     * The date this represents
     */
    private LocalDate mDate;
    /**
     * The list of courses to show
     */
    private List<Course> mCourses;

    /**
     * Creates a new DayFragment instance with bundled arguments
     *
     * @param day  The day
     * @param date The date
     * @return The DayFragment instance
     */
    public static DayFragment newInstance(Day day, LocalDate date){
        DayFragment fragment = new DayFragment();

        //Put the arguments in the bundle
        Bundle args = new Bundle();
        args.putSerializable(Constants.DAY, day);
        args.putSerializable(Constants.DATE, date);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Get the arguments from the bundle
        mDay = (Day)getArguments().get(Constants.DAY);
        mDate = (LocalDate)getArguments().get(Constants.DATE);
        //Get the courses from the ScheduleFragment
        mCourses = ((ScheduleFragment)getParentFragment()).getCourses(mDay, mDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        //Day Title
        TextView dayTitle = (TextView)view.findViewById(R.id.day_title);
        dayTitle.setText(mDay.getDayString(getActivity()));

        //Date Title
        TextView dayDate = (TextView)view.findViewById(R.id.day_date);
        dayDate.setText(Date.getDateString(mDate));

        //Get the container for the timetable
        LinearLayout timetableContainer = (LinearLayout)view.findViewById(R.id.timetable_container);
        //Get the container for the schedule
        LinearLayout scheduleContainer = (LinearLayout)view.findViewById(R.id.schedule_container);

        //Fill it up
        fillSchedule(timetableContainer, scheduleContainer);

        return view;
    }

    /**
     * Fills the schedule based on the courses
     *
     * @param timetableContainer The container for the timetable
     * @param scheduleContainer  The container for the schedule
     */
    private void fillSchedule(LinearLayout timetableContainer, LinearLayout scheduleContainer){
        //This will be used for the end time of a course when it is added to the schedule container
        LocalTime currentCourseEndTime = null;

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Start inflating a timetable cell
            View timetableCell = View.inflate(getActivity(), R.layout.item_day_timetable, null);

            //Put the correct time
            TextView time = (TextView)timetableCell.findViewById(R.id.cell_time);
            time.setText(Date.getHourString(hour));

            //Add it to the right container
            timetableContainer.addView(timetableCell);

            //Cycle through the half hours
            for(int min = 0; min < 31; min += 30){
                //Initialize the current course to null
                Course currentCourse = null;

                LocalTime currentTime = new LocalTime(hour, min);

                //if currentCourseEndTime = null (no course is being added) or it is equal to
                //the current time in min (end of a course being added) we need to add a new view
                if(currentCourseEndTime == null || currentCourseEndTime.equals(currentTime)){
                    //Reset currentCourseEndTime to null
                    currentCourseEndTime = null;

                    //Check if there is a course at this time
                    for(Course course : mCourses){
                        //If there is, set the current course to that time,
                        //  and get the ending time of this course
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
                        scheduleCell = View.inflate(getActivity(), R.layout.item_day_class, null);

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
                                (int) getActivity().getResources()
                                        .getDimension(R.dimen.cell_30min_height) * length);
                        scheduleCell.setLayoutParams(lp);

                        //We need a final variable for the onClick listener
                        final Course course = currentCourse;
                        //OnClick: CourseActivity (for a detailed description of the course)
                        scheduleCell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new CourseDialog(getActivity(), course).show();
                            }
                        });
                    }
                    else{
                        //Inflate the empty view
                        scheduleCell = View.inflate(getActivity(), R.layout.item_day_empty, null);
                    }

                    //Add the given view to the schedule container
                    scheduleContainer.addView(scheduleCell);
                }
            }
        }
    }
}