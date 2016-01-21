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
import android.support.annotation.DrawableRes;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
        View view = View.inflate(collection.getContext(), R.layout.item_walkthrough_email, null);

        //Bind the ImageView and the TextView
        TextView message = (TextView) view.findViewById(R.id.message);

        @DrawableRes int imageId;
        switch(position) {
            case 0:
                message.setText(R.string.help_email_walk0);
                imageId = R.drawable.email0;
                break;
            case 1:
                message.setText(R.string.help_email_walk1);
                imageId = R.drawable.email1;
                break;
            case 2:
                message.setText(R.string.help_email_walk2);
                imageId = R.drawable.email2;
                break;
            case 3:
                message.setText(R.string.help_email_walk3);
                imageId = R.drawable.email3;
                break;
            case 4:
                message.setText(R.string.help_email_walk4);
                imageId = R.drawable.email4;
                break;
            case 5:
                message.setText(R.string.help_email_walk5);
                imageId = R.drawable.email5;
                break;
            default:
                throw new IllegalStateException(
                        String.format("Unknown position %d in email walkthrough", position));
        }

        //Load the image
        Picasso.with(context)
                .load(imageId)
                .into((ImageView) view.findViewById(R.id.image));

        collection.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}