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
class PersonAdapter internal constructor() : BaseRecyclerViewAdapter(), KoinComponent {

    private val ga by inject<GAManager>()

    private val items = mutableListOf<Any>()

    init {
        // Current Contributors
        items.add(R.string.contributors_current)

        // Julien
        items.add(Person(R.string.about_julien, R.drawable.about_julien, R.string.about_julien_role,
                R.string.about_julien_email, R.string.about_julien_linkedin))

        // Past Contributors
        items.add(R.string.contributors_past)

        // Adnan
        items.add(Person(R.string.about_adnan, R.drawable.about_adnan, R.string.about_adnan_role,
                R.string.about_adnan_email, R.string.about_adnan_linkedin))

        // Hernan
        items.add(Person(R.string.about_hernan, R.drawable.about_hernan, R.string.about_hernan_role,
                R.string.about_hernan_email, R.string.about_hernan_linkedin))

        // Josh
        items.add(Person(R.string.about_joshua, R.drawable.about_josh, R.string.about_joshua_role,
                R.string.about_joshua_email, R.string.about_joshua_linkedin))

        // Julia
        items.add(Person(R.string.about_julia, R.drawable.about_julia, R.string.about_julia_role,
                R.string.about_julia_email, R.string.about_julia_linkedin))

        // Quang
        items.add(Person(R.string.about_quang, R.drawable.about_quang, R.string.about_quang_role,
                R.string.about_quang_email, R.string.about_quang_linkedin))

        // Ryan
        items.add(Person(R.string.about_ryan, R.drawable.about_ryan, R.string.about_ryan_role,
                R.string.about_ryan_email, R.string.about_ryan_linkedin))

        // Selim
        items.add(Person(R.string.about_selim, R.drawable.about_selim, R.string.about_selim_role,
                R.string.about_selim_email, R.string.about_selim_linkedin))

        // Shabbir
        items.add(Person(R.string.about_shabbir, R.drawable.about_shabbir,
                R.string.about_shabbir_role, R.string.about_shabbir_email,
                R.string.about_shabbir_linkedin))

        // Xavier
        items.add(Person(R.string.about_xavier, R.drawable.about_xavier, R.string.about_xavier_role,
                R.string.about_xavier_email, R.string.about_xavier_linkedin))

        // Yulric
        items.add(Person(R.string.about_yulric, R.drawable.about_yulric, R.string.about_yulric_role,
                R.string.about_yulric_email, R.string.about_yulric_linkedin))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewAdapter.BaseHolder {
        if (viewType == PERSON) {
            return PersonHolder(parent)
        }
        val textView = TextView(parent.context).apply {
            setTypeface(null, Typeface.BOLD)
            setTextSizeId(R.dimen.text_large)
            setPaddingId(R.dimen.padding_small)
        }
        return HeaderHolder(textView)
    }

    override fun getItemViewType(position: Int): Int = if (items[position] is Person) PERSON else -1

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
            val person = items[position] as Person

            itemView.apply {
                name.setText(person.name)

                Picasso.get()
                        .load(person.pictureId)
                        .into(picture)

                role.setText(person.role)

                linkedIn.setOnClickListener {
                    ga.sendEvent("About", "Linkedin", context.getString(person.name))
                    context.openUrl(context.getString(person.linkedIn))
                }

                email.setOnClickListener {
                    ga.sendEvent("About", "Email", context.getString(person.name))

                    // Send an email
                    val intent = Intent(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(person.email)))
                            .setType("message/rfc822")
                    context.startActivity(Intent.createChooser(intent, null))
                }
            }
        }
    }

    companion object {

        /**
         * Person view type
         */
        private const val PERSON = 0
    }
}

/**
 * One person for the About page
 * @author Julien Guerinet
 * @since 2.0.0
 *
 * @param name        Person's name
 * @param pictureId   Person's picture
 * @param role        Person's role
 * @param email       Person's email
 * @param linkedIn    URL to the person's LinkedIn
 */
private class Person(
        @StringRes val name: Int,
        @DrawableRes val pictureId: Int,
        @StringRes val role: Int,
        @StringRes val email: Int,
        @StringRes val linkedIn: Int)