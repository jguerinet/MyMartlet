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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.util.storage.Load;

/**
 * The adapter for the main navigation drawer
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class DrawerAdapter extends BaseAdapter {
    /**
     * List of drawer items
     */
    private List<DrawerItem> mDrawerItems;

    /**
     * Default Constructor
     */
    public DrawerAdapter(){
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

        Object holder;
        if(view == null){
            //If the view is not inflated yet, inflate the right one and set the holder as the tag
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            if(itemViewType == 0){
                view = inflater.inflate(R.layout.item_drawer_header, viewGroup, false);
                holder = new HeaderHolder(view);
            }
            else{
                view = inflater.inflate(R.layout.item_drawer, viewGroup, false);
                holder = new DrawerHolder(view);
            }
            view.setTag(holder);
        }
        else{
            //Get the holder from the view's tag
            holder = view.getTag();
        }

        //The header
        if(itemViewType == 0){
            ((HeaderHolder)holder).name.setText(App.getUser().getName());
            ((HeaderHolder)holder).email.setText(Load.fullUsername());

            //Not clickable
            view.setEnabled(false);
            view.setOnClickListener(null);
        }
        //A list object
        else {
            //Get the current object
            DrawerItem item = getItem(position);

            ((DrawerHolder)holder).icon.setText(item.getIcon());
            ((DrawerHolder)holder).title.setText(item.toString());
        }

        return view;
    }

    /**
     * @return The list of drawer items
     */
    public List<DrawerItem> getItems(){
        return this.mDrawerItems;
    }

    class HeaderHolder {
        /**
         * The user's name
         */
        @Bind(R.id.drawer_name)
        public TextView name;
        /**
         * The user's email
         */
        @Bind(R.id.drawer_email)
        public TextView email;

        /**
         * Default Constructor
         *
         * @param view The base view
         */
        public HeaderHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    class DrawerHolder {
        /**
         * The drawer item icon
         */
        @Bind(R.id.drawer_icon)
        public TextView icon;
        /**
         * The drawer item title
         */
        @Bind(R.id.drawer_title)
        public TextView title;

        /**
         * Default Constructor
         *
         * @param view The base view
         */
        public DrawerHolder(View view){
            ButterKnife.bind(this, view);
            icon.setTypeface(App.getIconFont());
        }
    }
}
