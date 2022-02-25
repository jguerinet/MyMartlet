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

package com.guerinet.mymartlet.ui.transcript

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.ui.transcript.semester.SemesterActivity
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.mymartlet.util.extensions.start
import com.guerinet.suitcase.ui.BaseListAdapter

/**
 * Populates the list of semesters on the transcript page
 * @author Julien Guerinet
 * @since 1.0.0
 */
internal class TranscriptAdapter : BaseListAdapter<Semester>(ItemCallback()) {

    /**
     * Updates the [list] of semesters shown
     */
    fun update(list: List<Semester>?) = submitList(list?.toMutableList())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SemesterHolder(parent)

    internal class SemesterHolder(parent: ViewGroup) :
        BaseHolder<Semester>(parent, R.layout.item_semester) {

        private val name by itemView.getView<TextView>(R.id.name)
        private val gpa by itemView.getView<TextView>(R.id.gpa)

        override fun bind(position: Int, item: Semester) {
            itemView.apply {
                name.text = item.getName(context)
                gpa.text = context.getString(R.string.transcript_termGPA, item.gpa.toString())

                setOnClickListener {
                    context.start<SemesterActivity>(Constants.ID to item.id)
                }
            }
        }
    }

    internal class ItemCallback : DiffUtil.ItemCallback<Semester>() {

        override fun areItemsTheSame(oldItem: Semester, newItem: Semester): Boolean =
            oldItem.term == newItem.term

        override fun areContentsTheSame(oldItem: Semester, newItem: Semester): Boolean =
            oldItem == newItem
    }
}
