/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.util.manager

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.IdRes
import android.support.annotation.StringRes

import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.MapActivity
import com.guerinet.mymartlet.ui.ScheduleActivity
import com.guerinet.mymartlet.ui.courses.CoursesActivity
import com.guerinet.mymartlet.ui.ebill.EbillActivity
import com.guerinet.mymartlet.ui.search.SearchActivity
import com.guerinet.mymartlet.ui.settings.SettingsActivity
import com.guerinet.mymartlet.ui.transcript.TranscriptActivity
import com.guerinet.mymartlet.ui.web.DesktopActivity
import com.guerinet.mymartlet.ui.web.MyCoursesActivity
import com.guerinet.mymartlet.ui.wishlist.WishlistActivity
import com.guerinet.mymartlet.util.Constants.COURSES
import com.guerinet.suitcase.prefs.IntPref

/**
 * Manages the user's homepage, an extension of the [IntPref]
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param prefs     [SharedPreferences] instance
 * @param context   App context
 */
class HomepageManager(prefs: SharedPreferences, private val context: Context) : IntPref(prefs,
        "home_page", HomePage.SCHEDULE.ordinal) {

    /**
     * Title String for the settings and/or the walkthrough
     */
    val titleString: String
        get() = context.getString(R.string.settings_homepage, string)

    /**
     * Class to open for the current homepage
     */
    val activity: Class<*>
        get() = getActivity(get())

    /**
     * Title of the current homepage
     */
    val string: String
        get() = getString(get())

    val activity: Activity
        get() =
            when (homepage) {
                SCHEDULE -> return ScheduleActivity::class.java
                TRANSCRIPT -> return TranscriptActivity::class.java
                MY_COURSES -> return MyCoursesActivity::class.java
                COURSES -> return CoursesActivity::class.java
                WISHLIST -> return WishlistActivity::class.java
                SEARCH_COURSES -> return SearchActivity::class.java
                EBILL -> return EbillActivity::class.java
                MAP -> return MapActivity::class.java
                DESKTOP -> return DesktopActivity::class.java
                HomePage.SETTINGS -> return SettingsActivity::class.java
                else -> throw IllegalStateException("Unknown homepage")
            }
}

/**
 * Returns the title of the given [homePage]
 */
fun getString(homePage: Int): String = context.getString(HomePage.values()[homePage].titleId)

/**
 * Converts the [menuId] to a home page
 */
fun getHomePage(@IdRes menuId: Int): Int =
        HomePage.values().first { it.menuId == menuId }.ordinal

/**
 * Converts the given [homePage] into a menu item Id
 */
@IdRes
fun getMenuId(homePage: Int) = HomePage.values()[homePage].menuId

enum class HomePage(@StringRes val titleId: Int, @IdRes val menuId: Int, activity: Activity) {
    SCHEDULE(R.string.homepage_schedule, R.id.schedule),
    TRANSCRIPT(R.string.homepage_transcript, R.id.transcript),
    MY_COURSES(R.string.homepage_mycourses, R.id.my_courses),
    COURSES(R.string.homepage_courses, R.id.courses),
    WISHLIST(R.string.homepage_wishlist, R.id.wishlist),
    SEARCH_COURSES(R.string.homepage_search, R.id.search),
    EBILL(R.string.homepage_ebill, R.id.ebill),
    MAP(R.string.homepage_map, R.id.map),
    DESKTOP(R.string.homepage_desktop, R.id.desktop),
    SETTINGS(R.string.title_settings, R.id.settings)
}
}
