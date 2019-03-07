/*
 * Copyright 2014-2019 Julien Guerinet
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
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.RegistrationError
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.manager.McGillManager
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.viewmodel.CoursesViewModel
import com.guerinet.suitcase.dialog.cancelButton
import com.guerinet.suitcase.dialog.okButton
import com.guerinet.suitcase.dialog.showDialog
import com.guerinet.suitcase.lifecycle.observe
import com.guerinet.suitcase.log.TimberTag
import com.guerinet.suitcase.ui.extensions.setWidthAndHeight
import kotlinx.android.synthetic.main.view_courses.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Shows the user all of the courses they have taken or is currently registered in
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @since 1.0.0
 */
class CoursesActivity : DrawerActivity(), TimberTag {

    override val tag: String = "CoursesActivity"

    override val currentPage = HomepageManager.HomePage.COURSES

    private val coursesViewModel by viewModel<CoursesViewModel>()

    private val defaultTermPref by inject<DefaultTermPref>()

    private val adapter: CoursesAdapter by lazy { CoursesAdapter(empty) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        // Format the unregister button
        register.apply {
            setText(R.string.courses_unregister)
            setWidthAndHeight(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener { unregister() }
        }

        // Remove the wishlist button
        wishlist.isVisible = false

        observe(coursesViewModel.term) {
            val term = it ?: return@observe

            // Set the title
            title = term.getString(this)
        }

        observe(coursesViewModel.termCourses) {
            val courses = it ?: return@observe

            // Update the list of shown courses
            //  TODO Better way of doing this
            adapter.update(courses, coursesViewModel.isUnregisterPossible.value ?: false)
        }

        observe(coursesViewModel.isUnregisterPossible) {
            val isUnregisterPossible = it ?: return@observe
            // Change the visibility of the register button if we are in the list of currently registered courses
            register.isVisible = isUnregisterPossible
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.refresh, menu)
        menuInflater.inflate(R.menu.change_semester, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_change_semester -> {
                TermDialogHelper(this, this, coursesViewModel.term.value, false) {
                    // Set the default term. This will kick off the updating of the UI
                    defaultTermPref.term = it
                    refresh()
                }
                true
            }
            R.id.action_refresh -> {
                refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Refreshes the list of courses for the current term
     */
    private fun refresh() {
        if (!canRefresh()) {
            return
        }

        // TODO Deal with the progress bar

        launch { coursesViewModel.refreshCourses() }
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

                    override fun onFailure(
                        call: Call<List<RegistrationError>>,
                        t: Throwable

                        ) {handleError("unregistering for courses", t)
                    }
                })}
        }
    }
}
