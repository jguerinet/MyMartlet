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

package com.guerinet.mymartlet.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.guerinet.morf.Morf
import com.guerinet.morf.morf
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper
import com.guerinet.mymartlet.ui.walkthrough.WalkthroughActivity
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.DayUtils
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.retrofit.TranscriptConverter.TranscriptResponse
import com.guerinet.mymartlet.util.room.daos.CourseDao
import com.guerinet.mymartlet.util.room.daos.PlaceDao
import com.guerinet.mymartlet.util.room.daos.TranscriptDao
import com.guerinet.suitcase.date.extensions.getLongDateString
import com.guerinet.suitcase.prefs.BooleanPref
import com.guerinet.suitcase.util.extensions.getColorCompat
import com.guerinet.suitcase.util.extensions.openUrl
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.fragment_day.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

/**
 * Displays the user's schedule
 * @author Julien Guerinet
 * @since 1.0.0
 */
class ScheduleActivity : DrawerActivity() {

    private val firstOpenPref by inject<BooleanPref>(Prefs.IS_FIRST_OPEN)

    private val twentyFourHourPref by inject<BooleanPref>(Prefs.SCHEDULE_24HR)

    private val defaultTermPref by inject<DefaultTermPref>()

    private val courseDao by inject<CourseDao>()

    private val transcriptDao by inject<TranscriptDao>()

    private val placeDao by inject<PlaceDao>()

    private var term: Term = defaultTermPref.term

    private val courses: MutableList<Course> = mutableListOf()

    /**
     * Current date (to know which week to show in the landscape orientation)
     */
    private var date: LocalDate = LocalDate.now()

    override val currentPage = HomepageManager.HomePage.SCHEDULE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val savedTerm = savedInstanceState?.get(Constants.TERM) as? Term
        if (savedTerm != null) {
            term = savedTerm
        }

        // Title
        title = term.getString(this)

        // Update the list of courses for this currentTerm and the starting date
        updateCoursesAndDate()

        // Render the right view based on the orientation
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            renderLandscapeView()
        } else {
            renderPortraitView()
        }

        // Check if this is the first time the user is using the app
        if (firstOpenPref.value) {
            // Show them the walkthrough if it is
            startActivity<WalkthroughActivity>(Prefs.IS_FIRST_OPEN to true)
            // Save the fact that the walkthrough has been seen at least once
            firstOpenPref.value = false
        }
    }

    // Only show the menu in portrait mode
    override fun onPrepareOptionsMenu(menu: Menu): Boolean =
            resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.refresh, menu)
        menuInflater.inflate(R.menu.change_semester, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_change_semester -> {
                TermDialogHelper(this, this, term, false) {
                    // If it's the same currentTerm as now, do nothing
                    if (it == term) {
                        return@TermDialogHelper
                    }

                    // Set the instance currentTerm
                    term = it

                    // Set the default currentTerm
                    defaultTermPref.term = term

                    // Update the courses
                    updateCoursesAndDate()

                    // Title
                    title = it.getString(this@ScheduleActivity)

                    // TODO This only renders the portrait view
                    renderPortraitView()

                    // Refresh the content
                    refreshCourses()
                }
                return true
            }
            R.id.action_refresh -> {
                refreshCourses()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the currentTerm
        outState.putSerializable(Constants.TERM, term)
    }

    private fun updateCourses() {
        // Clear the current courses
        courses.clear()

        // Get the new courses for the current currentTerm
        launch(Dispatchers.IO) {
            courses.addAll(courseDao.getTermCourses(term))
        }
    }

    /**
     * Gets the courses for the given [Term]
     */
    private fun updateCoursesAndDate() {
        updateCourses()

        // Date is by default set to today
        date = LocalDate.now()

        // Check if we are in the current semester
        if (term != Term.currentTerm()) {
            // If not, find the starting date of this semester instead of using today
            for (course in courses) {
                if (course.startDate.isBefore(date)) {
                    date = course.startDate
                }
            }
        }
    }

    /**
     * Refreshes the list of courses for the given currentTerm and the user's transcript
     * (only available in portrait mode)
     */
    private fun refreshCourses() {
        if (!canRefresh()) {
            return
        }

        // Download the courses for this currentTerm
        mcGillService.schedule(term).enqueue(object : Callback<List<Course>> {
            override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                launch(Dispatchers.IO) {
                    courseDao.update(response.body() ?: listOf(), term)
                    launch {
                        // Update the view
                        toolbarProgress.isVisible = false
                        updateCourses()
                        @Suppress("PLUGIN_WARNING")
                        viewPager.adapter?.notifyDataSetChanged()
                    }
                }

                // Download the transcript (if ever the user has new semesters on their transcript)
                mcGillService.transcript().enqueue(object : Callback<TranscriptResponse> {
                    override fun onResponse(call: Call<TranscriptResponse>,
                            response: Response<TranscriptResponse>) {
                        launch(Dispatchers.IO) {
                            transcriptDao.update(response.body()?.transcript!!)
                        }
                    }

                    override fun onFailure(call: Call<TranscriptResponse>, t: Throwable) {
                        handleError("refreshing the transcript", t)
                    }
                })
            }

            override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                handleError("refreshing the courses", t)
            }
        })
    }

    /* TODO Bring landscape view back
    /**
     * Renders the landscape view
     */
    @Suppress("PLUGIN_WARNING")
    private fun renderLandscapeView() {
        // Leave space at the top for the day names
        var dayView = View.inflate(this, R.layout.fragment_day_name, null)
        // Black line to separate the timetable from the schedule
        val dayViewLine = dayView.findViewById<View>(R.id.day_line)
        dayViewLine.visibility = View.VISIBLE

        // Add the day view to the top of the timetable
        timetableContainer.addView(dayView)

        // Find the index of the given date
        val currentDayIndex = date.dayOfWeek.value

        // Go through the 7 days of the week
        for (i in 1..7) {
            val day = DayOfWeek.of(i)

            // Set up the day name
            dayView = View.inflate(this, R.layout.fragment_day_name, null)
            val dayViewTitle = dayView.findViewById<TextView>(R.id.day_name)
            dayViewTitle.setText(DayUtils.getStringId(day))
            scheduleContainer!!.addView(dayView)

            // Set up the schedule container for that one day
            val scheduleContainer = LinearLayout(this)
            scheduleContainer.orientation = LinearLayout.VERTICAL
            scheduleContainer.layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.cell_landscape_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT)

            // Fill the schedule for the current day
            fillSchedule(this.timetableContainer, scheduleContainer,
                    date.plusDays((i - currentDayIndex).toLong()), false)

            // Add the current day to the schedule container
            this.scheduleContainer!!.addView(scheduleContainer)

            // Line
            val line = View(this)
            line.setBackgroundColor(Color.BLACK)
            line.layoutParams = ViewGroup.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.schedule_line),
                    ViewGroup.LayoutParams.MATCH_PARENT)
            this.scheduleContainer!!.addView(line)
        }
    }
    */

    /**
     * Renders the portrait view
     */
    private fun renderPortraitView() {
        val viewPager = this.viewPager ?: throw IllegalStateException("No ViewPager found")

        val adapter = ScheduleAdapter()

        // Set up the ViewPager
        viewPager.apply {
            this.adapter = adapter
            currentItem = adapter.startingDateIndex
            addOnPageChangeListener(object :
                androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                override fun onPageScrolled(i: Int, v: Float, i2: Int) {}

                override fun onPageSelected(i: Int) {
                    //Update the date every time the page is turned to have the right
                    //  week if ever the user rotates his device
                    date = adapter.getDate(i)
                }

                override fun onPageScrollStateChanged(i: Int) {}
            })
        }
    }

    /**
     * Fills the schedule based on given data
     *
     * @param timetableContainer Container for the timetable
     * @param scheduleContainer  Container for the schedule
     * @param date               Date to fill the schedule for
     * @param clickable          True if the user can click on the courses (portrait),
     * false otherwise (landscape)
     */
    private fun fillSchedule(timetableContainer: LinearLayout?, scheduleContainer: LinearLayout?,
            date: LocalDate, clickable: Boolean) {
        // Go through the list of courses, find which ones are for the given date
        val courses = this.courses.filter { it.isForDate(date) }

        // Set up the DateTimeFormatter we're going to use for the hours
        val pattern = if (twentyFourHourPref.value) "HH:mm" else "hh a"
        val formatter = DateTimeFormatter.ofPattern(pattern)

        // This will be used of an end time of a course when it is added to the schedule container
        var currentCourseEndTime: LocalTime? = null

        // Cycle through the hours
        for (hour in 8..21) {
            // Start inflating a timetable cell
            val timetableCell = View.inflate(this, R.layout.item_day_timetable, null)

            // Put the correct time
            val time = timetableCell.findViewById<TextView>(R.id.cell_time)
            time.text = LocalTime.MIDNIGHT.withHour(hour).format(formatter)

            // Add it to the right container
            timetableContainer!!.addView(timetableCell)

            // Cycle through the half hours
            var min = 0
            while (min < 31) {
                // Initialize the current course to null
                var currentCourse: Course? = null

                // Get the current time
                val currentTime = LocalTime.of(hour, min)

                // if currentCourseEndTime = null (no course is being added) or it is equal to
                //  the current time in min (end of a course being added) we need to add a new view
                if (currentCourseEndTime == null || currentCourseEndTime == currentTime) {
                    // Reset currentCourseEndTime
                    currentCourseEndTime = null

                    // Check if there is a course at this time
                    for (course in courses) {
                        // If there is, set the current course to that time, and calculate the
                        //  ending time of this course
                        if (course.roundedStartTime == currentTime) {
                            currentCourse = course
                            currentCourseEndTime = course.roundedEndTime
                            break
                        }
                    }

                    val scheduleCell: View

                    // There is a course at this time
                    if (currentCourse != null) {
                        // Inflate the right view
                        scheduleCell = View.inflate(this, R.layout.item_day_class, null)

                        // Set up all of the info
                        val code = scheduleCell.findViewById<TextView>(R.id.code)
                        code.text = currentCourse.code

                        val type = scheduleCell.findViewById<TextView>(R.id.type)
                        type.text = currentCourse.type

                        val courseTime = scheduleCell.findViewById<TextView>(R.id.course_time)
                        courseTime.text = currentCourse.timeString

                        val location = scheduleCell.findViewById<TextView>(R.id.course_location)
                        location.text = currentCourse.location

                        // Find out how long this course is in terms of blocks of 30 min
                        val length = ChronoUnit.MINUTES.between(
                                currentCourse.roundedStartTime,
                                currentCourse.roundedEndTime).toInt() / 30

                        // Set the height of the view depending on this height
                        val lp = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                resources
                                        .getDimension(R.dimen.cell_30min_height).toInt() * length)
                        scheduleCell.layoutParams = lp

                        // Check if we need to make the course clickable
                        if (clickable) {
                            // We need a final variable for the onClick listener
                            val course = currentCourse
                            // OnClick: CourseActivity (for a detailed description of the course)
                            scheduleCell.setOnClickListener { showCourseDialog(course) }
                        } else {
                            scheduleCell.isClickable = false
                        }
                    } else {
                        // Inflate the empty view
                        scheduleCell = View.inflate(this, R.layout.item_day_empty, null)
                    }

                    // Add the given view to the schedule container
                    scheduleContainer!!.addView(scheduleCell)
                }
                min += 30
            }
        }
    }

    /**
     * Shows a dialog with course information
     *
     * @param course Clicked [Course]
     */
    private fun showCourseDialog(course: Course) {
        ga.sendScreen("Schedule - Course")

        // Set up the view in the dialog
        val view = ScrollView(this)
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        view.addView(container)

        // Create the dialog
        val alert = AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
            .setNeutralButton(R.string.done) { dialog, _ -> dialog.dismiss() }
                .show()

        // Populate the form
        val shape = Morf.shape
        //                .setShowLine(false)
        //                .setInputDefaultBackground(android.R.color.transparent)
        container.morf(shape) {
            // Code
            addTextInput(R.string.course_code, course.code)

            // Title
            addTextInput(R.string.course_name, course.title)

            // Time
            addTextInput(R.string.course_time_title, course.timeString)

            // Location
            addTextInput(R.string.course_location, course.location)

            // Type
            addTextInput(R.string.schedule_type, course.type)

            // Instructor
            addTextInput(R.string.course_prof, course.instructor)

            // Section
            addTextInput(R.string.course_section, course.section)

            // Credits
            addTextInput(R.string.course_credits_title, course.credits.toString())

            // CRN
            addTextInput(R.string.course_crn, course.crn.toString())

            val color = getColorCompat(R.color.red)

            // Docuum
            borderlessButton {
                layoutParams(
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ), Gravity.CENTER
                )
                textId = R.string.course_docuum
                textColor = color
                onClick {
                    openUrl(
                        "http://www.docuum.com/mcgill/${course.subject.toLowerCase()}" +
                                "/${course.number}"
                    )
                }
            }

            // Maps
            borderlessButton {
                layoutParams(
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ), Gravity.CENTER
                )
                textId = R.string.course_map
                textColor = color
                onClick {
                    // Try to find a place that has the right name
                    launch(Dispatchers.IO) {
                        val places = placeDao.getPlaces()
                        val place = places.firstOrNull { place ->
                            course.location.contains(place.name, true)
                        }
                        if (place == null) {
                            // Tell the user
                            toast(getString(R.string.error_place_not_found, course.location))
                            // Send a Crashlytics report
                            Timber.e(NullPointerException("Location not found: ${course.location}"))
                        } else {
                            // Close the dialog
                            alert.dismiss()
                            // Open the map to the given place
                            val intent = intentFor<MapActivity>(Constants.ID to place.id)
                            handler.post { switchDrawerActivity(intent) }
                        }
                    }
                }
            }
        }
    }

    private fun Morf.addTextInput(@StringRes hintId: Int, text: String) = textInput {
        this.hintId = hintId
        this.text = text
        isEnabled = false
    }

    /**
     * Adapter used for the ViewPager in the portrait view of the schedule
     */
    internal inner class ScheduleAdapter : androidx.viewpager.widget.PagerAdapter() {

        /**
         * The initial date to use as a reference
         */
        private val startingDate = date
        /**
         * The index of the starting date (offset of 500001 to get the right day)
         */
        val startingDateIndex = 500001 + date.dayOfWeek.value

        private val holders = Stack<DayHolder>()

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val holder = if (holders.isNotEmpty()) {
                holders.pop()
            } else {
                DayHolder(
                    LayoutInflater.from(collection.context)
                        .inflate(R.layout.fragment_day, collection, false)
                )
            }

            // Get the date for this view and set it
            holder.bind(getDate(position))

            collection.addView(holder.view)
            return holder.view
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            val dayView = view as View
            collection.removeView(dayView)
            holders.push(DayHolder(view))
        }

        override fun getCount() = 1000000

        fun getDate(position: Int): LocalDate =
                startingDate.plusDays((position - startingDateIndex).toLong())

        // This is to force the refreshing of all of the views when the view is reloaded
        override fun getItemPosition(`object`: Any): Int =
            androidx.viewpager.widget.PagerAdapter.POSITION_NONE

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        inner class DayHolder(val view: View) {

            fun bind(date: LocalDate) {
                // Set the titles
                view.apply {
                    dayTitle.setText(DayUtils.getStringId(date.dayOfWeek))
                    dayDate.text = date.getLongDateString()

                    // Fill the schedule up
                    fillSchedule(timetableContainer, scheduleContainer, date, true)
                }
            }
        }
    }
}