package ca.appvelopers.mcgillmobile.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
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

    public ChangeSemesterDialog(Activity activity, boolean registerTerms, Term term) {
        super(activity);

        GoogleAnalytics.sendScreen(activity, "Schedule - Change Semester");

        //Inflate the right view
        View layout = View.inflate(activity, R.layout.dialog_change_semester, null);

        //Set up the default checkbox
        mDefaultCheckbox = (CheckBox)layout.findViewById(R.id.change_semester_default);

        //Check if there was a term sent, use the default one if none was sent
        mTerm = term == null ? App.getDefaultTerm() : term;

        List<Term> terms = new ArrayList<Term>();
        //Extract all the terms that the user has registered in
        for (Semester semester : App.getTranscript().getSemesters()) {
            terms.add(semester.getTerm());
        }

        //If we are also using the registration term
        if(registerTerms){
            //Get the current terms that the user can register in
            for(Term term1 : App.getRegisterTerms()){
                if(!terms.contains(term1)){
                    terms.add(term1);
                }
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
        final TermAdapter adapter = new TermAdapter(activity, terms);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false)
            .setView(layout)
            .setCustomTitle(View.inflate(activity, R.layout.dialog_change_semester_title, null))
            .setPositiveButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Check if the default checkbox is checked
                    if (mDefaultCheckbox.isChecked()) {
                        //Store this semester as the default semester if it is
                        App.setDefaultTerm(mTerm);
                    }

                    Intent replyIntent = new Intent();
                    replyIntent.putExtra(Constants.TERM, mTerm);

                    dialog.dismiss();
                }
            })
            .setNegativeButton(activity.getString(android.R.string.no), new DialogInterface.OnClickListener() {
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