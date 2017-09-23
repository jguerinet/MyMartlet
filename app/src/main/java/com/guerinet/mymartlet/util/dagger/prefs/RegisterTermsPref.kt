/*
 * Copyright 2014-2017 Julien Guerinet
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
import com.guerinet.suitcase.prefs.StringPref
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores and loads the list of [Term]s a user can currently register in
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
class RegisterTermsPref @Inject constructor(prefs: SharedPreferences) :
        StringPref(prefs, "register_terms", "") {

    var terms: MutableList<Term> = get()!!.split(",").map { Term.parseTerm(it) }.toMutableList()
        set(value) {
            field = value
            set(value.joinToString { term -> term.id })
        }

    override fun clear() {
        super.clear()
        terms.clear()
    }
}