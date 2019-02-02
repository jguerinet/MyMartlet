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

package com.guerinet.mymartlet.model.place

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guerinet.mymartlet.R
import java.util.*

/**
 * A type of place that the user can filter by
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @property id    Category Id
 * @property en    Category name in English
 * @property fr    Category name in French
 */
@Entity
data class Category(
    @PrimaryKey var id: Int = 0,
    var en: String,
    var fr: String
) {

    /**
     * Constructor used to create the Favorites (is [isFavorites] is true) and All types
     */
    constructor(isFavorites: Boolean) : this(if (isFavorites) FAVORITES else ALL, "", "")

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
