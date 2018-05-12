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

package com.guerinet.mymartlet.ui.dialog.list

import android.util.Pair

import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.suitcase.dialog.SingleListInterface
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Displays the available homepages
 * @author Julien Guerinet
 * @since 1.0.0
 */
abstract class HomepagesAdapter : SingleListInterface, KoinComponent {

    private val homePageManager by inject<HomepageManager>()

    private val homePages = listOf(
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

    override val currentChoice: Int = homePages.indexOfFirst { it.first == homePageManager.homePage }

    override val choices: Array<String> = homePages.map { it.second }.toTypedArray()

    override fun onChoiceSelected(position: Int) = onHomePageSelected(homePages[position].first)

    /**
     * Called when a [homePage] has been selected
     */
    abstract fun onHomePageSelected(homePage: HomepageManager.HomePage)
}
