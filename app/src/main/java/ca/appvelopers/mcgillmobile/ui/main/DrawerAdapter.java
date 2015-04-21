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

package ca.appvelopers.mcgillmobile.ui.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.util.Load;

/**
 * Author: Shabbir
 * Date: 24/02/14, 11:46 PM
 */
public class DrawerAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<DrawerItem> mDrawerItems;

    public DrawerAdapter(Activity activity){
        this.mActivity = activity;
        this.mDrawerItems = Arrays.asList(DrawerItem.values());
    }

    @Override
    public int getCount() {
        //Add 1 for the header
        return mDrawerItems.size() + 1;
    }

    @Override
    public DrawerItem getItem(int position) {
        return mDrawerItems.get(position - 1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public int getItemViewType(int position){
        if(position == 0){
            return 0;
        }
        return 1;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        int itemViewType = getItemViewType(position);

        if(view == null){
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            if(itemViewType == 0){
                view = inflater.inflate(R.layout.item_drawer_header, null);
            }
            else{
                view = inflater.inflate(R.layout.item_drawer, null);
            }
        }

        //The header
        if(itemViewType == 0){
            //Set the user's name and email in the drawer
            TextView name = (TextView)view.findViewById(R.id.drawer_name);
            name.setText(App.getUserInfo().getName());

            TextView email = (TextView)view.findViewById(R.id.drawer_email);
            email.setText(Load.loadFullUsername(mActivity));

            //Not clickable
            view.setEnabled(false);
            view.setOnClickListener(null);
        }
        //A list object
        else {
            //Get the current object
            DrawerItem currentItem = getItem(position);

            //Set the info up
            TextView icon = (TextView)view.findViewById(R.id.drawerItem_icon);
            icon.setTypeface(App.getIconFont());
            icon.setText(currentItem.getIcon(mActivity));

            TextView title = (TextView)view.findViewById(R.id.drawerItem_title);
            title.setText(currentItem.getTitle(mActivity));
        }

        return view;
    }

    public List<DrawerItem> getItems(){
        return this.mDrawerItems;
    }
}
