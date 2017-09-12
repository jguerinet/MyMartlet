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

package com.guerinet.mymartlet.util.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.ui.MapActivity;
import com.guerinet.mymartlet.ui.ScheduleActivity;
import com.guerinet.mymartlet.ui.courses.CoursesActivity;
import com.guerinet.mymartlet.ui.ebill.EbillActivity;
import com.guerinet.mymartlet.ui.search.SearchActivity;
import com.guerinet.mymartlet.ui.settings.SettingsActivity;
import com.guerinet.mymartlet.ui.transcript.TranscriptActivity;
import com.guerinet.mymartlet.ui.web.DesktopActivity;
import com.guerinet.mymartlet.ui.web.MyCoursesActivity;
import com.guerinet.mymartlet.ui.wishlist.WishlistActivity;
import com.guerinet.suitcase.prefs.IntPref;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

/**
 * Manages the user's homepage, an extension of the {@link IntPref}
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class HomepageManager extends IntPref {
    /**
     * The different homepages
     */
    @Retention(RetentionPolicy.CLASS)
    @IntDef({SCHEDULE, TRANSCRIPT, MY_COURSES, COURSES, WISHLIST, SEARCH_COURSES, EBILL, MAP,
            DESKTOP, SETTINGS})
    public @interface Homepage {}

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
     * App context
     */
    private Context context;

    /**
     * Default Constructor
     *
     * @param prefs        {@link SharedPreferences} instance
     * @param context      App context
     */
    @Inject
    public HomepageManager(SharedPreferences prefs, Context context) {
        super(prefs, "home_page", SCHEDULE);
        this.context = context;
    }

    @Override
    @SuppressWarnings("ResourceType")
    public @Homepage int get() {
        return super.get();
    }

    @Override
    public void set(@Homepage int value) {
        super.set(value);
    }

    /**
     * @return The title String for the settings and/or the walkthrough
     */
    public String getTitleString() {
        return context.getString(R.string.settings_homepage, getString(get()));
    }

    /**
     * @return Class to open for the current homepage
     */
    public Class getActivity() {
        return getActivity(get());
    }

    /**
     * @return The title of the current homepage
     */
    public String getString() {
        return getString(get());
    }

    /**
     * Returns the class to open based on the chosen homepage
     *
     * @param homepage The homepage to find the class for
     * @return Class to open
     */
    public Class getActivity(@Homepage int homepage) {
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
            default:
                throw new IllegalStateException("Unknown homepage");
        }
    }

    /**
     * @param homepage The homepage
     * @return The title of the homepage
     */
    public String getString(@Homepage int homepage) {
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
        return context.getString(stringId);
    }

    /**
     * Converts the menu item Id to a homepage
     *
     * @param menuId Clicked menu item Id
     * @return Homepage equivalent
     */
    public @Homepage int getHomepage(@IdRes int menuId) {
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
            default:
                throw new IllegalStateException("Unknown homepage");
        }
    }

    /**
     * Returns the menu Id for the given homepageManager
     *
     * @param homepage HomepageManager
     * @return Corresponding Menu item Id
     */
    public @IdRes int getMenuId(@Homepage int homepage) {
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
            default:
                throw new IllegalStateException("Unknown homepage");
        }
    }
}
