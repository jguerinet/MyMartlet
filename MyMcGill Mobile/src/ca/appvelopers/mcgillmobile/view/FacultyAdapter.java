package ca.appvelopers.mcgillmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Faculty;

/**
 * Author : Julien
 * Date :  2014-06-10 8:13 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class FacultyAdapter extends BaseAdapter {
    private Context mContext;
    private List<Faculty> mFaculties;

    public FacultyAdapter(Context context, boolean emptyTerm){
        this.mContext = context;
        this.mFaculties = new ArrayList<Faculty>();
        mFaculties.addAll(Arrays.asList(Faculty.values()));

        //Sort them
        Collections.sort(mFaculties, new Comparator<Faculty>() {
            @Override
            public int compare(Faculty faculty, Faculty faculty2) {
                return faculty.toString(mContext).compareToIgnoreCase(faculty2.toString(mContext));
            }
        });

        //See if we need to add an empty term
        if(emptyTerm){
            mFaculties.add(0, null);
        }
    }

    @Override
    public int getCount() {
        return mFaculties.size();
    }

    @Override
    public Faculty getItem(int i) {
        return mFaculties.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item, null);
        }

        Faculty faculty = getItem(position);
        if(faculty != null){
            ((TextView)view).setText(faculty.toString(mContext));
        }
        else{
            ((TextView)view).setText(" ");
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_dropdown, null);
        }

        Faculty faculty = getItem(position);
        if(faculty != null){
            ((TextView)view).setText(faculty.toString(mContext));
        }
        else{
            ((TextView)view).setText(" ");
        }

        return view;
    }
}
