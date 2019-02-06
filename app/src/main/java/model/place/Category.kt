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
import java.util.Locale

/**
 * A type of place that the user can filter by
 * @author Julien Guerinet
 * @since 1.0.0
 */
class Category() {

    var id: Int = ALL

    var en: String = ""

    var fr: String = ""

    /** Localized category name */
    val name: String
        get() = if (Locale.getDefault().language == "fr") fr else en

    /**
     * Constructor used to create the All category. Uses the app [context] to retrieve the necessary String
     */
    constructor(context: Context) : this() {
        id = ALL
        // Set the same translation for both languages, as this get regenerated every time the map is opened anyway
        en = context.getString(R.string.map_all)
        fr = en
    }

    companion object {

        /** All of the places */
        internal const val ALL = -1

        /**
         * Converts a Firestore [document] into a [Category] (null if error during the parsing)
         */
        fun fromDocument(document: DocumentSnapshot): Category? = document.toObject(Category::class.java)?.apply {
            id = document.id.toInt()
        }
    }
}
