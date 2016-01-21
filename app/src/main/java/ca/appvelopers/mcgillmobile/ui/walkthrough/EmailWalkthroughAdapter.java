/*
 * Copyright 2014-2016 Appvelopers
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
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import ca.appvelopers.mcgillmobile.R;

/**
 * Walkthrough for setting up email on your Android device
 * @author Julien Guerinet
 * @version 2.1.0
 */
public class EmailWalkthroughAdapter extends PagerAdapter {

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        final Context context = collection.getContext();
        View view;
        switch(position) {
            //Welcome
            case 0:
                view = View.inflate(context, R.layout.item_walkthrough_email_0, null);
                break;
            //Access Essentials
            case 1:
                view = View.inflate(context, R.layout.item_walkthrough_email_1, null);
                break;
            //Main Menu Explanation
            case 2:
                view = View.inflate(context, R.layout.item_walkthrough_email_2, null);
                break;
            //Horizontal Schedule
            case 3:
                view = View.inflate(context, R.layout.item_walkthrough_email_3, null);
                break;
            //Offline Access / Security
            case 4:
                view = View.inflate(context, R.layout.item_walkthrough_email_4, null);
                break;
            //Help/About/Bugs
            case 5:
                view = View.inflate(context, R.layout.item_walkthrough_email_5, null);
                break;
            //Default Homepage / Faculty
            case 6:
                view = View.inflate(context, R.layout.item_walkthrough_email_6, null);
                break;
            default:
                throw new IllegalStateException(
                        String.format("Unknown position %d in email walkthrough", position));
        }

        collection.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}