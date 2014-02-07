package ca.mcgill.mymcgill.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.CourseActivity;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.Day;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.Help;

/**
 * Fragment that represents one day in the schedule
 * Author: Julien
 * Date: 01/02/14, 7:10 PM
 */
public class DayFragment extends Fragment{
    private Day mDay;
    private List<CourseSched> mCourses;

    public static DayFragment newInstance(Day day){
        DayFragment fragment = new DayFragment();

        //Put the day in the bundle
        Bundle args = new Bundle();
        args.putSerializable(Constants.DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    //Empty Constructor (Required for Fragments)
    public DayFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mDay = (Day)getArguments().get(Constants.DAY);
        //Get the courses from ScheduleActivity
        mCourses = ((ScheduleActivity)getActivity()).getCoursesForDay(mDay);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        TextView dayTitle = (TextView)view.findViewById(R.id.day_title);
        dayTitle.setText(mDay.getDayString(getActivity()));

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
        int currentCourseEndTime = 0;

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Start inflating a timetable cell
            View timetableCell = inflater.inflate(R.layout.fragment_day_timetable_cell, null);

            //Quick check
            assert(timetableCell != null);

            //Put the correct time
            TextView time = (TextView)timetableCell.findViewById(R.id.cell_time);
            time.setText(Help.getShortTimeString(getActivity(), hour));

            //Add it to the right container
            timetableContainer.addView(timetableCell);

            //Cycle through the half hours
            for(int min = 0; min < 31; min+= 30){
                //Initialize the current course to null
                CourseSched currentCourse = null;

                //Calculate time in minutes
                int timeInMinutes = 60*hour + min;

                //if currentCourseEndTime = 0 (no course is being added) or it is equal to
                //the current time in min (end of a course being added) we need to add a new view
                if(currentCourseEndTime == 0 || currentCourseEndTime == timeInMinutes){
                    //Reset currentCourseEndTime to 0
                    currentCourseEndTime = 0;

                    //Check if there is a course at this time
                    for(CourseSched course : mCourses){
                        //If there is, set the current course to that time, and calculate the
                        //ending time of this course
                        if(course.getStartTimeInMinutes() == timeInMinutes){
                            currentCourse = course;
                            currentCourseEndTime = course.getEndTimeInMinutes();
                            break;
                        }
                    }

                    View scheduleCell;

                    //There is a course at this time
                    if(currentCourse != null){
                        //Inflate the right view
                        scheduleCell = inflater.inflate(R.layout.fragment_day_cell, null);

                        //Quick check
                        assert(scheduleCell != null);

                        //Set up all of the info
                        TextView courseName = (TextView)scheduleCell.findViewById(R.id.course_code);
                        courseName.setText(currentCourse.getCourseCode());

                        TextView  courseTime = (TextView)scheduleCell.findViewById(R.id.course_time);
                        //Get the beginning time
                        String beginningTime = Help.getLongTimeString(getActivity(), hour, min);
                        //Get the end time
                        String endTime = Help.getLongTimeString(getActivity(), currentCourse.getEndHour(), currentCourse.getEndMinute());

                        courseTime.setText(getResources().getString(R.string.course_time, beginningTime, endTime));

                        TextView courseLocation = (TextView)scheduleCell.findViewById(R.id.course_location);
                        courseLocation.setText(currentCourse.getRoom());

                        //Find out how long this course is in terms of blocks of 30 min
                        int length = ((currentCourse.getEndHour() - currentCourse.getStartHour()) * 60 +
                                (currentCourse.getEndMinute() - currentCourse.getStartMinute())) / 30;

                        //Set the height of the view depending on this height
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) getActivity().getResources().getDimension(R.dimen.cell_30min_height) * length);
                        scheduleCell.setLayoutParams(lp);

                        //We need a final variable for the onClick listener
                        final CourseSched course = currentCourse;
                        //OnClick: CourseActivity (for a detailed description of the course)
                        scheduleCell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), CourseActivity.class);
                                intent.putExtra(Constants.COURSE, course);
                                startActivity(intent);
                            }
                        });
                    }
                    else{
                        //Inflate the empty view
                        scheduleCell = inflater.inflate(R.layout.fragment_day_cell_empty, null);

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