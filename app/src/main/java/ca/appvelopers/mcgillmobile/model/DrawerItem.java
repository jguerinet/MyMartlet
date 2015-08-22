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

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * The items in the main navigation drawer
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public enum DrawerItem {
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
    SETTINGS,
    /**
     * Share on Facebook
     */
    FACEBOOK,
    /**
     * Share on Twitter
     */
    TWITTER,
    /**
     * Log out of the app
     */
    LOGOUT;

    /**
     * @return The list of possible homepages
     */
    public static List<DrawerItem> getHomePages(){
        List<DrawerItem> homePages = new ArrayList<>();
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

    /**
     * @return The page icon for the navigation drawer
     */
    public String getIcon(){
        switch(this){
            case SCHEDULE:
                return App.getContext().getString(R.string.icon_schedule);
            case TRANSCRIPT:
                return App.getContext().getString(R.string.icon_transcript);
            case MY_COURSES:
                return App.getContext().getString(R.string.icon_mycourses);
            case COURSES:
                return App.getContext().getString(R.string.icon_courses);
            case WISHLIST:
                return App.getContext().getString(R.string.icon_star);
            case SEARCH_COURSES:
                return App.getContext().getString(R.string.icon_search);
            case EBILL:
                return App.getContext().getString(R.string.icon_ebill);
            case MAP:
                return App.getContext().getString(R.string.icon_map);
            case DESKTOP:
                return App.getContext().getString(R.string.icon_desktop);
            case SETTINGS:
                return App.getContext().getString(R.string.icon_settings);
            case FACEBOOK:
                return App.getContext().getString(R.string.icon_facebook);
            case TWITTER:
                return App.getContext().getString(R.string.icon_twitter);
            case LOGOUT:
                return App.getContext().getString(R.string.icon_logout);
            default:
                return "";
        }
    }

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
            case FACEBOOK:
                return App.getContext().getString(R.string.title_facebook);
            case TWITTER:
                return App.getContext().getString(R.string.title_twitter);
            case LOGOUT:
                return App.getContext().getString(R.string.title_logout);
            default:
                return "";
        }
    }
}
