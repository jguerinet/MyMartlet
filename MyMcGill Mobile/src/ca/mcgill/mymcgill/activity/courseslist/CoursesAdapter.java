package ca.mcgill.mymcgill.activity.courseslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Course;

/**
 * Author : Julien
 * Date :  2014-05-26 7:14 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class CoursesAdapter extends BaseAdapter {
    private Context mContext;
    private List<Course> mCourses;
    private List<Course> mCheckedCourses;

    public CoursesAdapter(Context context, List<Course> courses){
        this.mContext = context;
        this.mCourses = courses;
        this.mCheckedCourses = new ArrayList<Course>();
    }

    @Override
    public int getCount() {
        return mCourses.size();
    }

    @Override
    public Course getItem(int i) {
        return mCourses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //Inflate the view if it is null
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_course, null);
        }

        //Get the concerned course
        final Course course = getItem(i);

        //Code
        TextView courseCode = (TextView)view.findViewById(R.id.course_code);
        courseCode.setText(course.getCourseCode());

        //Title
        TextView courseTitle = (TextView)view.findViewById(R.id.course_title);
        courseTitle.setText(course.getCourseTitle());

        //Credits
        TextView courseCredits = (TextView)view.findViewById(R.id.course_credits);
        courseCredits.setText(String.valueOf(mContext.getString(R.string.course_credits, course.getCredits())));

        //Days
        TextView courseDays = (TextView)view.findViewById(R.id.course_days);
        courseDays.setText(course.getDays());

        //Hours
        TextView courseHours = (TextView)view.findViewById(R.id.course_hours);
        courseHours.setText(course.getTime());

        //Set up the checkbox
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.course_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //If it becomes checked, add it to the list. If not, remove it
                if(b){
                    mCheckedCourses.add(course);
                }
                else {
                    mCheckedCourses.remove(course);
                }
            }
        });

        return view;
    }

    /**
     * Get the list of checked courses
     * @return The checked courses.
     */
    public List<Course> getCheckedCourses(){
        return mCheckedCourses;
    }
}
