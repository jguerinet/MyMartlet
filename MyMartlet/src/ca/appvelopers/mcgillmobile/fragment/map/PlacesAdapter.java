package ca.appvelopers.mcgillmobile.fragment.map;

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
import ca.appvelopers.mcgillmobile.object.PlaceCategory;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-12 11:36 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class PlacesAdapter extends BaseAdapter {
    private Context mContext;
    private List<PlaceCategory> mCategories;

    public PlacesAdapter(Context context){
        this.mContext = context;
        this.mCategories = new ArrayList<PlaceCategory>();
        mCategories.addAll(App.getPlaceCategories());

        //Sort them
        Collections.sort(mCategories, new Comparator<PlaceCategory>() {
            @Override
            public int compare(PlaceCategory category, PlaceCategory category2) {
                return category.toString().compareToIgnoreCase(category2.toString());
            }
        });

        //Add the favorites option
        mCategories.add(0, new PlaceCategory(true));

        //Add the All option
        mCategories.add(0, new PlaceCategory(false));
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public PlaceCategory getItem(int i) {
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
        PlaceCategory category = getItem(position);
        ((TextView)view).setText(category.toString());

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_dropdown, null);
        }

        //Set the category name
        PlaceCategory category = getItem(position);
        ((TextView)view).setText(category.toString());

        return view;
    }
}
