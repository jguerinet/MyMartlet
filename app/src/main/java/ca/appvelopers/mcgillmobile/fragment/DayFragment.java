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

package ca.appvelopers.mcgillmobile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.ClassItem;
import ca.appvelopers.mcgillmobile.model.Day;
import ca.appvelopers.mcgillmobile.ui.dialog.CourseDialog;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Date;

/**
 * Fragment that represents one day in the schedule
 * Author: Julien
 * Date: 01/02/14, 7:10 PM
 */
public class DayFragment extends Fragment{
    private Day mDay;
    private DateTime mDate;
    private List<ClassItem> mClassItems;

    public static DayFragment newInstance(Day day, DateTime date){
        DayFragment fragment = new DayFragment();

        //Put the day in the bundle
        Bundle args = new Bundle();
        args.putSerializable(Constants.DAY, day);
        args.putSerializable(Constants.DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    //Empty Constructor (Required for Fragments)
    public DayFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mDay = (Day)getArguments().get(Constants.DAY);
        mDate = (DateTime)getArguments().get(Constants.DATE);
        //Get the courses from ScheduleActivity
        mClassItems = ((ScheduleFragment)getParentFragment()).getClassesForDate(mDay, mDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        TextView dayTitle = (TextView)view.findViewById(R.id.day_title);
        dayTitle.setText(mDay.getDayString(getActivity()));

        TextView dayDate = (TextView)view.findViewById(R.id.day_date);
        dayDate.setText(Date.getDateString(mDate));

        //Get the container for the timetable
        LinearLayout timetableContainer = (LinearLayout)view.findViewById(R.id.timetable_container);
        //Get the container for the schedule
        LinearLayout scheduleContainer = (LinearLayout)view.findViewById(R.id.schedule_container);

        //Fill it up
        fillSchedule(inflater, timetableContainer, scheduleContainer);

        return view;
    }

    //Method that fills the schedule based on given data
    private void fillSchedule(LayoutInflater inflater, LinearLayout timetableContainer,
                             LinearLayout scheduleContainer){
        //This will be used of an end time of a course when it is added to the schedule container
        LocalTime currentCourseEndTime = null;

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Start inflating a timetable cell
            View timetableCell = inflater.inflate(R.layout.item_day_timetable, null);

            //Quick check
            assert(timetableCell != null);

            //Put the correct time
            TextView time = (TextView)timetableCell.findViewById(R.id.cell_time);
            time.setText(Date.getHourString(getActivity(), hour));

            //Add it to the right container
            timetableContainer.addView(timetableCell);

            //Cycle through the half hours
            for(int min = 0; min < 31; min+= 30){
                //Initialize the current course to null
                ClassItem currentClass = null;

                LocalTime currentTime = new LocalTime(hour, min);

                //if currentCourseEndTime = null (no course is being added) or it is equal to
                //the current time in min (end of a course being added) we need to add a new view
                if(currentCourseEndTime == null || currentCourseEndTime.equals(currentTime)){
                    //Reset currentCourseEndTime to null
                    currentCourseEndTime = null;

                    //Check if there is a course at this time
                    for(ClassItem classItem : mClassItems){
                        //If there is, set the current course to that time, and calculate the
                        //ending time of this course
                        if(classItem.getRoundedStartTime().equals(currentTime)){
                            currentClass = classItem;
                            currentCourseEndTime = classItem.getRoundedEndTime();
                            break;
                        }
                    }

                    View scheduleCell;

                    //There is a course at this time
                    if(currentClass != null){
                        //Inflate the right view
                        scheduleCell = inflater.inflate(R.layout.item_day_class, null);

                        //Set up all of the info
                        TextView courseName = (TextView)scheduleCell.findViewById(R.id.course_code);
                        courseName.setText(currentClass.getCode());

                        TextView courseType = (TextView)scheduleCell.findViewById(R.id.course_type);
                        courseType.setText(currentClass.getType());

                        TextView  courseTime = (TextView)scheduleCell.findViewById(R.id.course_time);
                        courseTime.setText(currentClass.getTimeString(getActivity()));

                        TextView courseLocation = (TextView)scheduleCell.findViewById(R.id.course_location);
                        courseLocation.setText(currentClass.getLocation());

                        //Find out how long this course is in terms of blocks of 30 min
                        int length = Minutes.minutesBetween(currentClass.getRoundedStartTime(), currentClass.getRoundedEndTime()).getMinutes() / 30;

                        //Set the height of the view depending on this height
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) getActivity().getResources().getDimension(R.dimen.cell_30min_height) * length);
                        scheduleCell.setLayoutParams(lp);

                        //We need a final variable for the onClick listener
                        final ClassItem course = currentClass;
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
                        scheduleCell = inflater.inflate(R.layout.item_day_empty, null);

                        //Quick check
                        assert(scheduleCell != null);
                    }

                    //Add the given view to the schedule container
                    scheduleContainer.addView(scheduleCell);
                }
            }
        }
    }
}