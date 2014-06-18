package ca.appvelopers.mcgillmobile.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.HomePage;

/**
 * Author : Julien
 * Date :  2014-06-10 8:17 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class HomePageAdapter extends BaseAdapter {
    private Context mContext;
    private List<HomePage> mHomePages;

    public HomePageAdapter(Context context){
        this.mContext = context;
        this.mHomePages = Arrays.asList(HomePage.values());

        //Sort them
        Collections.sort(mHomePages, new Comparator<HomePage>() {
            @Override
            public int compare(HomePage homePage, HomePage homePage2) {
                return homePage.toString(mContext).compareToIgnoreCase(homePage2.toString(mContext));
            }
        });
    }

    @Override
    public int getCount() {
        return mHomePages.size();
    }

    @Override
    public HomePage getItem(int i) {
        return mHomePages.get(i);
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

        HomePage homePage = getItem(position);
        ((TextView)view).setText(homePage.toString(mContext));

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.spinner_dropdown, null);
        }

        HomePage homePage = getItem(position);
        ((TextView)view).setText(homePage.toString(mContext));

        return view;
    }
}
