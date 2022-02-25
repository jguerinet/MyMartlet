/*
 * Copyright 2014-2022 Julien Guerinet
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
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.StringRes
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
import com.guerinet.suitcase.settings.IntSetting
import com.russhwolf.settings.Settings
import kotlin.reflect.KClass

/**
 * Manages the user's homepage, an extension of the [IntPref]
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param settings [Settings] instance
 * @property context App context
 */
class HomepageManager(settings: Settings, private val context: Context) : IntSetting(
    settings,
    "home_page", HomePage.SCHEDULE.ordinal
) {

    var homePage: HomePage
        get() = HomePage.values()[value]
        set(value) {
            super.value = value.ordinal
        }

    /**
     * Title of the current homepage
     */
    val title: String
        get() = getTitle(homePage)

    /**
     * Title String for the settings and/or the walkthrough
     */
    val titleString: String
        get() = context.getString(R.string.settings_homepage, title)

    /**
     * Class to open for the current homepage
     */
    val activity: Class<*>
        get() = homePage.activity.java

    /**
     * Returns the title String for the [homePage]
     */
    fun getTitle(homePage: HomePage): String = context.getString(homePage.titleId)

    /**
     * Converts the [menuId] to a home page
     */
    fun getHomePage(@IdRes menuId: Int): HomePage = HomePage.values().first { it.menuId == menuId }

    /**
     * A potential home page for the app
     *
     * @property titleId Id of the title of the his home page
     * @property menuId Id of the menu item for this home page
     * @property activity Corresponding activity to open for this home page
     */
    enum class HomePage(
        @StringRes val titleId: Int,
        @IdRes val menuId: Int,
        val activity: KClass<out Activity>
    ) {

        SCHEDULE(R.string.homepage_schedule, R.id.schedule, ScheduleActivity::class),
        TRANSCRIPT(R.string.homepage_transcript, R.id.transcript, TranscriptActivity::class),
        MY_COURSES(R.string.homepage_mycourses, R.id.my_courses, MyCoursesActivity::class),
        COURSES(R.string.homepage_courses, R.id.courses, CoursesActivity::class),
        WISHLIST(R.string.homepage_wishlist, R.id.wishlist, WishlistActivity::class),
        SEARCH_COURSES(R.string.homepage_search, R.id.search, SearchActivity::class),
        EBILL(R.string.homepage_ebill, R.id.ebill, EbillActivity::class),
        MAP(R.string.homepage_map, R.id.map, MapActivity::class),
        DESKTOP(R.string.homepage_desktop, R.id.desktop, DesktopActivity::class),
        SETTINGS(R.string.title_settings, R.id.settings, SettingsActivity::class)
    }
}
