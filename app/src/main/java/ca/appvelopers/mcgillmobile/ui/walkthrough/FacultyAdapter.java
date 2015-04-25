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

package ca.appvelopers.mcgillmobile.ui.walkthrough;

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
import ca.appvelopers.mcgillmobile.model.Faculty;

/**
 * Displays the list of faculties that the user can choose from
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class FacultyAdapter extends BaseAdapter {
    /**
     * The list of faculties
     */
    private List<Faculty> mFaculties;

    /**
     * Default Constructor
     */
    public FacultyAdapter(Context context){
        this.mFaculties = new ArrayList<>();
        mFaculties.addAll(Arrays.asList(Faculty.values()));

        //Sort them
        Collections.sort(mFaculties, new Comparator<Faculty>() {
            @Override
            public int compare(Faculty faculty, Faculty faculty2) {
                return faculty.toString().compareToIgnoreCase(faculty2.toString());
            }
        });

        //Add the empty faculty at the front
        mFaculties.add(0, null);
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
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.spinner_item, viewGroup, false);
        }

        Faculty faculty = getItem(position);
        if(faculty != null){
            ((TextView)view).setText(faculty.toString());
        }
        else{
            ((TextView)view).setText(" ");
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.spinner_dropdown, viewGroup, false);
        }

        Faculty faculty = getItem(position);
        if(faculty != null){
            ((TextView)view).setText(faculty.toString());
        }
        else{
            ((TextView)view).setText(" ");
        }

        return view;
    }
}
