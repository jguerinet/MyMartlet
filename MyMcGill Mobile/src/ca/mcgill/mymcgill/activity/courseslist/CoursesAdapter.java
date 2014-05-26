package ca.mcgill.mymcgill.activity.courseslist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

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

        }

        //Get the concerned course
        Course course = getItem(i);

        return view;
    }
}
