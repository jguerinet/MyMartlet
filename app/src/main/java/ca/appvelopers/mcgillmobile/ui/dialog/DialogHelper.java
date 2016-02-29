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

package ca.appvelopers.mcgillmobile.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.TermAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.manager.TranscriptManager;

/**
 * Helper methods that create dialogs for various situations
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class DialogHelper {

    /**
     * Displays a toast with a generic error message
     *
     * @param context App context
     */
    public static void error(Context context) {
        Utils.toast(context, R.string.error_other);
    }

    /**
     * Shows an error {@link AlertDialog} with one button
     *
     * @param context   App context
     * @param messageId String Id of the error description
     */
    public static void error(Context context, @StringRes int messageId) {
        DialogUtils.neutral(context, R.string.error, messageId);
    }

    /**
     * Shows an error {@link AlertDialog} with one button
     *
     * @param context App context
     * @param message Message String
     */
    public static void error(Context context, String message) {
        DialogUtils.neutral(context, R.string.error, message);
    }

    /**
     * Shows an alert dialog allowing the user to change their shown semester
     *
     * @param context       The app context
     * @param term          The term currently selected
     * @param registerTerms True if we should be using the registration terms, false otherwise
     * @param callback      Callback to use when a new term has been selected
     * @param analytics     {@link Analytics} instance
     */
    public static void changeSemester(Context context, @Nullable Term term, boolean registerTerms,
            final TermCallback callback, Analytics analytics, TranscriptManager transcriptManager) {
        analytics.sendScreen("Change Semester");

        //Use the default term if no term was sent
        if(term == null) {
            term = App.getDefaultTerm();
        }

        List<Term> terms = new ArrayList<>();
        if (!registerTerms) {
            //We are using the user's existing terms
            for (Semester semester : transcriptManager.get().getSemesters()) {
                terms.add(semester.getTerm());
            }
        } else {
            //We are using the registration terms
            terms.addAll(App.getRegisterTerms());
        }

        View view = View.inflate(context, R.layout.dialog_change_semester, null);

        final CheckBox defaultCheckBox = (CheckBox) view.findViewById(R.id.default_term);
        final Spinner termSpinner = (Spinner)view.findViewById(R.id.change_semester_term);

        //Don't show the checkbox if we are doing the register terms only
        if (registerTerms) {
            defaultCheckBox.setVisibility(View.GONE);
        }

        //Set up the spinner
        TermAdapter adapter = new TermAdapter(terms);
        termSpinner.setAdapter(adapter);
        termSpinner.setSelection(terms.indexOf(term));

        new AlertDialog.Builder(context)
                .setTitle(R.string.title_change_semester)
                .setView(view)
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        //Get the selected term
                        Term term = (Term) termSpinner.getSelectedItem();

                        //Check if the default checkbox is checked
                        if(defaultCheckBox.isChecked()){
                            //Store this semester as the default semester if it is
                            App.setDefaultTerm(term);
                        }
                        dialog.dismiss();
                        callback.onTermSelected(term);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Callback used for the ChangeTermDialog
     */
    public static abstract class TermCallback {
        /**
         * Called when a new term has been selected by the user
         *
         * @param term The term selected
         */
        public abstract void onTermSelected(Term term);
    }
}
