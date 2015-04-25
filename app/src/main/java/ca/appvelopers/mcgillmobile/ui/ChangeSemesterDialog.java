/*
 * Copyright 2014-2015 Appvelopers
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemSelected;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.util.Analytics;

/**
 * Allows the user to change their currently viewed semester in some sections of the app
 * @author Joshua David Alfaro
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class ChangeSemesterDialog extends AlertDialog {
    /**
     * The current term
     */
    private Term mTerm;
    /**
     * True if we should show the terms that the user can currently register in, false otherwise
     */
    private boolean mRegisterTerms;
    /**
     * The checkbox to set the default term
     */
    @InjectView(R.id.change_semester_default)
    private CheckBox mDefaultCheckbox;
    /**
     * The spinner used to choose a term
     */
    @InjectView(R.id.change_semester_term)
    private Spinner mTermSpinner;
    /**
     * The adapter used for the term spinner
     */
    private TermAdapter mAdapter;

    public ChangeSemesterDialog(Context context, Term term, boolean registerTerms){
        super(context);
        this.mTerm = term;
        this.mRegisterTerms = registerTerms;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_semester);
        ButterKnife.inject(this);
        Analytics.getInstance().sendScreen("Change Semester");

        //Don't show the checkbox if we are doing the register terms only
        if(mRegisterTerms){
            mDefaultCheckbox.setVisibility(View.GONE);
        }

        //Use the default term if no term was sent
        if(mTerm == null){
            mTerm = App.getDefaultTerm();
        }

        List<Term> terms = new ArrayList<>();
        //We are using the user's existing terms
        if(!mRegisterTerms){
            for (Semester semester : App.getTranscript().getSemesters()) {
                terms.add(semester.getTerm());
            }
        }
        //We are using the registration terms
        else{
            terms.addAll(App.getRegisterTerms());
        }

        //Set up the spinner
        mAdapter = new TermAdapter(terms);
        mTermSpinner.setAdapter(mAdapter);
        mTermSpinner.setSelection(terms.indexOf(mTerm));

        //Set up some other parts of the dialog
        setCancelable(false);
        setCustomTitle(View.inflate(getContext(), R.layout.dialog_change_semester_title, null));
        setButton(DialogInterface.BUTTON_POSITIVE, getContext().getString(android.R.string.ok),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        //Check if the default checkbox is checked
                        if(mDefaultCheckbox.isChecked()){
                            //Store this semester as the default semester if it is
                            App.setDefaultTerm(mTerm);
                        }

                        dialog.dismiss();
                    }
                });
        setButton(DialogInterface.BUTTON_NEGATIVE, getContext().getString(android.R.string.cancel),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        //Dismiss with the term set to null
                        mTerm = null;
                        dialog.dismiss();
                    }
                });
    }

    @OnItemSelected(R.id.change_semester_term)
    void termSelected(int position){
        mTerm = mAdapter.getItem(position);
    }

    /**
     * @return The term selected, if any
     */
    public Term getTerm(){
        return mTerm;
    }
}