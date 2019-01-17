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
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.guerinet.mymartlet.R
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.ui.BaseRecyclerViewAdapter
import com.guerinet.suitcase.ui.extensions.setPaddingId
import com.guerinet.suitcase.ui.extensions.setTextSizeId
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
        Person.JULIEN,
        R.string.contributors_past,
        Person.ADNAN,
        Person.HERNAN,
        Person.JOSHUA,
        Person.JULIA,
        Person.QUANG,
        Person.RYAN,
        Person.SELIM,
        Person.SHABBIR,
        Person.XAVIER,
        Person.YULRIC
    )

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
            val title = items[position] as? Int ?: return
            view.setText(title)
        }
    }

    /**
     * Person item in the list
     */
    internal inner class PersonHolder(parent: ViewGroup) :
        BaseRecyclerViewAdapter.BaseHolder(parent, R.layout.item_person) {

        override fun bind(position: Int) {
            val person = items[position] as? Person ?: return

            itemView.apply {
                // Name
                name.setText(person.nameId)

                // Picture
                Picasso.get()
                    .load(person.pictureId)
                    .into(picture)

                // LinkedIn
                linkedIn.setOnClickListener {
                    ga.sendEvent("About", "Linkedin", person.name)
                    context.openUrl(context.getString(person.linkedInId))
                }

                // Email
                email.setOnClickListener {
                    ga.sendEvent("About", "Email", person.name)

                    // Send an email
                    val intent = Intent(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(person.emailId)))
                        .setType("message/rfc822")
                    context.startActivity(Intent.createChooser(intent, null))
                }
            }
        }
    }

    private enum class Person(
        @StringRes val nameId: Int,
        @DrawableRes val pictureId: Int,
        @StringRes val emailId: Int,
        @StringRes val linkedInId: Int
    ) {
        JULIEN(
            R.string.about_julien,
            R.drawable.about_julien,
            R.string.about_julien_email,
            R.string.about_julien_linkedin
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