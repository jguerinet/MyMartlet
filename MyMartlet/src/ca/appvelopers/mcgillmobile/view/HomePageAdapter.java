package ca.appvelopers.mcgillmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.DrawerItem;

/**
 * Author : Julien
 * Date :  2014-06-10 8:17 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class HomePageAdapter extends BaseAdapter {
    private Context mContext;
    private List<DrawerItem> mHomePageItems;

    public HomePageAdapter(Context context){
        this.mContext = context;
        this.mHomePageItems = DrawerItem.getHomePages();

        //Sort them
        Collections.sort(mHomePageItems, new Comparator<DrawerItem>() {
            @Override
            public int compare(DrawerItem drawerItem, DrawerItem drawerItem2) {
                return drawerItem.toString(mContext).compareToIgnoreCase(drawerItem2.toString(mContext));
            }
        });
    }

    @Override
    public int getCount() {
        return mHomePageItems.size();
    }

    @Override
    public DrawerItem getItem(int i) {
        return mHomePageItems.get(i);
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

        DrawerItem drawerItem = getItem(position);
        ((TextView)view).setText(drawerItem.toString(mContext));

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_dropdown, null);
        }

        DrawerItem drawerItem = getItem(position);
        ((TextView)view).setText(drawerItem.toString(mContext));

        return view;
    }

    public int getPosition(DrawerItem drawerItem){
        return mHomePageItems.indexOf(drawerItem);
    }
}
