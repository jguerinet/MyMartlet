package ca.mcgill.mymcgill.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.Day;
import ca.mcgill.mymcgill.util.Constants;

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

        //Get the container for the actual schedule
        LinearLayout scheduleContainer = (LinearLayout)view.findViewById(R.id.schedule_container);
        //Fill it up
        fillSchedule(inflater, scheduleContainer);

        return view;
    }

    //Method that fills the schedule based on given data
    public void fillSchedule(LayoutInflater inflater, LinearLayout parentView){
        //Make a copy of the course
        List<CourseSched> availableCourses = new ArrayList<CourseSched>(mCourses);

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Cycle through the half hours
            for(int min = 0; min < 2; min++){
                CourseSched currentCourse = null;

                //Check if there is a course at this time
                for(CourseSched course : availableCourses){
                    if(course.getStartHour() == hour && course.getStartMinute() == min){
                        currentCourse = course;
                        break;
                    }
                }

                View view;

                //There is a course at this time
                if(currentCourse != null){
                    //Start by removing it from the list of available courses
                    //This will speed up the loop above since there will less courses to find
                    availableCourses.remove(currentCourse);

                    //Inflate the view
                    view = inflater.inflate(R.layout.fragment_day_cell, null);

                    //Set up all of the info

                    //Figure out if we need more views

                    //Find out how long this course is
                    int length = (currentCourse.getEndHour() - currentCourse.getStartHour()) * 60 +
                            (currentCourse.getEndMinute() - currentCourse.getStartMinute());
                }
                else{
                    //Inflate the empty view
                    view = inflater.inflate(R.layout.fragment_day_cell_empty, null);

                }

                //Set up the time
                String hours = hour == 12 ? "12" : String.valueOf(hour % 12) ;
                String minutes = min == 0 ? "00" : "30";
                TextView time = (TextView)view.findViewById(R.id.cell_time);
                time.setText(hours + ":" + minutes);

                boolean am = hour / 12 == 0;
                TextView amView = (TextView)view.findViewById(R.id.cell_am);
                amView.setText(am ? "A.M." : "P.M.");
                parentView.addView(view);
            }
        }
    }
}