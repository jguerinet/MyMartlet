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

import java.util.Arrays;
import java.util.Comparator;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;

/**
 * Displays the different homepages the user can have
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class HomepageAdapter extends BaseAdapter {
    /**
     * The list of homepage items
     */
    private Homepage[] mItems;

    /**
     * Default Constructor
     */
    public HomepageAdapter(){
        this.mItems = Homepage.values();

        //Sort them
        Arrays.sort(mItems, new Comparator<Homepage>() {
            @Override
            public int compare(Homepage a, Homepage b) {
                return a.toString().compareToIgnoreCase(b.toString());
            }
        });
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public Homepage getItem(int i) {
        return mItems[i];
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
     * @param item The drawer item
     * @return Its position
     */
    public int getPosition(Homepage item){
        for(int i = 0; i < mItems.length; i++){
            if(mItems[i] == item){
                return i;
            }
        }

        throw new IllegalStateException("Homepage " + item + " not found");
    }
}
