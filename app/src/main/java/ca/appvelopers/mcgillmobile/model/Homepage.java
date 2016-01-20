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
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
 * List of possible homepages
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Homepage {
    /**
     * The different homepages
     */
    @Retention(RetentionPolicy.CLASS)
    @IntDef({UNDEFINED, SCHEDULE, TRANSCRIPT, MY_COURSES, COURSES, WISHLIST, SEARCH_COURSES, EBILL,
            MAP, DESKTOP, SETTINGS})
    public @interface Type{}
    public static final int UNDEFINED = -1;
    public static final int SCHEDULE = 0;
    public static final int TRANSCRIPT = 1;
    public static final int MY_COURSES = 2;
    public static final int COURSES = 3;
    public static final int WISHLIST = 4;
    public static final int SEARCH_COURSES = 5;
    public static final int EBILL = 6;
    public static final int MAP = 7;
    public static final int DESKTOP = 8;
    public static final int SETTINGS = 9;

    /**
     * @param homepage Homepage
     * @return The title of the homepage
     */
    public static String getString(@Type int homepage) {
        int stringId;
        switch(homepage) {
            case SCHEDULE:
                stringId = R.string.homepage_schedule;
                break;
            case TRANSCRIPT:
                stringId = R.string.homepage_transcript;
                break;
            case MY_COURSES:
                stringId = R.string.homepage_mycourses;
                break;
            case COURSES:
                stringId = R.string.homepage_courses;
                break;
            case WISHLIST:
                stringId = R.string.homepage_wishlist;
                break;
            case SEARCH_COURSES:
                stringId = R.string.homepage_search;
                break;
            case EBILL:
                stringId = R.string.homepage_ebill;
                break;
            case MAP:
                stringId = R.string.homepage_map;
                break;
            case DESKTOP:
                stringId = R.string.homepage_desktop;
                break;
            case SETTINGS:
                stringId = R.string.title_settings;
                break;
            default:
                throw new IllegalArgumentException("Unknown homepage: " + homepage);
        }
        return App.getContext().getString(stringId);
    }

    /**
     * @param homepage Homepage
     * @return The title String for the settings and/or the walkthrough
     */
    public static String getTitleString(@Type int homepage) {
        return App.getContext().getString(R.string.settings_homepage, getString(homepage));
    }

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
