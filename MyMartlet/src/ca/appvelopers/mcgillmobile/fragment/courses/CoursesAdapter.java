package ca.appvelopers.mcgillmobile.fragment.courses;

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

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.object.Term;

/**
 * Author : Julien
 * Date :  2014-05-26 7:14 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class CoursesAdapter extends BaseAdapter {
    private Context mContext;
    private List<ClassItem> mClassItems;
    private List<ClassItem> mCheckedClassItems;
    private boolean mCanUnregister;

    public CoursesAdapter(Context context, Term term, boolean canUnregister){
        this.mContext = context;
        this.mClassItems = new ArrayList<ClassItem>();
        this.mCheckedClassItems = new ArrayList<ClassItem>();
        this.mCanUnregister = canUnregister;

        //Add only the courses for this term
        for(ClassItem classItem : App.getClasses()){
            if(classItem.getTerm().equals(term)){
                mClassItems.add(classItem);
            }
        }
    }

    @Override
    public int getCount() {
        return mClassItems.size();
    }

    @Override
    public ClassItem getItem(int i) {
        return mClassItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //Inflate the view if it is null
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_class, null);
        }

        //Get the concerned course
        final ClassItem currentClassItem = getItem(i);

        //Code
        TextView courseCode = (TextView)view.findViewById(R.id.course_code);
        courseCode.setText(currentClassItem.getCourseCode());

        //Title
        TextView courseTitle = (TextView)view.findViewById(R.id.course_title);
        courseTitle.setText(currentClassItem.getCourseTitle());

        //Type
        TextView courseType = (TextView)view.findViewById(R.id.course_type);
        courseType.setText(currentClassItem.getSectionType());

        //Credits - Don't show this
        TextView courseCredits = (TextView)view.findViewById(R.id.course_credits);
        courseCredits.setText(mContext.getString(R.string.course_credits, currentClassItem.getCredits()));

        //Days
        TextView courseDays = (TextView)view.findViewById(R.id.course_days);
        courseDays.setText(Day.getDayStrings(currentClassItem.getDays()));

        //Hours
        TextView courseHours = (TextView)view.findViewById(R.id.course_hours);
        courseHours.setText(currentClassItem.getTimeString(mContext));

        //Set up the checkbox
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.course_checkbox);
        //Don't show it if we are only viewing the courses
        if(!mCanUnregister){
            checkBox.setVisibility(View.GONE);
        }
        else{
            checkBox.setVisibility(View.VISIBLE);
            //Remove any other listeners
            checkBox.setOnCheckedChangeListener(null);
            //Initially unchecked
            checkBox.setChecked(mCheckedClassItems.contains(currentClassItem));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //If it becomes checked, add it to the list. If not, remove it
                    if(b){
                        mCheckedClassItems.add(currentClassItem);
                    }
                    else {
                        mCheckedClassItems.remove(currentClassItem);
                    }
                }
            });
        }

        return view;
    }

    /**
     * Get the list of checked classes
     * @return The checked classes
     */
    public List<ClassItem> getCheckedClasses(){
        return mCheckedClassItems;
    }
}