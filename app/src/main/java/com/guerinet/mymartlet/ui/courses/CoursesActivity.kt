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

package com.guerinet.mymartlet.ui.courses

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.RegistrationError
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper
import com.guerinet.mymartlet.util.dagger.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.dagger.prefs.RegisterTermsPref
import com.guerinet.mymartlet.util.dbflow.databases.CourseDB
import com.guerinet.mymartlet.util.dbflow.databases.TranscriptDB
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.retrofit.TranscriptConverter.TranscriptResponse
import com.guerinet.suitcase.dialog.alertDialog
import com.guerinet.suitcase.dialog.singleListDialog
import com.guerinet.suitcase.ui.extensions.setWidthAndHeight
import kotlinx.android.synthetic.main.view_courses.*
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Shows the user all of the courses they have taken or is currently registered in
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @since 1.0.0
 */
class CoursesActivity : DrawerActivity() {

    private val defaultTermPref by inject<DefaultTermPref>()

    private val registerTermsPref by inject<RegisterTermsPref>()

    private val adapter: CoursesAdapter by lazy { CoursesAdapter(empty) }

    private var term: Term = defaultTermPref.getTerm()

    override val currentPage = HomepageManager.HomePage.COURSES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)
        ga.sendScreen("View Courses")

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        // Format the unregister button
        register.setText(R.string.courses_unregister)
        register.setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        register.setOnClickListener { unregister() }

        // Remove the wishlist button
        wishlist.isVisible = false
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.refresh, menu)
        menuInflater.inflate(R.menu.change_semester, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_semester -> {
                singleListDialog(R.string.title_change_semester,
                        object : TermDialogHelper(this, term, false) {
                            override fun onTermSelected(term: Term) {
                                // Set the default currentTerm
                                defaultTermPref.setTerm(term)

                                // Set the instance currentTerm
                                this@CoursesActivity.term = term

                                update()
                                refresh()
                            }
                        })
                return true
            }
            R.id.action_refresh -> {
                refresh()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    /**
     * Updates all of the info in the view
     */
    private fun update() {
        // Set the title
        title = term.getString(this)

        // User can unregister if the current currentTerm is in the list of terms to register for
        val canUnregister = registerTermsPref.terms.contains(term)

        // Change the text and the visibility if we are in the list of currently registered courses
        register.isVisible = canUnregister

        // Update the list
        adapter.update(term, canUnregister)
    }

    /**
     * Refreshes the list of courses for the given currentTerm and the user's transcript
     */
    private fun refresh() {
        if (!canRefresh()) {
            return
        }

        // Download the courses for this currentTerm
        mcGillService.schedule(term).enqueue(object : Callback<List<Course>> {
            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                // Set the courses
                CourseDB.setCourses(term, response.body()) {
                    handler.post {
                        // Update the view
                        update()
                        toolbarProgress.isVisible = false
                    }
                }

                // Download the transcript (if ever the user has new semesters on their transcript)
                mcGillService.transcript().enqueue(object : Callback<TranscriptResponse> {
                    override fun onResponse(call: Call<TranscriptResponse>,
                            response: Response<TranscriptResponse>) {
                        TranscriptDB.saveTranscript(this@CoursesActivity, response.body()!!)
                    }

                    override fun onFailure(call: Call<TranscriptResponse>, t: Throwable) =
                            handleError("refreshing transcript", t)
                })
            }

            override fun onFailure(call: Call<List<Course>>, t: Throwable) =
                    handleError("refreshing courses", t)
        })
    }

    /**
     * Attempts to unregister from the given courses
     */
    private fun unregister() {
        // Get checked courses from adapter
        val courses = adapter.checkedCourses

        if (courses.size > 10) {
            // Too many courses
            toast(R.string.courses_too_many_courses)
            return
        }

        if (courses.isEmpty()) {
            // No courses
            toast(R.string.courses_none_selected)
            return
        }

        alertDialog(R.string.unregister_dialog_title, R.string.unregister_dialog_message,
                MaterialDialog.SingleButtonCallback { _, which ->

                    // Don't continue if the positive button has not been clicked on
                    if (which != DialogAction.POSITIVE) {
                        return@SingleButtonCallback
                    }

                    if (!canRefresh()) {
                        return@SingleButtonCallback
                    }

                    // Run the registration thread
                    mcGillService.registration(McGillManager.getRegistrationURL(courses, true))
                            .enqueue(object : Callback<List<RegistrationError>> {
                                override fun onResponse(call: Call<List<RegistrationError>>,
                                        response: Response<List<RegistrationError>>) {
                                    toolbarProgress.isVisible = false

                                    // If there are no errors, show the success message
                                    val body = response.body()
                                    if (body == null || body.isEmpty()) {
                                        toast(R.string.unregistration_success)
                                        return
                                    }

                                    // Prepare the error message String
                                    val errorMessage = body.joinToString("\n",
                                            transform = { error -> error.getString(courses) })
                                    errorDialog(errorMessage)

                                    // Refresh the courses
                                    refresh()
                                }

                                override fun onFailure(call: Call<List<RegistrationError>>,
                                        t: Throwable) {
                                    handleError("unregistering for courses", t)
                                }
                            })
                })
    }
}