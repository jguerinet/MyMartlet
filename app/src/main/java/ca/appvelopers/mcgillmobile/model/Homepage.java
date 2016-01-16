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

package ca.appvelopers.mcgillmobile.model;

import android.support.annotation.IdRes;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.courses.CoursesActivity;
import ca.appvelopers.mcgillmobile.ui.ebill.EbillActivity;
import ca.appvelopers.mcgillmobile.ui.map.MapActivity;
import ca.appvelopers.mcgillmobile.ui.schedule.ScheduleActivity;
import ca.appvelopers.mcgillmobile.ui.search.SearchActivity;
import ca.appvelopers.mcgillmobile.ui.settings.SettingsActivity;
import ca.appvelopers.mcgillmobile.ui.transcript.TranscriptActivity;
import ca.appvelopers.mcgillmobile.ui.web.DesktopActivity;
import ca.appvelopers.mcgillmobile.ui.web.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistActivity;

/**
 * The list of possible homepages
 * @author Julien Guerinet
 * @since 1.0.0
 */
public enum Homepage {
    /**
     * The user's schedule
     */
    SCHEDULE,
    /**
     * The user's transcript
     */
    TRANSCRIPT,
    /**
     * The user's MyCourses page
     */
    MY_COURSES,
    /**
     * The user's list of courses
     */
    COURSES,
    /**
     * The user's wishlist
     */
    WISHLIST,
    /**
     * Search for courses
     */
    SEARCH_COURSES,
    /**
     * The user's ebill
     */
    EBILL,
    /**
     * Campus Map
     */
    MAP,
    /**
     * The user's MyMcGill
     */
    DESKTOP,
    /**
     * App Settings
     */
    SETTINGS;

    @Override
    public String toString(){
        switch(this){
            case SCHEDULE:
                return App.getContext().getString(R.string.homepage_schedule);
            case TRANSCRIPT:
                return App.getContext().getString(R.string.homepage_transcript);
            case MY_COURSES:
                return App.getContext().getString(R.string.homepage_mycourses);
            case COURSES:
                return App.getContext().getString(R.string.homepage_courses);
            case WISHLIST:
                return App.getContext().getString(R.string.homepage_wishlist);
            case SEARCH_COURSES:
                return App.getContext().getString(R.string.homepage_search);
            case EBILL:
                return App.getContext().getString(R.string.homepage_ebill);
            case MAP:
                return App.getContext().getString(R.string.homepage_map);
            case DESKTOP:
                return App.getContext().getString(R.string.homepage_desktop);
            case SETTINGS:
                return App.getContext().getString(R.string.title_settings);
            default:
                throw new IllegalArgumentException("Unknown homepage: " + this);
        }
    }

    /* STATIC */

    /**
     * Returns the class to open based on the clicked menu Id
     *
     * @param menuId The clicked menu Id
     * @return Class to open
     */
    public static Class getActivity(@IdRes int menuId) {
        switch (menuId) {
            case R.id.schedule:
                return ScheduleActivity.class;
            case R.id.transcript:
                return TranscriptActivity.class;
            case R.id.my_courses:
                return MyCoursesActivity.class;
            case R.id.courses:
                return CoursesActivity.class;
            case R.id.wishlist:
                return WishlistActivity.class;
            case R.id.search:
                return SearchActivity.class;
            case R.id.ebill:
                return EbillActivity.class;
            case R.id.map:
                return MapActivity.class;
            case R.id.desktop:
                return DesktopActivity.class;
            case R.id.settings:
                return SettingsActivity.class;
            //Facebook, Twitter, Logout
            default:
                return null;
        }
    }
}
