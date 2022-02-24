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

package com.guerinet.mymartlet.ui.courses

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.RegistrationError
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.retrofit.TranscriptConverter.TranscriptResponse
import com.guerinet.mymartlet.util.room.daos.CourseDao
import com.guerinet.mymartlet.util.room.daos.TranscriptDao
import com.guerinet.suitcase.coroutines.ioDispatcher
import com.guerinet.suitcase.dialog.cancelButton
import com.guerinet.suitcase.dialog.okButton
import com.guerinet.suitcase.dialog.showDialog
import com.guerinet.suitcase.util.extensions.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val courseDao by inject<CourseDao>()

    private val transcriptDao by inject<TranscriptDao>()

    private val adapter: CoursesAdapter by lazy { CoursesAdapter(empty) }

    private var term: Term = defaultTermPref.term

    override val currentPage = HomepageManager.HomePage.COURSES

    private val empty by lazy<TextView> { findViewById(R.id.empty) }
    private val list by lazy<RecyclerView> { findViewById(R.id.list) }
    private val register by lazy<Button> { findViewById(R.id.register) }
    private val wishlist by lazy<Button> { findViewById(R.id.button) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        // Format the unregister button
        register.setText(R.string.courses_unregister)
        register.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
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
                TermDialogHelper(this, this, term) {
                    // Set the default currentTerm
                    defaultTermPref.term = it

                    term = it

                    update()
                    refresh()
                }
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
        launch {
            // Set the title
            title = term.getString(this@CoursesActivity)

            // User can unregister if the current currentTerm is in the list of terms to register for
            val canUnregister = withContext(ioDispatcher) {
                Term.loadRegistrationTerms().contains(term)
            }

            // Load the courses for the current term
            val courses = withContext(ioDispatcher) {
                courseDao.getTermCourses(term)
            }

            adapter.update(courses, canUnregister)

            // Change the text and the visibility if we are in the list of currently registered courses
            register.isVisible = canUnregister
        }
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
                launch(Dispatchers.IO) {
                    courseDao.update(response.body() ?: listOf(), term)

                    withContext(Dispatchers.Main) {
                        // Update the view
                        update()
                        toolbarProgress.isVisible = false
                    }
                }

                // Download the transcript (if ever the user has new semesters on their transcript)
                mcGillService.oldTranscript().enqueue(object : Callback<TranscriptResponse> {
                    override fun onResponse(
                        call: Call<TranscriptResponse>,
                        response: Response<TranscriptResponse>
                    ) {
                        launch(Dispatchers.IO) {
                            transcriptDao.update(response.body()!!.transcript)
                        }
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
            toast(getString(R.string.courses_too_many_courses))
            return
        }

        if (courses.isEmpty()) {
            // No courses
            toast(getString(R.string.courses_none_selected))
            return
        }

        showDialog(R.string.unregister_dialog_title, R.string.unregister_dialog_message) {
            cancelButton {}
            okButton {
                if (!canRefresh()) {
                    return@okButton
                }

                // Run the registration thread
                mcGillService.registration(McGillManager.getRegistrationURL(courses, true))
                    .enqueue(object : Callback<List<RegistrationError>> {
                        override fun onResponse(
                            call: Call<List<RegistrationError>>,
                            response: Response<List<RegistrationError>>
                        ) {
                            toolbarProgress.isVisible = false

                            // If there are no errors, show the success message
                            val body = response.body()
                            if (body == null || body.isEmpty()) {
                                toast(getString(R.string.unregistration_success))
                                return
                            }

                            // Prepare the error message String
                            val errorMessage = body.joinToString("\n",
                                transform = { error -> error.getString(courses) })
                            errorDialog(errorMessage)

                            // Refresh the courses
                            refresh()
                        }

                        override fun onFailure(
                            call: Call<List<RegistrationError>>,
                            t: Throwable
                        ) {
                            handleError("unregistering for courses", t)
                        }
                    })
            }
        }
    }
}
