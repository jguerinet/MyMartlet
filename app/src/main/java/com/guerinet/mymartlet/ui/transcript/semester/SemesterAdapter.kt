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

package com.guerinet.mymartlet.ui.transcript.semester

import android.support.v7.util.DiffUtil
import android.view.ViewGroup
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.transcript.TranscriptCourse
import com.guerinet.mymartlet.model.transcript.TranscriptCourse_Table
import com.guerinet.suitcase.ui.BaseListAdapter
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.item_transcript_course.view.*

/**
 * Displays the list of courses for a semester
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param semesterId Id of the [Semester] to show
 */
internal class SemesterAdapter(private val semesterId: Int) :
        BaseListAdapter<TranscriptCourse>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseHolder =
            CourseHolder(parent)

    fun update() {
        SQLite.select()
                .from(TranscriptCourse::class)
                .where(TranscriptCourse_Table.semesterId.eq(semesterId))
                .async()
                .queryListResultCallback { _, tResult ->
                    submitList(tResult)
                }
                .execute()
    }

    internal class CourseHolder(parent: ViewGroup) :
            BaseHolder<TranscriptCourse>(parent, R.layout.item_transcript_course) {

        override fun bind(position: Int, item: TranscriptCourse) {
            itemView.apply {
                code.text = item.courseCode
                grade.text = item.userGrade
                title.text = item.courseTitle
                credits.text = context.getString(R.string.course_credits, item.credits.toString())

                // Don't display average if it does not exist
                if (item.averageGrade.isNotBlank()) {
                    average.text = context.getString(R.string.course_average, item.averageGrade)
                }
            }
        }
    }

    class ItemCallback : DiffUtil.ItemCallback<TranscriptCourse>() {

        override fun areItemsTheSame(oldItem: TranscriptCourse, newItem: TranscriptCourse)
                : Boolean = oldItem.courseCode == newItem.courseCode && oldItem.term == newItem.term

        override fun areContentsTheSame(oldItem: TranscriptCourse, newItem: TranscriptCourse)
                : Boolean = oldItem == newItem
    }
}
