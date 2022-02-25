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

package com.guerinet.mymartlet.ui.walkthrough

import android.content.Context
import android.graphics.Color
import android.util.Pair
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.google.firebase.analytics.FirebaseAnalytics
import com.guerinet.morf.Morf
import com.guerinet.morf.TextViewItem
import com.guerinet.morf.util.Position
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.suitcase.analytics.event
import com.guerinet.suitcase.dialog.singleListDialog
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Initial walkthrough
 * @author Julien Guerinet
 * @version 2.1.0
 *
 * @property isFirstOpen True if this is the first open, false otherwise
 *                      For a first open there is an extra page at the end
 */
class WalkthroughAdapter(private val isFirstOpen: Boolean) : PagerAdapter(), KoinComponent {

    private val homePageManager by inject<HomepageManager>()

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val layout = when (position) {
            // Welcome
            0 -> R.layout.item_walkthrough_0
            // Access Essentials
            1 -> R.layout.item_walkthrough_1
            // Main Menu Explanation
            2 -> R.layout.item_walkthrough_2
            // Horizontal Schedule
//            3 -> R.layout.item_walkthrough_3
            // Info
            3 -> R.layout.item_walkthrough_4
            // Default HomepageManager / Faculty (first open only)
            4 -> 0
            else -> throw IllegalStateException("Unknown position $position in walkthrough")
        }

        val context = collection.context
        val view = if (position == 4) {
            // Set up the question page
            val container = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
            }

            val morf = Morf.bind(container)

            // Home Page Prompt
            morf.text {
                text(R.string.walkthrough_homepage)
                gravity(Gravity.CENTER)
                paddingId(R.dimen.padding_small)
            }

            // Home Page
            morf.text {
                text(homePageManager.titleString)
                icon(Position.START, R.drawable.ic_phone_android)
                icon(Position.END, R.drawable.ic_chevron_right, true, Color.GRAY)
                onClick { onHomePageClick(context, it) }
            }

            // Faculty Prompt
            morf.text {
                text(R.string.walkthrough_faculty)
                gravity(Gravity.CENTER)
                paddingId(R.dimen.padding_small)
            }

            // Faculty
            morf.text {
                text(R.string.faculty_none)
                icon(Position.START, R.drawable.ic_mycourses)
                icon(Position.END, R.drawable.ic_chevron_right, true, Color.GRAY)
                onClick { onFacultyClick(context, it) }
            }
            container
        } else {
            LayoutInflater.from(context).inflate(layout, null)
        }

        collection.addView(view)
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        if (view is View) {
            collection.removeView(view)
        }
    }

    override fun getCount() = if (isFirstOpen) 5 else 4

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    private fun onHomePageClick(context: Context, item: TextViewItem) {
        val homePages = listOf(
            HomepageManager.HomePage.SCHEDULE,
            HomepageManager.HomePage.TRANSCRIPT,
            HomepageManager.HomePage.MY_COURSES,
            HomepageManager.HomePage.COURSES,
            HomepageManager.HomePage.WISHLIST,
            HomepageManager.HomePage.SEARCH_COURSES,
            HomepageManager.HomePage.EBILL,
            HomepageManager.HomePage.MAP,
            HomepageManager.HomePage.DESKTOP,
            HomepageManager.HomePage.SETTINGS
        )
            .map { Pair(it, homePageManager.getTitle(it)) }
            .sortedWith(Comparator { o1, o2 -> o1.second.compareTo(o2.second) })

        val currentChoice = homePages.indexOfFirst { it.first == homePageManager.homePage }

        context.singleListDialog(homePages.map { it.second }, R.string.settings_homepage_title, currentChoice) {
            val homePage = homePages[it].first

            // Update it
            homePageManager.homePage = homePage

            item.text(homePageManager.titleString)

            FirebaseAnalytics.getInstance(context)
                .event("walkthrough", "homepage" to homePageManager)
        }
    }

    private fun onFacultyClick(context: Context, item: TextViewItem) {
        val faculties = listOf(
            R.string.faculty_enviro,
            R.string.faculty_arts,
            R.string.faculty_continuing_studies,
            R.string.faculty_dentistry,
            R.string.faculty_education,
            R.string.faculty_engineering,
            R.string.faculty_graduate,
            R.string.faculty_law,
            R.string.faculty_management,
            R.string.faculty_medicine,
            R.string.faculty_music,
            R.string.faculty_religion,
            R.string.faculty_science
        )
            .map { context.getString(it) }
            .sortedWith(kotlin.Comparator { o1, o2 -> o1.compareTo(o2, true) })
            .toMutableList()

        // Add undefined to the top of the list
        faculties.add(0, context.getString(R.string.faculty_none))

        // Get the current choice index
        val currentChoice = faculties.indexOf(item.view.text.toString())

        context.singleListDialog(faculties, R.string.faculty_title, currentChoice) {
            val faculty = faculties[it]

            // Update the view
            item.text(faculty)
            FirebaseAnalytics.getInstance(context).event("walkthrough", "faculty" to faculty)
        }
    }
}
