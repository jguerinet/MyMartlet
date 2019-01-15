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

import android.view.View
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.DialogAction
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.RegistrationError
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.retrofit.McGillService
import com.guerinet.mymartlet.util.room.daos.CourseResultDao
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.dialog.alertDialog
import kotlinx.android.synthetic.main.view_courses.view.*
import org.jetbrains.anko.toast
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Shows the results of the search from the SearchActivity
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param activity  Calling activity instance
 * @param container View to manipulate
 * @param canAdd    True if the user can add courses to the wishlist, false otherwise
 */
class WishlistHelper(private val activity: BaseActivity, container: View,
        private val canAdd: Boolean) : KoinComponent {

    private val courseResultDao by inject<CourseResultDao>()

    private val mcGillService by inject<McGillService>()

    private val ga by inject<GAManager>()

    private val adapter: WishlistAdapter by lazy { WishlistAdapter(container.empty) }

    init {
        container.apply {
            list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            list.adapter = adapter

            // Change the button text if this is to remove courses
            if (!canAdd) {
                wishlist.setText(R.string.courses_remove_wishlist)
            }

            wishlist.setOnClickListener { wishlist() }
            register.setOnClickListener { register() }
        }
    }

    /**
     * Updates the list of [courses] to display
     */
    fun update(courses: List<CourseResult>) = adapter.update(courses)

    /**
     * Updates the [term] that the courses should be in, null if none
     */
    fun update(term: Term?) {
        val courses = if (term != null) {
            courseResultDao.get(term)
        } else {
            listOf()
        }

        adapter.update(term, courses)
    }

    /**
     * Attempts to (un)register to the list of checked courses
     */
    private fun register() {
        val courses = adapter.checkedCourses
        when {
            // Too many courses
            courses.size > 10 -> activity.toast(R.string.courses_too_many_courses)
            // No Courses
            courses.isEmpty() -> activity.toast(R.string.courses_none_selected)
            else -> {
                // Execute registration of checked classes in a new thread
                if (!activity.canRefresh()) {
                    // Check that we can continue
                    return
                }

                // Confirm with the user before continuing
                activity.alertDialog(R.string.warning, R.string.registration_disclaimer)
                { _, which ->
                    if (which === DialogAction.POSITIVE) {
                        register(courses)
                    } else {
                        activity.toolbarProgress.isVisible = false
                    }
                }
            }
        }
    }

    internal fun wishlist() = updateWishlist(adapter.checkedCourses)

    private fun register(courses: List<CourseResult>) {
        mcGillService.registration(McGillManager.getRegistrationURL(courses, false))
                .enqueue(object : Callback<List<RegistrationError>> {

                    override fun onResponse(call: Call<List<RegistrationError>>,
                            response: Response<List<RegistrationError>>) {
                        activity.toolbarProgress.isVisible = false

                        val body = response.body()

                        if (body == null || body.isEmpty()) {
                            // If there are no errors, show the success message
                            activity.toast(R.string.registration_success)
                            courses.forEach { courseResultDao.delete(it) }
                            return
                        }

                        val errorCourses = courses.map { it as Course }.toMutableList()

                        // Prepare the error message String
                        val errorMessage = body.joinToString(separator = "\n") {
                            it.getString(errorCourses)
                        }

                        activity.errorDialog(errorMessage)
                    }

                    override fun onFailure(call: Call<List<RegistrationError>>, t: Throwable) =
                            activity.handleError("(un)registering for courses", t)
                })
    }

    /**
     * Adds/removes the [courses] to/from the wishlist
     */
    private fun updateWishlist(courses: List<CourseResult>) {
        val toastMessage = when {
            // If there are none, display error message
            courses.isEmpty() -> activity.getString(R.string.courses_none_selected)
            canAdd -> {
                val coursesAdded = courses.filter {
                    // Check if the course exists already
                    val isPresent = courseResultDao.get(it.term, it.crn) != null

                    // Save it
                    courseResultDao.insert(it)

                    // Only add it if it's not already part of the wishlist
                    isPresent
                }.size

                ga.sendEvent("Search Results", "Add to Wishlist", coursesAdded.toString())
                activity.getString(R.string.wishlist_add, coursesAdded)
            }
            else -> {
                courses.forEach { courseResultDao.delete(it) }

                // Get the term from the first course (they will all be in the same term)
                update(courses[0].term)

                ga.sendEvent("Wishlist", "Remove", courses.size.toString())
                activity.getString(R.string.wishlist_remove, courses.size)
            }
        }

        activity.toast(toastMessage)
    }
}