/*
 * Copyright 2014-2017 Julien Guerinet
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

package com.guerinet.mymartlet.ui.walkthrough;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.ui.dialog.list.FacultiesAdapter;
import com.guerinet.mymartlet.ui.dialog.list.HomepagesAdapter;
import com.guerinet.mymartlet.util.Analytics;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.suitcase.dialog.DialogUtils;

import javax.inject.Inject;

/**
 * Initial walkthrough
 * @author Julien Guerinet
 * @version 2.1.0
 */
public class WalkthroughAdapter extends PagerAdapter {
    /**
     * App context
     */
    private final Context context;
    /**
     * True if this is the first open, false otherwise
     *  For a first open there is an extra page at the end
     */
    private final boolean firstOpen;
    /**
     * {@link Analytics} instance
     */
    @Inject
    Analytics analytics;
    /**
     * {@link HomepageManager} instance
     */
    @Inject
    HomepageManager homepageManager;

    /**
     * Default Constructor
     *
     * @param context   App context
     * @param firstOpen True if this is the first open, false otherwise
     */
    WalkthroughAdapter(Context context, boolean firstOpen) {
        super();
        this.context = context;
        this.firstOpen = firstOpen;
        App.component(context).inject(this);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        int layout = 0;
        switch (position) {
            // Welcome
            case 0:
                layout = R.layout.item_walkthrough_0;
                break;
            // Access Essentials
            case 1:
                layout = R.layout.item_walkthrough_1;
                break;
            // Main Menu Explanation
            case 2:
                layout = R.layout.item_walkthrough_2;
                break;
            // Horizontal Schedule
//            case 3:
//                layout = R.layout.item_walkthrough_3;
//                break;
            // Info
            case 3:
                layout = R.layout.item_walkthrough_4;
                break;
            // Default HomepageManager / Faculty (first open only)
            case 4:
                break;
            default:
                throw new IllegalStateException("Unknown position " + position + " in walkthrough");
        }

        View view;

        if (position == 4) {
            // Set up the question page
            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setGravity(Gravity.CENTER);

            FormGenerator fg = FormGenerator.bind(container);

            // HomepageManager Prompt
            fg.text()
                    .text(R.string.walkthrough_homepage)
                    .gravity(Gravity.CENTER)
                    .padding(context.getResources().getDimensionPixelOffset(R.dimen.padding_small))
                    .build();

            // HomepageManager
            fg.text()
                    .text(homepageManager.getTitleString())
                    .leftIcon(R.drawable.ic_phone_android)
                    .rightIcon(R.drawable.ic_chevron_right, Color.GRAY)
                    .onClick(item -> DialogUtils.singleList(context, R.string.settings_homepage_title,
                            new HomepagesAdapter(context) {
                                @Override
                                public void onHomepageSelected(@HomepageManager.Homepage
                                        int choice) {
                                    // Update it
                                    homepageManager.set(choice);

                                    item.view().setText(homepageManager.getTitleString());

                                    analytics.sendEvent("Walkthrough", "HomepageManager",
                                            homepageManager.getString());
                                }
                            }))
                    .build();

            // Faculty Prompt
            fg.text()
                    .text(R.string.walkthrough_faculty)
                    .gravity(Gravity.CENTER)
                    .padding(context.getResources().getDimensionPixelOffset(R.dimen.padding_small))
                    .build();

            // Faculty
            fg.text()
                    .text(R.string.faculty_none)
                    .leftIcon(R.drawable.ic_mycourses)
                    .rightIcon(R.drawable.ic_chevron_right, Color.GRAY)
                    .onClick(item -> DialogUtils.singleList(context, R.string.faculty_title,
                            new FacultiesAdapter(context, item.view().getText().toString()) {
                                @Override
                                public void onFacultySelected(String faculty) {
                                    // Update the view
                                    item.view().setText(faculty);
                                    analytics.sendEvent("Walkthrough", "Faculty", faculty);
                                }
                            }))
                    .build();
            view = container;
        } else {
            view = LayoutInflater.from(context).inflate(layout, null);
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
        return firstOpen ? 5 : 4;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}