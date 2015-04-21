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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.DrawerItem;

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
