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

package ca.appvelopers.mcgillmobile.ui.semester;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Semester;

/**
 * List Adapter that will populate the courses list in SemesterActivity
 * Author: Julien
 * Date: 01/02/14, 10:27 AM
 */
public class SemesterAdapter extends BaseAdapter {
    private Context mContext;
    private List<Course> mCourses;

    public SemesterAdapter(Context context, Semester semester){
        this.mContext = context;
        this.mCourses = semester.getCourses();
    }

    @Override
    public int getCount() {
        return mCourses.size();
    }

    @Override
    public Course getItem(int position) {
        return mCourses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        //Items are not clickable
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_course, null);
        }

        //Get the current course
        Course currentCourse = getItem(position);

        //Set up the info for the course
        TextView courseCode = (TextView)view.findViewById(R.id.course_code);
        courseCode.setText(currentCourse.getCourseCode());

        TextView courseGrade = (TextView)view.findViewById(R.id.course_grade);
        courseGrade.setText(currentCourse.getUserGrade());

        TextView courseTitle = (TextView)view.findViewById(R.id.course_title);
        courseTitle.setText(currentCourse.getCourseTitle());

        TextView courseCredits = (TextView)view.findViewById(R.id.course_credits);
        courseCredits.setText(mContext.getString(R.string.course_credits, currentCourse.getCredits()));

        TextView courseAverage = (TextView)view.findViewById(R.id.course_average);
        //Don't display average if it does not exist
        if(!currentCourse.getAverageGrade().equals("")){
            courseAverage.setText(mContext.getString(R.string.course_average, currentCourse.getAverageGrade()));
        }


        return view;
    }
}
