package ca.mcgill.mymcgill.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.objects.Day;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Fragment that represents one day in the schedule
 * Author: Julien
 * Date: 01/02/14, 7:10 PM
 */
public class DayFragment extends Fragment{
    private Day mDay;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_day, container, false);
    }
}