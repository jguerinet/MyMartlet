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

package com.guerinet.mymartlet.ui.wishlist

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.DayUtils
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.suitcase.ui.BaseListAdapter

/**
 * Displays the list of courses in the user's wish list or after a course search
 * @author Julien Guerinet
 * @since 1.0.0
 */
internal class WishlistAdapter(private val empty: TextView) :
    BaseListAdapter<CourseResult>(ItemCallback(), empty) {

    val checkedCourses = mutableListOf<CourseResult>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder =
        CourseHolder(parent)

    /**
     * Updates the list of [courses] shown
     */
    fun update(courses: List<CourseResult>) = submitList(courses.toMutableList())

    /**
     * Updates the list of courses shown for a [term] (null if no wishlist semesters available)
     */
    fun update(term: Term?, courses: List<CourseResult>) {
        if (term == null) {
            // Hide all of the main content and show explanatory text if the currentTerm is null
            empty.setText(R.string.registration_no_semesters)
            submitList(mutableListOf())
            return
        }
        submitList(courses.toMutableList())
    }

    internal inner class CourseHolder(parent: ViewGroup) :
        BaseHolder<CourseResult>(parent, R.layout.item_course) {

        private val code by itemView.getView<TextView>(R.id.code)
        private val credits by itemView.getView<TextView>(R.id.credits)
        private val title by itemView.getView<TextView>(R.id.title)
        private val spots by itemView.getView<TextView>(R.id.spots)
        private val type by itemView.getView<TextView>(R.id.type)
        private val waitlistRemaining by itemView.getView<TextView>(R.id.waitlistRemaining)
        private val days by itemView.getView<TextView>(R.id.days)
        private val hours by itemView.getView<TextView>(R.id.hours)
        private val dates by itemView.getView<TextView>(R.id.dates)
        private val checkBox by itemView.getView<CheckBox>(R.id.checkBox)

        override fun bind(position: Int, item: CourseResult) {
            itemView.apply {
                code.text = item.code
                credits.text = context.getString(R.string.course_credits, item.credits.toString())
                title.text = item.title
                spots.visibility = View.VISIBLE
                spots.text = context.getString(
                    R.string.registration_spots,
                    item.seatsRemaining.toString()
                )
                type.text = item.type
                waitlistRemaining.visibility = View.VISIBLE
                waitlistRemaining.text = context.getString(
                    R.string.registration_waitlist,
                    item.waitlistRemaining.toString()
                )
                days.text = DayUtils.getDayStrings(item.days)
                hours.text = item.timeString
                dates.text = item.dateString

                checkBox.visibility = View.VISIBLE

                // Remove any other listeners
                checkBox.setOnCheckedChangeListener(null)
                // Initial state
                checkBox.isChecked = checkedCourses.contains(item)
                checkBox.setOnCheckedChangeListener { _, checked ->
                    // If it becomes checked, add it to the list. If not, remove it
                    if (checked) {
                        checkedCourses.add(item)
                    } else {
                        checkedCourses.remove(item)
                    }
                }
            }
        }
    }

    internal class ItemCallback : DiffUtil.ItemCallback<CourseResult>() {
        override fun areItemsTheSame(oldItem: CourseResult, newItem: CourseResult): Boolean =
            oldItem.term == newItem.term && oldItem.crn == newItem.crn

        override fun areContentsTheSame(oldItem: CourseResult, newItem: CourseResult): Boolean =
            oldItem == newItem
    }
}
