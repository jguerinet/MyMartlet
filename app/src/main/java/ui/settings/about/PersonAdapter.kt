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

package com.guerinet.mymartlet.ui.settings.about

import android.content.Intent
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.suitcase.analytics.Analytics
import com.guerinet.suitcase.ui.BaseRecyclerViewAdapter
import com.guerinet.suitcase.ui.extensions.setPaddingId
import com.guerinet.suitcase.ui.extensions.setTextSizeId
import com.guerinet.suitcase.util.extensions.openUrl
import com.squareup.picasso.Picasso
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Displays the developer in the About page
 * @author Julien Guerinet
 * @since 1.0.0
 */
class PersonAdapter : BaseRecyclerViewAdapter(), KoinComponent {

    private val analytics by inject<Analytics>()

    private val items: List<Any> by lazy {
        val (currentContributors, pastContributors) = Person.values().partition { it.isCurrent }
        mutableListOf<Any>().apply {
            add(R.string.contributors_current)
            addAll(currentContributors)
            add(R.string.contributors_past)
            addAll(pastContributors)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseRecyclerViewAdapter.BaseHolder {
        if (viewType == VIEW_TYPE_PERSON) {
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

    override fun getItemViewType(position: Int): Int =
        if (items[position] is Person) VIEW_TYPE_PERSON else -1

    override fun getItemCount(): Int = items.size

    /**
     * Header in the list
     */
    internal inner class HeaderHolder(private val view: TextView) :
        BaseRecyclerViewAdapter.BaseHolder(view) {

        override fun bind(position: Int) {
            val title = items[position] as? Int ?: error("Item at position $position not an Int")
            view.setText(title)
        }
    }

    /**
     * Person item in the list
     */
    internal inner class PersonHolder(parent: ViewGroup) :
        BaseRecyclerViewAdapter.BaseHolder(parent, R.layout.item_person) {

        private val name by itemView.getView<TextView>(R.id.name)
        private val picture by itemView.getView<ImageView>(R.id.picture)
        private val linkedIn by itemView.getView<Button>(R.id.linkedIn)
        private val email by itemView.getView<Button>(R.id.email)

        override fun bind(position: Int) {
            val person = items[position] as? Person
                ?: error("Item at position $position not a Person")

            itemView.apply {
                // Name
                name.setText(person.nameRes)

                // Picture
                Picasso.get()
                    .load(person.pictureRes)
                    .into(picture)

                // LinkedIn
                linkedIn.setOnClickListener {
                    analytics.event("about_linkedin", "person" to person.name)

                    context.openUrl(context.getString(person.linkedInRes))
                }

                // Email
                email.setOnClickListener {
                    analytics.event("about_email", "person" to person.name)

                    // Send an email
                    val intent = Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(person.emailRes)))
                        .setType("message/rfc822")
                    context.startActivity(Intent.createChooser(intent, null))
                }
            }
        }
    }

    private enum class Person(
        @StringRes val nameRes: Int,
        @DrawableRes val pictureRes: Int,
        @StringRes val emailRes: Int,
        @StringRes val linkedInRes: Int,
        val isCurrent: Boolean = false
    ) {
        JULIEN(
            R.string.about_julien,
            R.drawable.about_julien,
            R.string.about_julien_email,
            R.string.about_julien_linkedin,
            true
        ),
        ADNAN(
            R.string.about_adnan,
            R.drawable.about_adnan,
            R.string.about_adnan_email,
            R.string.about_adnan_linkedin
        ),
        HERNAN(
            R.string.about_hernan,
            R.drawable.about_hernan,
            R.string.about_hernan_email,
            R.string.about_hernan_linkedin
        ),
        JOSHUA(
            R.string.about_joshua,
            R.drawable.about_joshua,
            R.string.about_joshua_email,
            R.string.about_joshua_linkedin
        ),
        JULIA(
            R.string.about_julia,
            R.drawable.about_julia,
            R.string.about_julia_email,
            R.string.about_julia_linkedin
        ),
        QUANG(
            R.string.about_quang,
            R.drawable.about_quang,
            R.string.about_quang_email,
            R.string.about_quang_linkedin
        ),
        RYAN(
            R.string.about_ryan,
            R.drawable.about_ryan,
            R.string.about_ryan_email,
            R.string.about_ryan_linkedin
        ),
        SELIM(
            R.string.about_selim,
            R.drawable.about_selim,
            R.string.about_selim_email,
            R.string.about_selim_linkedin
        ),
        SHABBIR(
            R.string.about_shabbir,
            R.drawable.about_shabbir,
            R.string.about_shabbir_email,
            R.string.about_shabbir_linkedin
        ),
        XAVIER(
            R.string.about_xavier,
            R.drawable.about_xavier,
            R.string.about_xavier_email,
            R.string.about_xavier_linkedin
        ),
        YULRIC(
            R.string.about_yulric,
            R.drawable.about_yulric,
            R.string.about_yulric_email,
            R.string.about_yulric_linkedin
        )
    }

    companion object {

        private const val VIEW_TYPE_PERSON = 0
    }
}
