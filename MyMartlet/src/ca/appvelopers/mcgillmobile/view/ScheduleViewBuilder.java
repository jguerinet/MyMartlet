package ca.appvelopers.mcgillmobile.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.fragment.ScheduleFragment;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Author: Julien Guerinet
 * Date: 2014-09-19 10:52 AM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class ScheduleViewBuilder {
    public ScheduleFragment mFragment;
    public Context mContext;

    public ScheduleViewBuilder(ScheduleFragment fragment){
        this.mFragment = fragment;
        this.mContext = fragment.getActivity();
    }

    public View renderLandscapeView(DateTime startingDate){
        //Load the right view
        View view = View.inflate(mContext, R.layout.fragment_schedule_land, null);

        //Fill out the timetable container
        fillTimetable(view);

        //Get the schedule container
        LinearLayout scheduleContainer = (LinearLayout) view.findViewById(R.id.schedule_container);

        //Find the index of the given date (days are offset by 1 in Jodatime)
        int currentDayIndex = startingDate.getDayOfWeek() - 1;

        //Fill out the schedule
        for(int i = 0; i < 7; i ++){
            LinearLayout coursesLayout = new LinearLayout(mContext);
            coursesLayout.setOrientation(LinearLayout.VERTICAL);
            coursesLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    mFragment.getResources().getDimensionPixelSize(R.dimen.cell_landscape_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            fillSchedule(Day.getDay(i), startingDate.plusDays(i - currentDayIndex), coursesLayout);
            scheduleContainer.addView(coursesLayout);

            //Line
            View line = new View(mContext);
            line.setBackgroundColor(mContext.getResources().getColor(R.color.black));
            line.setLayoutParams(new ViewGroup.LayoutParams(mFragment.getResources().getDimensionPixelSize(R.dimen.line),
                    ViewGroup.LayoutParams.MATCH_PARENT));
            scheduleContainer.addView(line);
        }

        return view;
    }

    //Fills the timetable without the classes
    private void fillTimetable(View view){
        //Get the timetable container
        LinearLayout timetableContainer = (LinearLayout) view.findViewById(R.id.timetable_container);

        //Empty view for the days
        //Day name
        View dayView = View.inflate(mContext, R.layout.fragment_day_name, null);

        //Black line
        View dayViewLine = dayView.findViewById(R.id.day_line);
        dayViewLine.setVisibility(View.VISIBLE);

        timetableContainer.addView(dayView);

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Start inflating a timetable cell
            View timetableCell = View.inflate(mContext, R.layout.item_day_timetable, null);

            //Put the correct time
            TextView time = (TextView)timetableCell.findViewById(R.id.cell_time);
            time.setText(Help.getShortTimeString(mContext, hour));

            //Add it to the right container
            timetableContainer.addView(timetableCell);
        }
    }

    //Method that fills the schedule based on given data
    private void fillSchedule(Day currentDay, DateTime date, LinearLayout scheduleContainer){
        //This will be used of an end time of a course when it is added to the schedule container
        LocalTime currentCourseEndTime = null;

        //Get the classes for today
        List<ClassItem> classItems = mFragment.getClassesForDate(currentDay, date);

        //Day name
        View dayView = View.inflate(mContext, R.layout.fragment_day_name, null);
        TextView dayViewTitle = (TextView)dayView.findViewById(R.id.day_name);
        dayViewTitle.setText(currentDay.getDayString(mContext));

        scheduleContainer.addView(dayView);

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Cycle through the half hours
            for(int min = 0; min < 31; min+= 30){
                //Initialize the current course to null
                ClassItem currentClass = null;

                //Get the current time
                LocalTime currentTime = new LocalTime(hour, min);

                //if currentCourseEndTime = null (no course is being added) or it is equal to
                //the current time in min (end of a course being added) we need to add a new view
                if(currentCourseEndTime == null || currentCourseEndTime.equals(currentTime)){
                    //Reset currentCourseEndTime
                    currentCourseEndTime = null;

                    //Check if there is a course at this time
                    for(ClassItem course : classItems){
                        //If there is, set the current course to that time, and calculate the
                        //ending time of this course
                        if(course.getStartTime().equals(currentTime)){
                            currentClass = course;
                            currentCourseEndTime = course.getEndTime();
                            break;
                        }
                    }

                    View scheduleCell;

                    //There is a course at this time
                    if(currentClass != null){
                        //Inflate the right view
                        scheduleCell = View.inflate(mContext, R.layout.item_day_class, null);

                        //Quick check
                        assert(scheduleCell != null);

                        //Set up all of the info
                        TextView courseName = (TextView)scheduleCell.findViewById(R.id.course_code);
                        courseName.setText(currentClass.getCourseCode());

                        TextView courseType = (TextView)scheduleCell.findViewById(R.id.course_type);
                        courseType.setText(currentClass.getSectionType());

                        TextView  courseTime = (TextView)scheduleCell.findViewById(R.id.course_time);
                        courseTime.setText(currentClass.getTimeString(mContext));

                        TextView courseLocation = (TextView)scheduleCell.findViewById(R.id.course_location);
                        courseLocation.setText(currentClass.getLocation());

                        //Find out how long this course is in terms of blocks of 30 min
                        int length = Minutes.minutesBetween(currentClass.getStartTime(), currentClass.getEndTime()).getMinutes() / 30;

                        //Set the height of the view depending on this height
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) mFragment.getResources().getDimension(R.dimen.cell_30min_height) * length);
                        scheduleCell.setLayoutParams(lp);

                        //OnClick: CourseActivity (for                                                                                                                                                                                                  b                                                                                                                                               a detailed description of the course)
                        scheduleCell.setClickable(false);
                    }
                    else{
                        //Inflate the empty view
                        scheduleCell = View.inflate(mContext, R.layout.item_day_empty, null);

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
