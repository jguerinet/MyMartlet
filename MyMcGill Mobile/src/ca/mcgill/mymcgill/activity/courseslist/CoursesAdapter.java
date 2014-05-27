package ca.mcgill.mymcgill.activity.courseslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    public CoursesAdapter(Context context, List<Course> courses){
        this.mContext = context;
        this.mCourses = courses;
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
            view = LayoutInflater.from(mContext).inflate(R.layout.activity_semester_course, null);
        }

        //Get the concerned course
        Course course = getItem(i);

        //Set up the info for the course
        TextView courseCode = (TextView)view.findViewById(R.id.course_code);
        courseCode.setText(course.getCourseCode());

        TextView courseGrade = (TextView)view.findViewById(R.id.course_grade);
        courseGrade.setText(course.getCrn());

        TextView courseTitle = (TextView)view.findViewById(R.id.course_title);
        courseTitle.setText(course.getCourseTitle());

        TextView courseCredits = (TextView)view.findViewById(R.id.course_credits);
        courseCredits.setText(mContext.getString(R.string.course_credits, course.getCredits()));

        return view;
    }
}
