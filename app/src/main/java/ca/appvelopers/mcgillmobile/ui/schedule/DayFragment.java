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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.utils.DateUtils;

import org.threeten.bp.LocalDate;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.DayUtils;

/**
 * Represents one day in the schedule in portrait mode
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class DayFragment extends Fragment{
    /**
     * The date this represents
     */
    private LocalDate date;
    /**
     * The list of courses to show
     */
    private List<Course> courses;

    /**
     * Creates a new DayFragment instance with bundled arguments
     *
     * @param date The date
     * @return The DayFragment instance
     */
    public static DayFragment newInstance(LocalDate date) {
        DayFragment fragment = new DayFragment();

        //Put the arguments in the bundle
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATE, date);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the arguments from the bundle
        date = (LocalDate) getArguments().get(Constants.DATE);
        //Get the courses from the ScheduleActivity
        courses = ((ScheduleActivity) getActivity()).getCourses(date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_day, container, false);

        //Day Title
        TextView dayTitle = (TextView)view.findViewById(R.id.day_title);
        dayTitle.setText(DayUtils.getString(getActivity(), date.getDayOfWeek()));

        //Date Title
        TextView dayDate = (TextView)view.findViewById(R.id.day_date);
        dayDate.setText(DateUtils.getLongDateString(date));

        //Get the container for the timetable
        LinearLayout timetableContainer = (LinearLayout)view.findViewById(R.id.timetable_container);
        //Get the container for the schedule
        LinearLayout scheduleContainer = (LinearLayout)view.findViewById(R.id.schedule_container);

        //Fill it up
        ScheduleViewBuilder.fillSchedule(getActivity(), timetableContainer, scheduleContainer,
                courses, true);

        return view;
    }
}