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
import android.util.Pair
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.dagger.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.dagger.prefs.RegisterTermsPref
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.dialog.SingleListInterface
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * [SingleListInterface] implementation for a list of terms
 * @author Julien Guerinet
 * @since 2.0.0
 */
abstract class TermDialogHelper(context: Context, currentTerm: Term?, registration: Boolean) :
        SingleListInterface, KoinComponent {

    private val ga by inject<GAManager>()

    private val defaultTermPref by inject<DefaultTermPref>()

    private val registerTermsPref by inject<RegisterTermsPref>()

    private val terms by lazy {
        val terms = if (!registration) {
            // We are using the user's existing terms
            SQLite.select()
                    .from(Semester::class)
                    .queryList()
                    .map { it.term }
        } else {
            // We are using the registration terms
            registerTermsPref.terms.toList()
        }
        terms.sortedWith(kotlin.Comparator { o1, o2 -> if (o1.isAfter(o2)) -1 else 1 })
                .map { Pair(it, it.getString(context)) }
    }

    private val currentTerm: Term

    override val currentChoice: Int
        get() = terms.indexOfFirst { it.first == currentTerm }

    override val choices: Array<String>
        get() = terms.map { it.second }.toTypedArray()

    init {
        ga.sendScreen("Change Semester")

        // Use the default currentTerm if no currentTerm was sent
        this.currentTerm = currentTerm ?: defaultTermPref.getTerm()
    }

    override fun onChoiceSelected(position: Int) {
        onTermSelected(terms[position].first)
    }

    /**
     * Called when a [term] has been selected
     */
    abstract fun onTermSelected(term: Term)
}
