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

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.Course_Table
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.DayUtils
import com.guerinet.suitcase.ui.BaseListAdapter
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.item_course.view.*

/**
 * Displays the user's list of [Course]s
 * @author Julien Guerinet
 * @since 1.0.0
 */
internal class CoursesAdapter(emptyView: TextView) :
        BaseListAdapter<Course>(ItemCallback(), emptyView) {

    val checkedCourses = mutableListOf<Course>()

    private var canUnregister: Boolean = false

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CourseHolder = CourseHolder(viewGroup)

    fun update(term: Term, canUnregister: Boolean) {
        this.canUnregister = canUnregister
        checkedCourses.clear()

        // Get the courses asynchronously
        SQLite.select()
                .from(Course::class)
                .where(Course_Table.term.eq(term))
                .async()
                .queryListResultCallback { _, tResult ->
                    submitList(tResult)
                    showEmptyView(tResult.isEmpty())
                }
                .execute()
    }

    internal inner class CourseHolder(parent: ViewGroup) :
            BaseHolder<Course>(parent, R.layout.item_course) {

        override fun bind(position: Int, item: Course) {
            itemView.apply {
                code.text = item.code
                title.text = item.title
                type.text = item.type
                credits.text = context.getString(R.string.course_credits, item.credits.toString())
                days.text = DayUtils.getDayStrings(item.days)
                hours.text = item.timeString

                // Show the check box if the user can unregister
                checkBox.isVisible = canUnregister
                // Only set the view selectable if the user can unregister
                isClickable = canUnregister
                if (canUnregister) {
                    setOnClickListener { checkBox.isChecked = !checkBox.isChecked }

                    // Remove any other listeners
                    checkBox.setOnCheckedChangeListener(null)
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
    }

    class ItemCallback : DiffUtil.ItemCallback<Course>() {

        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem.term == newItem.term && oldItem.crn == newItem.crn

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean = oldItem == newItem
    }
}
