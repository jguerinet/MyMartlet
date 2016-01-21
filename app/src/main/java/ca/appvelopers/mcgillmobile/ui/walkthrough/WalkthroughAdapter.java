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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.formgenerator.TextViewFormItem;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.dialog.list.FacultyListAdapter;
import ca.appvelopers.mcgillmobile.ui.dialog.list.HomepageListAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;

/**
 * Initial walkthrough
 * @author Julien Guerinet
 * @version 2.1.0
 */
public class WalkthroughAdapter extends PagerAdapter {
    /**
     * True if this is the first open, false otherwise
     *  For a first open there is an extra page at the end
     */
    private boolean mFirstOpen;

    public WalkthroughAdapter(boolean firstOpen) {
        super();
        mFirstOpen = firstOpen;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        final Context context = collection.getContext();
        View view;
        switch(position) {
            //Welcome
            case 0:
                view = View.inflate(context, R.layout.item_walkthrough_0, null);
                break;
            //Access Essentials
            case 1:
                view = View.inflate(context, R.layout.item_walkthrough_1, null);
                break;
            //Main Menu Explanation
            case 2:
                view = View.inflate(context, R.layout.item_walkthrough_2, null);
                break;
            //Horizontal Schedule
            case 3:
                view = View.inflate(context, R.layout.item_walkthrough_3, null);
                break;
            //Info
            case 4:
                view = View.inflate(context, R.layout.item_walkthrough_4, null);
                break;
            //Default Homepage / Faculty (first open only)
            case 5:
                view = View.inflate(context, R.layout.item_walkthrough_5, null);

                FormGenerator fg = FormGenerator.bind(context, (LinearLayout) view);

                //Homepage Prompt
                fg.text(R.string.walkthrough_homepage)
                        .gravity(Gravity.CENTER)
                        .padding(R.dimen.padding_small);

                //Homepage
                final TextViewFormItem homepageView =
                        fg.text(Homepage.getTitleString(App.getHomepage()));
                homepageView
                        .leftIcon(R.drawable.ic_phone_android)
                        .rightIcon(R.drawable.ic_chevron_right, R.color.grey)
                        .onClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogHelper.list(context, R.string.settings_homepage_title,
                                        new HomepageListAdapter() {
                                            @Override
                                            public void onHomepageSelected(
                                                    @Homepage.Type int homepage) {
                                                homepageView.view().setText(
                                                        Homepage.getTitleString(homepage));

                                                Analytics.get().sendEvent("Walkthrough", "Homepage",
                                                        Homepage.getString(homepage));

                                                //Update it in the App
                                                App.setHomepage(homepage);
                                            }
                                        });
                            }
                        });

                //Faculty Prompt
                fg.text(R.string.walkthrough_faculty)
                        .gravity(Gravity.CENTER)
                        .padding(R.dimen.padding_small);

                //Faculty
                final TextViewFormItem facultyView = fg.text(R.string.faculty_none);
                facultyView
                        .leftIcon(R.drawable.ic_mycourses)
                        .rightIcon(R.drawable.ic_chevron_right, R.color.grey)
                        .onClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogHelper.list(context, R.string.faculty_title,
                                        new FacultyListAdapter(facultyView.view()
                                                .getText().toString()) {
                                            @Override
                                            public void onFacultySelected(String faculty) {
                                                //Update the view
                                                facultyView.view().setText(faculty);

                                                //If the faculty is not empty, send the GA
                                                Analytics.get().sendEvent("Walkthrough", "Faculty",
                                                        faculty);
                                            }
                                        });
                            }
                        });

                break;
            default:
                throw new IllegalStateException(
                        String.format("Unknown position %d in walkthrough", position));
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
        return mFirstOpen ? 6 : 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}