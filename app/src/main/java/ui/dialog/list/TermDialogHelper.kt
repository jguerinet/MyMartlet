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

package com.guerinet.mymartlet.ui.dialog.list

import android.content.Context
import android.util.Pair
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.prefs.DefaultTermPref
import com.guerinet.mymartlet.util.room.daos.SemesterDao
import com.guerinet.suitcase.dialog.singleListDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

/**
 * Displays a list of terms
 * @author Julien Guerinet
 * @since 2.0.0
 */
class TermDialogHelper(
    context: Context,
    mainScope: CoroutineScope,
    currentTerm: Term?,
    registrationTerms: List<Term>? = null,
    onTermSelected: ((Term) -> Unit)
) : KoinComponent {

    private val defaultTermPref by inject<DefaultTermPref>()

    private val semesterDao by inject<SemesterDao>()

    init {
        mainScope.launch(Dispatchers.Default) {
            val terms = (registrationTerms ?: semesterDao.getSemesters().map { it.term })
                .sortedWith(kotlin.Comparator { o1, o2 -> if (o1.isAfter(o2)) -1 else 1 })
                .map { Pair(it, it.getString(context)) }

            terms.map { it.second }.toTypedArray()

            val term = currentTerm ?: defaultTermPref.term

            // Use the default currentTerm if no currentTerm was sent
            val currentChoice = terms.indexOfFirst { it.first == term }

            val choices = terms.map { it.second }.toTypedArray()

            withContext(Dispatchers.Main) {
                context.singleListDialog(choices, R.string.title_change_semester, currentChoice) {
                    onTermSelected(terms[it].first)
                }
            }
        }
    }
}
