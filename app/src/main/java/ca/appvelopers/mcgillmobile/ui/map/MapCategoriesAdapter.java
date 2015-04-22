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

package ca.appvelopers.mcgillmobile.ui.map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.PlaceType;

public class MapCategoriesAdapter extends BaseAdapter {
    private Context mContext;
    private List<PlaceType> mCategories;

    public MapCategoriesAdapter(Context context){
        this.mContext = context;
        this.mCategories = new ArrayList<PlaceType>();
        mCategories.addAll(App.getPlaceTypes());

        //Sort them
        Collections.sort(mCategories, new Comparator<PlaceType>() {
            @Override
            public int compare(PlaceType category, PlaceType category2) {
                return category.toString().compareToIgnoreCase(category2.toString());
            }
        });

        //Add the favorites option
        mCategories.add(0, new PlaceType(true));

        //Add the All option
        mCategories.add(0, new PlaceType(false));
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public PlaceType getItem(int i) {
        return mCategories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_item, viewGroup, false);
        }

        //Set the category name
        PlaceType category = getItem(position);
        ((TextView)view).setText(category.toString());

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_dropdown, null);
        }

        //Set the category name
        PlaceType category = getItem(position);
        ((TextView)view).setText(category.toString());

        return view;
    }
}
