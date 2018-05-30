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

package com.guerinet.mymartlet.ui.dialog.list

import android.content.Context
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.suitcase.dialog.SingleListInterface
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite

/**
 * Displays a list of place types to choose from in the maps section
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param context           App context
 * @param currentCategory   Currently selected category
 */
abstract class CategoriesAdapter(context: Context, currentCategory: Category) :
        SingleListInterface {

    /**
     * List of place types with their associated String
     */
    private val categories: List<Pair<Category, String>> by lazy {
        val list = mutableListOf<Pair<Category, String>>()

        // Get the categories synchronously from the DB
        val categories = SQLite.select()
                .from(Category::class)
                .queryList()
                .map { Pair(it, it.getString(context)) }
                .sortedWith(Comparator { o1, o2 -> o1.second.compareTo(o2.second, true) })
        list.addAll(categories)

        // Add the favorites option
        var type = Category(true)
        list.add(0, Pair(type, type.getString(context)))

        // Add the All option
        type = Category(false)
        list.add(0, Pair(type, type.getString(context)))

        list
    }

    override var currentChoice = categories.indexOfFirst { it.first == currentCategory }

    override val choices: Array<String> by lazy { categories.map { it.second }.toTypedArray() }

    override fun onChoiceSelected(position: Int) = onCategorySelected(categories[position].first)

    /**
     * Called when a [category] is selected
     */
    abstract fun onCategorySelected(category: Category)
}
