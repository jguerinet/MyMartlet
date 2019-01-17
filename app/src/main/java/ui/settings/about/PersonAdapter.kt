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

package com.guerinet.mymartlet.ui.settings.about

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.guerinet.mymartlet.R
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.ui.BaseRecyclerViewAdapter
import com.guerinet.suitcase.ui.extensions.setPaddingId
import com.guerinet.suitcase.ui.extensions.setTextSizeId
import com.guerinet.suitcase.util.extensions.getResourceId
import com.guerinet.suitcase.util.extensions.openUrl
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_person.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Displays the developer in the About page
 * @author Julien Guerinet
 * @since 1.0.0
 */
class PersonAdapter : BaseRecyclerViewAdapter(), KoinComponent {

    private val ga by inject<GAManager>()

    private val items = listOf(
        R.string.contributors_current,
        "julien",
        R.string.contributors_past,
        "adnan",
        "hernan",
        "josh",
        "julia",
        "quang",
        "ryan",
        "selim",
        "shabbir",
        "xavier",
        "yulric"
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewAdapter.BaseHolder {
        if (viewType == PERSON) {
            return PersonHolder(parent)
        }

        // Prepare the header view
        val textView = TextView(parent.context).apply {
            setTypeface(null, Typeface.BOLD)
            setTextSizeId(R.dimen.text_large)
            setPaddingId(R.dimen.padding_small)
        }
        return HeaderHolder(textView)
    }

    override fun getItemViewType(position: Int): Int = if (items[position] is String) PERSON else -1

    override fun getItemCount(): Int = items.size

    /**
     * Header in the list
     */
    internal inner class HeaderHolder(private val view: TextView) :
        BaseRecyclerViewAdapter.BaseHolder(view) {

        override fun bind(position: Int) {
            view.setText(items[position] as Int)
        }
    }

    /**
     * Person item in the list
     */
    internal inner class PersonHolder(parent: ViewGroup) :
        BaseRecyclerViewAdapter.BaseHolder(parent, R.layout.item_person) {

        override fun bind(position: Int) {
            val personName = items[position] as? String ?: return

            val idPrefix = "about_$personName"

            itemView.apply {
                // Name
                val nameId = getResourceId(idPrefix)
                if (nameId != 0) {
                    name.setText(nameId)
                } else {
                    name.text = ""
                }

                // Picture
                val pictureId = getResourceId(idPrefix, "drawable")
                if (pictureId != 0) {
                    Picasso.get()
                        .load(pictureId)
                        .into(picture)
                } else {
                    picture.visibility = View.GONE
                }

                // Role
                val roleId = getResourceId(idPrefix + "_role")
                if (roleId != 0) {
                    role.setText(roleId)
                } else {
                    role.text = ""
                }

                // LinkedIn
                val linkedInId = getResourceId(idPrefix + "_linkedin")
                if (linkedInId != 0) {
                    linkedIn.setOnClickListener {
                        ga.sendEvent("About", "Linkedin", personName)
                        context.openUrl(context.getString(linkedInId))
                    }
                } else {
                    linkedIn.visibility = View.GONE
                }

                // Email
                val emailId = getResourceId(idPrefix + "_email")
                if (emailId != 0) {
                    email.setOnClickListener {
                        ga.sendEvent("About", "Email", personName)

                        // Send an email
                        val intent = Intent(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(emailId)))
                            .setType("message/rfc822")
                        context.startActivity(Intent.createChooser(intent, null))
                    }
                } else {
                    email.visibility = View.GONE
                }
            }
        }

        /**
         * Returns the resource Id for the [stringId] and the [resourceType] (defaults to String),
         *  0 if none found
         */
        private fun getResourceId(stringId: String, resourceType: String = "string"): Int =
            itemView.context.getResourceId(resourceType, stringId)
    }

    companion object {

        /**
         * Person view type
         */
        private const val PERSON = 0
    }
}