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

package ca.appvelopers.mcgillmobile.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.util.DateUtils;

/**
 * Displays the user's schedule for a given day
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.Holder> {
    private LocalDate date;
    private List<Course> courses;
    private Map<LocalTime, List<Course>> schedule;

    /**
     * Default Constructor
     *
     * @param date Date to represent
     */
    public DayAdapter(LocalDate date, List<Course> courses) {
        this.date = date;
        this.courses = courses;
        schedule = new HashMap<>();
        update();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_half_hour, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.bind(getTime(position));
    }

    @Override
    public int getItemCount() {
        // Every half hour between 8 AM and 10 PM
        return 28;
    }

    /**
     * TODO
     * @param position
     * @return
     */
    public LocalTime getTime(int position) {
        return LocalTime.of(8, 0).plusMinutes(position * 30);
    }

    private void update() {
        //Clear the current schedule
        schedule.clear();

        //Go through the courses
        for (Course course : courses) {
            //Go through the course times, half hour by half hour
            for (LocalTime time = course.getRoundedStartTime();
                 course.getRoundedEndTime().isAfter(time); time.plusMinutes(30)) {

                List<Course> timeCourses = schedule.get(time);

                if (timeCourses == null) {
                    //Make a list for the given time if there is none already, and save it bacl
                    timeCourses = new ArrayList<>();
                    schedule.put(time, timeCourses);
                }

                //Add the course
                timeCourses.add(course);
            }
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        /**
         * Line for the top of the time, to be shown at the beginning of every hour
         */
        @Bind(R.id.time_line)
        protected View timeLine;
        /**
         * Time, to be shown at the beginning of every hour
         */
        @Bind(R.id.time)
        protected TextView timeTitle;
        /**
         * The course slot
         */
        @Bind(R.id.slot)
        protected LinearLayout slot;
        /**
         * Divider between half hours, to hide if there's a course to show
         */
        @Bind(R.id.divider)
        protected LinearLayout divider;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(LocalTime time) {
            //If the time is not a whole hour, hide the time and time line views
            boolean showTime = time.getMinute() == 0;

            timeLine.setVisibility(showTime ? View.VISIBLE : View.GONE);
            timeTitle.setVisibility(showTime ? View.VISIBLE : View.GONE);

            //Set the time if we are showing it
            if (showTime) {
                timeTitle.setText(DateUtils.getHourString(time.getHour()));
            }
        }
    }
}
