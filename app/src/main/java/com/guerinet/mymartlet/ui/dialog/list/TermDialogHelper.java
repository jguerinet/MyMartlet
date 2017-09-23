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

package com.guerinet.mymartlet.ui.dialog.list;

import android.content.Context;

import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.model.Semester;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.util.dagger.prefs.DefaultTermPref;
import com.guerinet.mymartlet.util.dagger.prefs.RegisterTermsPref;
import com.guerinet.suitcase.analytics.GAManager;
import com.guerinet.suitcase.dialog.SingleListInterface;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * {@link SingleListInterface} implementation for a list of terms
 * @author Julien Guerinet
 * @since 2.0.0
 */
public abstract class TermDialogHelper implements SingleListInterface {
    /**
     * App context
     */
    @Inject
    protected Context context;

    @Inject
    protected GAManager ga;

    @Inject
    DefaultTermPref defaultTermPref;

    @Inject
    RegisterTermsPref registerTermsPref;
    /**
     * List of {@link Term}s to choose from
     */
    private final List<Term> terms;
    /**
     * Currently selected {@link Term}
     */
    private final Term currentTerm;

    /**
     * Default Constructor
     *
     * @param context App context
     */
    public TermDialogHelper(Context context, Term currentTerm, boolean registration) {
        App.Companion.component(context).inject(this);

        ga.sendScreen("Change Semester");

        //Use the default currentTerm if no currentTerm was sent
        this.currentTerm = currentTerm == null ? defaultTermPref.getTerm() : currentTerm;

        terms = new ArrayList<>();
        if (!registration) {
            List<Semester> semesters = SQLite.select()
                    .from(Semester.class)
                    .queryList();

            // We are using the user's existing terms
            for (Semester semester : semesters) {
                terms.add(semester.getTerm());
            }
        } else {
            // We are using the registration terms
            terms.addAll(registerTermsPref.getTerms());
        }

        //Sort them chronologically
        Collections.sort(terms, new Comparator<Term>() {
            @Override
            public int compare(Term term, Term term2){
                return term.isAfter(term2) ? -1 : 1;
            }
        });
    }

    @Override
    public int getCurrentChoice() {
        return terms.indexOf(currentTerm);
    }

    @Override
    public String[] getChoices() {
        String[] titles = new String[terms.size()];
        for (int i = 0; i < terms.size(); i ++) {
            titles[i] = terms.get(i).getString(context);
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
        onTermSelected(terms.get(position));
    }

    /**
     * Called when a {@link Term} has been selected
     *
     * @param term The selected {@link Term}
     */
    public abstract void onTermSelected(Term term);
}
