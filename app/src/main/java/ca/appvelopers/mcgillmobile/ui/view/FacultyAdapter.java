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

package ca.appvelopers.mcgillmobile.ui.view;

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

public class FacultyAdapter extends BaseAdapter {
    private Context mContext;
    private List<Faculty> mFaculties;

    public FacultyAdapter(Context context, boolean emptyFaculty){
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

        //See if we need to add an empty faculty
        if(emptyFaculty){
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
