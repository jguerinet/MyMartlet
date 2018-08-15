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
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.prefs.RegisterTermsPref
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.dialog.singleListDialog
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.sql.language.SQLite
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Displays a list of terms
 * @author Julien Guerinet
 * @since 2.0.0
 */
class TermDialogHelper(context: Context, currentTerm: Term?, registration: Boolean,
        onTermSelected: ((Term) -> Unit)) : KoinComponent {

    private val ga by inject<GAManager>()

    private val defaultTermPref by inject<DefaultTermPref>()

    private val registerTermsPref by inject<RegisterTermsPref>()

    private val terms by lazy {
        if (!registration) {
            // We are using the user's existing terms
            SQLite.select()
                    .from(Semester::class)
                    .queryList()
                    .map { it.term }
        } else {
            // We are using the registration terms
            registerTermsPref.terms.toList()
        }
                .sortedWith(kotlin.Comparator { o1, o2 -> if (o1.isAfter(o2)) -1 else 1 })
                .map { Pair(it, it.getString(context)) }
    }

    init {
        ga.sendScreen("Change Semester")

        terms.map { it.second }.toTypedArray()

        val term = currentTerm ?: defaultTermPref.getTerm()

        // Use the default currentTerm if no currentTerm was sent
        val currentChoice = terms.indexOfFirst { it.first == term }

        val choices = terms.map { it.second }.toTypedArray()

        context.singleListDialog(choices, R.string.title_change_semester, currentChoice) {
            onTermSelected(terms[it].first)
        }
    }
}
