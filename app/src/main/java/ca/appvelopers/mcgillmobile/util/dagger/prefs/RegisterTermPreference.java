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

import com.guerinet.utils.prefs.StringPreference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.Term;

/**
 * Stores and loads the list of {@link Term} a user can currently register in
 * @author Julien Guerinet
 * @since 2.4.0
 */
@Singleton
public class RegisterTermPreference extends StringPreference {
    /**
     * Instance list of the register {@link Term}s
     */
    private List<Term> registerTerms;

    /**
     * Default Injectable Constructor
     *
     * @param prefs {@link SharedPreferences} instance
     */
    @Inject
    RegisterTermPreference(SharedPreferences prefs) {
        super(prefs, "register_terms", "");
    }

    /**
     * @return List of {@link Term}s a user can register for
     */
    public List<Term> getTerms() {
        if (registerTerms == null) {
            registerTerms = new ArrayList<>();
            String[] termsStrings = get().split(",");
            for (String term : termsStrings) {
                registerTerms.add(Term.parseTerm(term));
            }
        }
        return registerTerms;
    }

    /**
     * @param terms List of {@link Term}s to set
     */
    public void setTerms(List<Term> terms) {
        if (terms == null) {
            terms = new ArrayList<>();
        }

        // Set the instance
        registerTerms = terms;

        // Create the String to store in the SharedPrefs
        String termsString = "";
        for (int i = 0; i < terms.size(); i ++) {
            termsString += terms.get(i).getId();

            // Add comma if this isn't the last item
            if (i != terms.size() - 1) {
                termsString += ",";
            }
        }
        set(termsString);
    }

    @Override
    public void clear() {
        super.clear();
        // Clear the instance list
        getTerms().clear();
    }
}
