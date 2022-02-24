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

package com.guerinet.mymartlet.ui.ebill

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Statement
import com.guerinet.suitcase.date.android.extensions.getLongDateString
import com.guerinet.suitcase.ui.BaseListAdapter
import com.guerinet.suitcase.util.extensions.getColorCompat

/**
 * Adapter used for the ebill page
 * @author Julien Guerinet
 * @since 1.0.0
 */
internal class EbillAdapter : BaseListAdapter<Statement>(ItemCallback()) {

    /**
     * Updates the list of [Statement]s shown
     */
    fun update(statements: List<Statement>?) = submitList(statements?.toMutableList())

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) = StatementHolder(viewGroup)

    internal class StatementHolder(parent: ViewGroup) :
        BaseHolder<Statement>(parent, R.layout.item_statement) {

        private val date by lazy<TextView> { itemView.findViewById(R.id.date) }
        private val dueDate by lazy<TextView> { itemView.findViewById(R.id.dueDate) }
        private val amount by lazy<TextView> { itemView.findViewById(R.id.amount) }

        @SuppressLint("SetTextI18n")
        override fun bind(position: Int, item: Statement) {
            itemView.apply {
                date.text = context.getString(
                    R.string.ebill_statement_date,
                    item.date.getLongDateString()
                )

                dueDate.text = context.getString(
                    R.string.ebill_due_date,
                    item.dueDate.getLongDateString()
                )

                amount.text = "$${item.amount}"

                // Set the color based on if the user owes money
                val colorId = when {
                    item.amount > 0 -> R.color.red
                    item.amount < 0 -> R.color.green
                    else -> android.R.color.black
                }

                amount.setTextColor(context.getColorCompat(colorId))
            }
        }
    }

    internal class ItemCallback : DiffUtil.ItemCallback<Statement>() {

        // Note: we check some of the data and not the Id here because the Id is auto-generated
        override fun areItemsTheSame(oldItem: Statement, newItem: Statement): Boolean =
            oldItem.date == newItem.date && oldItem.amount == newItem.amount

        override fun areContentsTheSame(oldItem: Statement, newItem: Statement): Boolean =
            oldItem == newItem
    }
}
