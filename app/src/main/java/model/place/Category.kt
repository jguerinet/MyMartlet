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

package com.guerinet.mymartlet.model.place

import android.content.Context
import com.google.firebase.firestore.DocumentSnapshot
import com.guerinet.mymartlet.R
import timber.log.Timber
import java.util.Locale

/**
 * A type of place that the user can filter by
 * @author Julien Guerinet
 * @since 1.0.0
 */
data class Category(
    val id: Int,
    val en: String,
    val fr: String
) {

    /** Localized category name */
    val name: String
        get() = if (Locale.getDefault().language == "fr") fr else en

    /**
     * Constructor used to create the All category. Uses the app [context] to retrieve the necessary String
     *  Set the same translation for both languages, as this gets regenerated every time the map is opened anyway
     */
    constructor(context: Context) : this(ALL, context.getString(R.string.map_all), context.getString(R.string.map_all))

    companion object {

        /** All of the places */
        internal const val ALL = -1

        /**
         * Converts a Firestore [document] into a [Category] (null if error during the parsing)
         */
        fun fromDocument(document: DocumentSnapshot): Category? {
            val id = document.id.toInt()
            val en = document["en"] as? String
            val fr = document["fr"] as? String

            return if (en != null && fr != null) {
                Category(id, en, fr)
            } else {
                Timber.e(Exception("Category with id $id has null name. en: $en, fr: $fr"))
                null
            }
        }
    }
}
