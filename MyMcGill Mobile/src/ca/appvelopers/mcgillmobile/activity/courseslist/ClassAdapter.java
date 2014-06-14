package ca.appvelopers.mcgillmobile.activity.courseslist;

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

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.object.Term;

/**
 * Author : Julien
 * Date :  2014-05-26 7:14 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class ClassAdapter extends BaseAdapter {
    private Context mContext;
    private List<ClassItem> mClassItems;
    private List<ClassItem> mCheckedClassItems;

    public ClassAdapter(Context context, Term term, List<ClassItem> classItems){
        this.mContext = context;
        this.mClassItems = new ArrayList<ClassItem>();
        this.mCheckedClassItems = new ArrayList<ClassItem>();

        //Add only the courses for this term
        for(ClassItem classItem : classItems){
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
        final ClassItem classItem = getItem(i);

        //Code
        TextView courseCode = (TextView)view.findViewById(R.id.course_code);
        courseCode.setText(classItem.getCourseCode());

        //Credits
        TextView courseCredits = (TextView)view.findViewById(R.id.course_credits);
        courseCredits.setText(mContext.getString(R.string.course_credits, classItem.getCredits()));

        //Title
        TextView courseTitle = (TextView)view.findViewById(R.id.course_title);
        courseTitle.setText(classItem.getCourseTitle());

        //Spots Remaining
        TextView spots = (TextView)view.findViewById(R.id.course_spots);
        spots.setVisibility(View.VISIBLE);
        spots.setText(classItem.getSeatsRemaining() + "Seat(s) Remaining");

        //Type
        TextView courseType = (TextView)view.findViewById(R.id.course_type);
        courseType.setText(classItem.getSectionType());

        //Waitlist Remaining
        TextView waitlistRemaining = (TextView)view.findViewById(R.id.course_waitlist);
        waitlistRemaining.setVisibility(View.VISIBLE);
        waitlistRemaining.setText("Waitlist Spots : " + classItem.getWaitlistRemaining());

        //Days
        TextView courseDays = (TextView)view.findViewById(R.id.course_days);
        courseDays.setText(Day.getDayStrings(classItem.getDays()));

        //Hours
        TextView courseHours = (TextView)view.findViewById(R.id.course_hours);
        courseHours.setText(classItem.getTimeString(mContext));

        //Set up the checkbox
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.course_checkbox);
        checkBox.setVisibility(View.VISIBLE);
        //Remove any other listeners
        checkBox.setOnCheckedChangeListener(null);
        //Initially unchecked
        checkBox.setChecked(mCheckedClassItems.contains(classItem));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //If it becomes checked, add it to the list. If not, remove it
                if(b){
                    mCheckedClassItems.add(classItem);
                }
                else {
                    mCheckedClassItems.remove(classItem);
                }
            }
        });

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
