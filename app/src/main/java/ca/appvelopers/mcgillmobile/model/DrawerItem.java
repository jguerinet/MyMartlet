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

package ca.appvelopers.mcgillmobile.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;

public enum DrawerItem {
    SCHEDULE,
    TRANSCRIPT,
    MY_COURSES,
    COURSES,
    WISHLIST,
    SEARCH_COURSES,
    EBILL,
    MAP,
    DESKTOP,
    SETTINGS,
    FACEBOOK,
    TWITTER,
    LOGOUT;

    /**
     * Returns a list of possible homepages
     * @return The list of possible homepages
     */
    public static List<DrawerItem> getHomePages(){
        List<DrawerItem> homePages = new ArrayList<DrawerItem>();
        homePages.add(SCHEDULE);
        homePages.add(TRANSCRIPT);
        homePages.add(MY_COURSES);
        homePages.add(COURSES);
        homePages.add(WISHLIST);
        homePages.add(SEARCH_COURSES);
        homePages.add(EBILL);
        homePages.add(MAP);
        homePages.add(DESKTOP);

        return homePages;
    }

    public String toString(Context context){
        switch(this){
            case SCHEDULE:
                return context.getString(R.string.homepage_schedule);
            case TRANSCRIPT:
                return context.getString(R.string.homepage_transcript);
            case MY_COURSES:
                return context.getString(R.string.homepage_mycourses);
            case COURSES:
                return context.getString(R.string.homepage_courses);
            case WISHLIST:
                return context.getString(R.string.homepage_wishlist);
            case SEARCH_COURSES:
                return context.getString(R.string.homepage_search);
            case EBILL:
                return context.getString(R.string.homepage_ebill);
            case MAP:
                return context.getString(R.string.homepage_map);
            case DESKTOP:
                return context.getString(R.string.homepage_desktop);
            default:
                return "";
        }
    }

    /**
     * Returns the title for the drawer
     * @return The page title
     */
    public String getTitle(Context context){
        switch(this){
            case SCHEDULE:
                return context.getString(R.string.title_schedule);
            case TRANSCRIPT:
                return context.getString(R.string.title_transcript);
            case MY_COURSES:
                return context.getString(R.string.title_mycourses);
            case COURSES:
                return context.getString(R.string.title_courses);
            case WISHLIST:
                return context.getString(R.string.title_wishlist);
            case SEARCH_COURSES:
                return context.getString(R.string.title_registration);
            case EBILL:
                return context.getString(R.string.title_ebill);
            case MAP:
                return context.getString(R.string.title_map);
            case DESKTOP:
                return context.getString(R.string.title_desktop);
            case SETTINGS:
                return context.getString(R.string.title_settings);
            case FACEBOOK:
                return context.getString(R.string.title_facebook);
            case TWITTER:
                return context.getString(R.string.title_twitter);
            case LOGOUT:
                return context.getString(R.string.title_logout);
            default:
                return "";
        }
    }

    /**
     * Returns the icon for the drawer
     * @return The page icon
     */
    public String getIcon(Context context){
        switch(this){
            case SCHEDULE:
                return context.getString(R.string.icon_schedule);
            case TRANSCRIPT:
                return context.getString(R.string.icon_transcript);
            case MY_COURSES:
                return context.getString(R.string.icon_mycourses);
            case COURSES:
                return context.getString(R.string.icon_courses);
            case WISHLIST:
                return context.getString(R.string.icon_star);
            case SEARCH_COURSES:
                return context.getString(R.string.icon_search);
            case EBILL:
                return context.getString(R.string.icon_ebill);
            case MAP:
                return context.getString(R.string.icon_map);
            case DESKTOP:
                return context.getString(R.string.icon_desktop);
            case SETTINGS:
                return context.getString(R.string.icon_settings);
            case FACEBOOK:
                return context.getString(R.string.icon_facebook);
            case TWITTER:
                return context.getString(R.string.icon_twitter);
            case LOGOUT:
                return context.getString(R.string.icon_logout);
            default:
                return "";
        }
    }
}
