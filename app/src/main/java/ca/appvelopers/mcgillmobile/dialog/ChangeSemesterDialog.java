package ca.appvelopers.mcgillmobile.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Semester;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.view.TermAdapter;

/**
 * Author: Julien Guerinet
 * Date: 2014-09-23 9:45 AM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class ChangeSemesterDialog extends AlertDialog {
    private AlertDialog mDialog;

    private Term mTerm;
    private CheckBox mDefaultCheckbox;

    public ChangeSemesterDialog(Context context, boolean registerTerms, Term term) {
        super(context);

        Analytics.getInstance().sendScreen("Schedule - Change Semester");

        //Inflate the right view
        View layout = View.inflate(context, R.layout.dialog_change_semester, null);

        //Set up the default checkbox
        mDefaultCheckbox = (CheckBox)layout.findViewById(R.id.change_semester_default);
        //Don't show it if we are doing the register terms only
        if(registerTerms){
            mDefaultCheckbox.setVisibility(View.GONE);
        }

        //Check if there was a term sent, use the default one if none was sent
        mTerm = term == null ? App.getDefaultTerm() : term;

        List<Term> terms = new ArrayList<Term>();
        //We are using the user's existing terms
        if(!registerTerms){
            for (Semester semester : App.getTranscript().getSemesters()) {
                terms.add(semester.getTerm());
            }
        }
        //We are using the registration terms
        else{
            for(Term term1 : App.getRegisterTerms()){
                terms.add(term1);
            }
        }

        //Order them chronologically
        Collections.sort(terms, new Comparator<Term>() {
            @Override
            public int compare(Term term, Term term2) {
                return term.isAfter(term2) ? -1 : 1;
            }
        });

        //Set up the spinner
        Spinner spinner = (Spinner)layout.findViewById(R.id.change_semester_term);
        final TermAdapter adapter = new TermAdapter(context, terms);
        spinner.setAdapter(adapter);
        spinner.setSelection(terms.indexOf(mTerm));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the selected term
                mTerm = adapter.getItem(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
            .setView(layout)
            .setCustomTitle(View.inflate(context, R.layout.dialog_change_semester_title, null))
            .setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Check if the default checkbox is checked
                    if (mDefaultCheckbox.isChecked()) {
                        //Store this semester as the default semester if it is
                        App.setDefaultTerm(mTerm);
                    }

                    dialog.dismiss();
                }
            })
            .setNegativeButton(context.getString(android.R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Dismiss with the term set to null
                    mTerm = null;
                    dialog.dismiss();
                }
            });
        mDialog = builder.create();
    }

    @Override
    public void show(){
        mDialog.show();
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener){
        mDialog.setOnDismissListener(onDismissListener);
    }

    public Term getTerm(){
        return mTerm;
    }
}