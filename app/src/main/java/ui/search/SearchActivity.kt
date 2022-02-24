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

package com.guerinet.mymartlet.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.DayUtils
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.mymartlet.util.extensions.start
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.suitcase.coroutines.ioDispatcher
import com.guerinet.suitcase.log.TimberTag
import com.guerinet.suitcase.util.Device
import com.guerinet.suitcase.util.extensions.toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DayOfWeek
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Allows a user to search for courses that they can register for
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SearchActivity : DrawerActivity(), TimberTag {

    override val tag: String = "SearchActivity"

    private var registrationTerms = listOf<Term>()

    private lateinit var term: Term

    private var isAllOptionsShown = false

    override val currentPage = HomepageManager.HomePage.SEARCH_COURSES

    private val searchEmpty by lazy<TextView> { findViewById(R.id.searchEmpty) }
    private val searchContainer by lazy<View> { findViewById(R.id.searchContainer) }
    private val searchButton by lazy<Button> { findViewById(R.id.searchButton) }
    private val termSelector by lazy<TextView> { findViewById(R.id.termSelector) }
    private val termContainer by lazy<View> { findViewById(R.id.termContainer) }
    private val startTime by lazy<TimePicker> { findViewById(R.id.startTime) }
    private val endTime by lazy<TimePicker> { findViewById(R.id.endTime) }
    private val moreOptionsButton by lazy<Button> { findViewById(R.id.moreOptionsButton) }
    private val moreOptionsContainer by lazy<View> { findViewById(R.id.moreOptionsContainer) }
    private val subject by lazy<EditText> { findViewById(R.id.subject) }
    private val minCredits by lazy<EditText> { findViewById(R.id.minCredits) }
    private val maxCredits by lazy<EditText> { findViewById(R.id.maxCredits) }
    private val monday by lazy<CheckBox> { findViewById(R.id.monday) }
    private val tuesday by lazy<CheckBox> { findViewById(R.id.tuesday) }
    private val wednesday by lazy<CheckBox> { findViewById(R.id.wednesday) }
    private val thursday by lazy<CheckBox> { findViewById(R.id.thursday) }
    private val friday by lazy<CheckBox> { findViewById(R.id.friday) }
    private val saturday by lazy<CheckBox> { findViewById(R.id.saturday) }
    private val sunday by lazy<CheckBox> { findViewById(R.id.sunday) }
    private val number by lazy<EditText> { findViewById(R.id.number) }
    private val courseTitle by getView<EditText>(R.id.courseTitle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        launch {
            // Check if there are any terms to register for
            registrationTerms = withContext(ioDispatcher) { Term.loadRegistrationTerms() }

            if (registrationTerms.isEmpty()) {
                // Hide all of the search related stuff, show explanatory text, and return
                searchEmpty.isVisible = true
                searchContainer.isVisible = false
                searchButton.isVisible = false
                return@launch
            }

            // Set the currentTerm to the first one
            term = registrationTerms.first()
            termSelector.text = term.getString(this@SearchActivity)
            termContainer.setOnClickListener {
                TermDialogHelper(this@SearchActivity, this@SearchActivity, term, registrationTerms) {
                    term = it
                    termSelector.text = term.getString(this@SearchActivity)
                }
            }

            startTime.setIs24HourView(false)
            endTime.setIs24HourView(false)

            moreOptionsButton.setOnClickListener { showMoreOptions() }
            searchButton.setOnClickListener { searchCourses() }

            reset()
        }
    }

    private fun showMoreOptions() {
        // Inverse the boolean
        isAllOptionsShown = !isAllOptionsShown

        moreOptionsContainer.isVisible = isAllOptionsShown
        moreOptionsButton.setText(
            if (isAllOptionsShown)
                R.string.registration_hide_options
            else
                R.string.registration_show_options
        )
    }

    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun searchCourses() {
        // Subject Input
        val subject = this.subject.text.toString().toUpperCase().trim()
        if (subject.isEmpty()) {
            toast(getString(R.string.registration_error_no_faculty))
            return
        } else if (!subject.matches("[A-Za-z]{4}".toRegex())) {
            toast(getString(R.string.registration_invalid_subject))
            return
        }

        // Check that the credits are valid
        val minCredits = this.minCredits.text.toString().toIntOrNull() ?: 0
        val maxCredits = this.maxCredits.text.toString().toIntOrNull() ?: 0

        if (maxCredits < minCredits) {
            toast(getString(R.string.registration_error_credits))
            return
        }

        // Show the user we are downloading new information
        toolbarProgress.isVisible = true

        val startHour: Int
        val startMinute: Int
        val startAM: Boolean
        val endHour: Int
        val endMinute: Int
        val endAM: Boolean

        if (Device.isApiLevel(23)) {
            startHour = this.startTime.hour % 12
            startMinute = this.startTime.minute
            startAM = this.startTime.hour < 12
            endHour = this.endTime.hour % 12
            endMinute = this.endTime.minute
            endAM = this.endTime.hour < 12
        } else {
            startHour = this.startTime.currentHour % 12
            startMinute = this.startTime.currentMinute
            startAM = this.startTime.currentHour < 12
            endHour = this.endTime.currentHour % 12
            endMinute = this.endTime.currentMinute
            endAM = this.endTime.currentHour < 12
        }

        // Days
        val days = mutableListOf<DayOfWeek>()

        if (monday.isChecked) {
            days.add(DayOfWeek.MONDAY)
        }
        if (tuesday.isChecked) {
            days.add(DayOfWeek.TUESDAY)
        }
        if (wednesday.isChecked) {
            days.add(DayOfWeek.WEDNESDAY)
        }
        if (thursday.isChecked) {
            days.add(DayOfWeek.THURSDAY)
        }
        if (friday.isChecked) {
            days.add(DayOfWeek.FRIDAY)
        }
        if (saturday.isChecked) {
            days.add(DayOfWeek.SATURDAY)
        }
        if (sunday.isChecked) {
            days.add(DayOfWeek.SUNDAY)
        }

        val dayChars = days.map { DayUtils.getDayChar(it) }

        // Check if we can refresh
        if (!canRefresh()) {
            return
        }

        // Execute the request
        mcGillService.search(
            term, subject, number.text.toString(), courseTitle.text.toString(),
            minCredits, maxCredits, startHour, startMinute,
            if (startAM) "a" else "p", endHour, endMinute, if (endAM) "a" else "p", dayChars
        )
            .enqueue(object : Callback<List<CourseResult>> {
                override fun onResponse(
                    call: Call<List<CourseResult>>,
                    response: Response<List<CourseResult>>
                ) {
                    toolbarProgress.isVisible = false
                    val body = response.body()
                    if (body != null) {
                        start<SearchResultsActivity>(
                            Constants.TERM to term,
                            Constants.COURSES to (body as ArrayList<CourseResult>)
                        )
                    }
                }

                override fun onFailure(call: Call<List<CourseResult>>, t: Throwable) {
                    handleError("searching for courses", t)
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Only inflate the menu if there are semesters to register for
        if (registrationTerms.isNotEmpty()) {
            menuInflater.inflate(R.menu.reset, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                reset()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun reset() {
        //Reset all of the views
        if (Device.isApiLevel(23)) {
            startTime.hour = 0
            startTime.minute = 0
            endTime.hour = 0
            endTime.minute = 0
        } else {
            startTime.currentHour = 0
            startTime.currentMinute = 0
            endTime.currentHour = 0
            endTime.currentMinute = 0
        }

        subject.setText("")
        number.setText("")
        courseTitle.setText("")
        minCredits.setText("")
        maxCredits.setText("")
        monday.isChecked = false
        tuesday.isChecked = false
        wednesday.isChecked = false
        thursday.isChecked = false
        friday.isChecked = false
        saturday.isChecked = false
        sunday.isChecked = false
    }
}
