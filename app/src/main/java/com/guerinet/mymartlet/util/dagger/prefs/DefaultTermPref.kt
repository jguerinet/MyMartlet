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

package com.guerinet.mymartlet.util.dagger.prefs

import android.content.SharedPreferences
import com.guerinet.mymartlet.model.Term
import com.guerinet.suitcase.prefs.NullStringPref
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores the user's default [Term] in the [SharedPreferences]
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
class DefaultTermPref @Inject constructor(prefs: SharedPreferences):
        NullStringPref(prefs, "default_term", null) {

    private var currentTerm: Term? = null

    /**
     * Returns the stored default [Term], the current one if none stored
     */
    fun getTerm(): Term {
        if (currentTerm == null) {
            // Get the stored term
            val term = get()
            currentTerm = if (term == null) {
                // If there is no default term, use the current one
                Term.currentTerm()
            } else {
                Term.parseTerm(term)
            }
        }
        return currentTerm!!
    }

    fun setTerm(term: Term) {
        this.currentTerm = term
        set(term.id)
    }

    override fun clear() {
        super.clear()
        // Clear the instance
        currentTerm = null
    }
}