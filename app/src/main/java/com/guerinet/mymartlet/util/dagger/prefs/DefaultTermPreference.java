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

package com.guerinet.mymartlet.util.dagger.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.guerinet.mymartlet.model.Term;
import com.guerinet.suitcase.prefs.StringPref;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Stores the user's default {@link Term} in the Shared Preferences
 * @author Julien Guerinet
 * @since 2.4.0
 */
@Singleton
public class DefaultTermPreference extends StringPref {
    /**
     * Default {@link Term}
     */
    private Term defaultTerm;

    /**
     * Default Injectable Constructor
     *
     * @param prefs SharedPreferences instance
     */
    @Inject
    DefaultTermPreference(@NonNull SharedPreferences prefs) {
        super(prefs, "default_term", null);
    }

    /**
     * @param term {@link Term} to save
     */
    public void setTerm(Term term) {
        defaultTerm = term;
        set(term.getId());
    }

    /**
     * @return Stored default {@link Term}, the current term if none stored
     */
    @NonNull
    public Term getTerm() {
        if (defaultTerm == null) {
            String term = get();
            if (term == null) {
                // If there is no default term, use today
                defaultTerm = Term.currentTerm();
            } else {
                defaultTerm = Term.parseTerm(term);
            }
        }
        return defaultTerm;
    }

    @Override
    public void clear() {
        super.clear();
        // Clear the instance
        defaultTerm = null;
    }
}
