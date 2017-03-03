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

package ca.appvelopers.mcgillmobile.util.dagger.prefs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.guerinet.utils.prefs.StringPreference;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.Term;

/**
 * Stores the user's default {@link Term} in the Shared Preferences
 * @author Julien Guerinet
 * @since 2.4.0
 */
@Singleton
public class DefaultTermPreference extends StringPreference {
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
        set(term.getId());
    }

    /**
     * @return Stored default {@link Term}, the current term if none stored
     */
    public Term getTerm() {
        String term = get();
        if (term == null) {
            // If there is no default term, use today
            return Term.currentTerm();
        }
        return Term.parseTerm(term);
    }
}
