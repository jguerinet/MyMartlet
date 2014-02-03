package ca.mcgill.mymcgill.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.objects.CourseSched;
import ca.mcgill.mymcgill.objects.Day;
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
        //Cycle through the hours
        for(int i = 8; i < 22; i++){
            //Cycle through the half hours
            for(int j = 0; j < 2; j++){
                //Inflate the empty view
                View emptyView = inflater.inflate(R.layout.fragment_day_cell_empty, null);

                String hours = i == 12 ? "12" : String.valueOf(i % 12) ;
                String minutes = j == 0 ? "00" : "30";
                TextView time = (TextView)emptyView.findViewById(R.id.cell_time);
                time.setText(hours + ":" + minutes);

                boolean am = i / 12 == 0;
                TextView amView = (TextView)emptyView.findViewById(R.id.cell_am);
                amView.setText(am ? "A.M." : "P.M.");
                parentView.addView(emptyView);
            }
        }
    }
}