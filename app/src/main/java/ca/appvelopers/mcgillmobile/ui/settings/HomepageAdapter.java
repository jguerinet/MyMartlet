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

package ca.appvelopers.mcgillmobile.ui.settings;

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

/**
 * Displays the different homepages the user can have
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class HomepageAdapter extends BaseAdapter {
    /**
     * The list of homepage items
     */
    private List<DrawerItem> mHomepageItems;

    /**
     * Default Constructor
     */
    public HomepageAdapter(){
        this.mHomepageItems = DrawerItem.getHomePages();

        //Sort them
        Collections.sort(mHomepageItems, new Comparator<DrawerItem>() {
            @Override
            public int compare(DrawerItem drawerItem, DrawerItem drawerItem2) {
                return drawerItem.toString().compareToIgnoreCase(drawerItem2.toString());
            }
        });
    }

    @Override
    public int getCount() {
        return mHomepageItems.size();
    }

    @Override
    public DrawerItem getItem(int i) {
        return mHomepageItems.get(i);
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

        ((TextView)view).setText(getItem(position).toString());

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.spinner_dropdown, viewGroup, false);
        }

        ((TextView)view).setText(getItem(position).toString());

        return view;
    }

    /**
     * Gets the position of the given drawer item
     *
     * @param drawerItem The drawer item
     * @return Its position
     */
    public int getPosition(DrawerItem drawerItem){
        return mHomepageItems.indexOf(drawerItem);
    }
}
