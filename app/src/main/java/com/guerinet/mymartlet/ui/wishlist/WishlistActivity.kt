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

package com.guerinet.mymartlet.ui.wishlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.core.widget.toast
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper
import com.guerinet.mymartlet.util.dagger.prefs.RegisterTermsPref
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.view_courses.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Response

/**
 * Displays the user's wishlist
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
class WishlistActivity : DrawerActivity() {

    private val registerTermsPref by inject<RegisterTermsPref>()

    /**
     * Current term, null if none possible (no semesters to register for)
     */
    private var term: Term? = null

    private val wishlistHelper: WishlistHelper by lazy {
        WishlistHelper(this, mainView, false)
    }

    /**
     * Keeps track of the number of concurrent update calls that are currently being executed
     */
    private var updateCount: Int = 0

    override val currentPage = HomepageManager.HomePage.WISHLIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)
        ga.sendScreen("Wishlist")

        // Load the first registration currentTerm if there is one
        term = registerTermsPref.terms.firstOrNull()
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!registerTermsPref.terms.isEmpty()) {
            menuInflater.inflate(R.menu.refresh, menu)

            // Allow user to change the semester if there is more than 1 semester
            if (registerTermsPref.terms.size > 1) {
                menuInflater.inflate(R.menu.change_semester, menu)
            }
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_semester -> {
                changeSemester()
                true
            }
            R.id.action_refresh -> {
                refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun changeSemester() {
        TermDialogHelper(this, term, true) {
            term = it
            update()
        }
    }

    /**
     * Updates the view
     */
    private fun update() {
        // Set the title if there is a currentTerm
        title = term?.getString(this)

        // Reload the adapter
        wishlistHelper.update(term)
    }

    /**
     * Refreshes the course info on the current wishlist
     */
    private fun refresh() {
        toolbarProgress.isVisible = true

        // Go through the user's wishlist
        SQLite.select()
                .from(CourseResult::class)
                .async()
                .queryListResultCallback { _, tResult ->
                    val holders = mutableListOf<CourseHolder>()
                    for (course in tResult) {
                        val holder = CourseHolder(course)
                        if (!holders.contains(holder)) {
                            holders.add(holder)
                        }
                    }

                    launch(UI) { performUpdateCalls(holders) }
                }
                .execute()

    }

    /**
     * Makes the necessary calls to update the list of [courses]
     */
    private fun performUpdateCalls(courses: List<CourseHolder>) {
        if (courses.isEmpty()) {
            // If there are no courses, don't continue
            toolbarProgress.isVisible = false
            return
        }

        // Set the update count to the number of courses
        updateCount = courses.size
        for (course in courses) {
            // Get the course registration URL
            val code = course.code.split(" ".toRegex())
            if (code.size < 2) {
                toast(getString(R.string.error_cannot_update, course.code))
                finalizeUpdate()
                continue
            }

            val subject = code[0]
            val number = code[1]

            mcGillService.search(course.term, subject, number, "", 0, 0, 0, 0, "a", 0, 0, "a",
                    mutableListOf()).enqueue(object : retrofit2.Callback<List<CourseResult>> {
                override fun onResponse(call: Call<List<CourseResult>>,
                        response: Response<List<CourseResult>>) {
                    // Go through the received courses, check if they are on the user's wishlist
                    val receivedCourses = response.body()
                    if (receivedCourses == null) {
                        finalizeUpdate()
                        return
                    }
                    receivedCourses.forEach {
                        val exists = SQLite.selectCountOf()
                                .from(CourseResult::class)
                                .where(CourseResult_Table.term.eq(it.term))
                                .and(CourseResult_Table.crn.eq(it.crn))
                                .hasData()
                        if (exists) {
                            course.save()
                        }
                    }
                    finalizeUpdate()
                }

                override fun onFailure(call: Call<List<CourseResult>>, t: Throwable) {
                    handleError("updating wishlist", t)
                    finalizeUpdate()
                }
            })
        }
    }

    /**
     * Finalizes an update call and determine whether UI action is necessary depending on where
     *  we are in the update process
     */
    private fun finalizeUpdate() {
        // Decrement the update count
        updateCount--

        // If there are no more updates to wait for, hide the progress bar and reload the adapter
        if (updateCount == 0) {
            update()
            toolbarProgress.isVisible = false
        }
    }

    /**
     * [CourseResult] holder to update the user's wishlist
     *
     * @param course    [CourseResult] instance to hold the information for
     */
    @Suppress("EqualsOrHashCode")
    private inner class CourseHolder(course: CourseResult) {

        val term: Term = course.term

        val code: String = course.code

        override fun equals(other: Any?): Boolean {
            if (other !is CourseHolder) {
                return false
            }
            return other.term == term && other.code == code
        }
    }
}