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
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guerinet.mymartlet.R
import java.util.Locale

/**
 * A type of place that the user can filter by
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @property id Category Id
 * @property en Category name in English
 * @property fr Category name in French
 */
@Entity
class Category() {

    @PrimaryKey
    var id: Int = 0

    var en: String = ""

    var fr: String = ""

    /** Localized category name */
    val name: String
        get() = if (Locale.getDefault().language == "fr") fr else en

    /**
     * Constructor used to create the Favorites (is [isFavorites] is true) and All types.
     *  Uses the app [context] to retrieve the necessary Strings
     */
    constructor(isFavorites: Boolean, context: Context) : this() {
        id = if (isFavorites) FAVORITES else ALL
        // Set the same translation for both languages, as this get regenerated every time the map is opened anyway
        val stringId = if (isFavorites) R.string.map_favorites else R.string.map_all
        en = context.getString(stringId)
        fr = context.getString(stringId)
    }

    /**
     * Returns the String to use. Uses the app [context]
     */
    fun getString(context: Context): String = when {
        id == FAVORITES -> context.getString(R.string.map_favorites)
        id == ALL -> context.getString(R.string.map_all)
        Locale.getDefault().language == "fr" -> fr
        else -> en
    }

    companion object {

        /** User's saved favorite places */
        const val FAVORITES = -2

        /** All of the places */
        internal const val ALL = -1
    }
}
