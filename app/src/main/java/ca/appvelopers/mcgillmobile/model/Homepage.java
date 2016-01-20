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
     * Returns the class to open based on the chosen homepage
     *
     * @param homepage Homepage
     * @return Class to open
     */
    public static Class getActivity(@Type int homepage) {
        switch (homepage) {
            case SCHEDULE:
                return ScheduleActivity.class;
            case TRANSCRIPT:
                return TranscriptActivity.class;
            case MY_COURSES:
                return MyCoursesActivity.class;
            case COURSES:
                return CoursesActivity.class;
            case WISHLIST:
                return WishlistActivity.class;
            case SEARCH_COURSES:
                return SearchActivity.class;
            case EBILL:
                return EbillActivity.class;
            case MAP:
                return MapActivity.class;
            case DESKTOP:
                return DesktopActivity.class;
            case SETTINGS:
                return SettingsActivity.class;
            //Facebook, Twitter, Logout
            default:
                return null;
        }
    }

    /**
     * Converts the menu item Id to a homepage
     *
     * @param menuId Clicked menu item Id
     * @return Homepage equivalent, {@link #UNDEFINED} if none
     */
    public static @Homepage.Type int getHomepage(@IdRes int menuId) {
        switch (menuId) {
            case R.id.schedule:
                return SCHEDULE;
            case R.id.transcript:
                return TRANSCRIPT;
            case R.id.my_courses:
                return MY_COURSES;
            case R.id.courses:
                return COURSES;
            case R.id.wishlist:
                return WISHLIST;
            case R.id.search:
                return SEARCH_COURSES;
            case R.id.ebill:
                return EBILL;
            case R.id.map:
                return MAP;
            case R.id.desktop:
                return DESKTOP;
            case R.id.settings:
                return SETTINGS;
            //Facebook, Twitter, Logout
            default:
                return UNDEFINED;
        }
    }

    /**
     * Returns the menu Id for the given homepage
     *
     * @param homepage Homepage
     * @return Corresponding Menu item Id
     */
    public static @IdRes int getMenuId(@Homepage.Type int homepage) {
        switch (homepage) {
            case SCHEDULE:
                return R.id.schedule;
            case TRANSCRIPT:
                return R.id.transcript;
            case MY_COURSES:
                return R.id.my_courses;
            case COURSES:
                return R.id.courses;
            case WISHLIST:
                return R.id.wishlist;
            case SEARCH_COURSES:
                return R.id.search;
            case EBILL:
                return R.id.ebill;
            case MAP:
                return R.id.map;
            case DESKTOP:
                return R.id.desktop;
            case SETTINGS:
                return R.id.settings;
            //Facebook, Twitter, Logout
            default:
                return UNDEFINED;
        }
    }
}
