/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Term;

/**
 * Used for the term spinners
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class TermAdapter extends BaseAdapter implements SpinnerAdapter{
    /**
     * The list of terms
     */
    private List<Term> mTerms;

    /**
     * Default Constructor
     *
     * @param terms The list of terms
     */
    public TermAdapter(List<Term> terms){
        this.mTerms = terms;

        //Sort them chronologically
        Collections.sort(mTerms, new Comparator<Term>() {
            @Override
            public int compare(Term term, Term term2){
                return term.isAfter(term2) ? -1 : 1;
            }
        });
    }

    @Override
    public int getCount() {
        return mTerms.size();
    }

    @Override
    public Term getItem(int i) {
        return mTerms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.spinner_item, viewGroup, false);
        }

        //Semester name
        ((TextView)view).setText(getItem(position).getString(viewGroup.getContext()));

        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup){
        if(view == null){
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.spinner_dropdown, viewGroup, false);
        }

        //Semester name
        ((TextView)view).setText(getItem(position).getString(viewGroup.getContext()));

        return view;
    }
}
