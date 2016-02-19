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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.TermAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Helper methods that create dialogs for various situations
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class DialogHelper {

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
     * Shows an alert dialog allowing the user to change their shown semester
     *
     * @param context       The app context
     * @param term          The term currently selected
     * @param registerTerms True if we should be using the registration terms, false otherwise
     * @param callback      Callback to use when a new term has been selected
     */
    public static void changeSemester(Context context, @Nullable Term term, boolean registerTerms,
            final TermCallback callback){
        Analytics.get().sendScreen("Change Semester");

        //Use the default term if no term was sent
        if(term == null) {
            term = App.getDefaultTerm();
        }

        List<Term> terms = new ArrayList<>();
        if (!registerTerms) {
            //We are using the user's existing terms
            for (Semester semester : App.getTranscript().getSemesters()) {
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
     * Shows a dialog with course information
     *
     * @param activity The calling activity
     * @param course   The course
     */
    public static void showCourseDialog(final Activity activity, final Course course){
        Analytics.get().sendScreen("Schedule - Course");

        //Inflate the title
        View titleView = View.inflate(activity, R.layout.dialog_course_title, null);

        //Code
        TextView code = (TextView)titleView.findViewById(R.id.course_code);
        code.setText(course.getCode());

        //Title
        TextView title = (TextView)titleView.findViewById(R.id.course_title);
        title.setText(course.getTitle());

        //Inflate the body
        View layout = View.inflate(activity, R.layout.dialog_course, null);

        //Time
        TextView time = (TextView)layout.findViewById(R.id.course_time);
        time.setText(course.getTimeString());

        //Location
        TextView location = (TextView)layout.findViewById(R.id.course_location);
        location.setText(course.getLocation());

        //Type
        TextView type = (TextView)layout.findViewById(R.id.course_type);
        type.setText(course.getType());

        //Instructor
        TextView instructor = (TextView)layout.findViewById(R.id.course_instructor);
        instructor.setText(course.getInstructor());

        //Section
        TextView section = (TextView)layout.findViewById(R.id.course_section);
        section.setText(course.getSection());

        //Credits
        TextView credits = (TextView)layout.findViewById(R.id.course_credits);
        credits.setText(String.valueOf(course.getCredits()));

        //CRN
        TextView crn = (TextView)layout.findViewById(R.id.course_crn);
        crn.setText(String.valueOf(course.getCRN()));

        //Docuum Link
        TextView docuum = (TextView)layout.findViewById(R.id.course_docuum);
        docuum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Utils.openURL(activity, Help.getDocuumLink(course.getSubject(), course.getNumber()));
            }
        });

        //Show on Map
        TextView map = (TextView)layout.findViewById(R.id.course_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO
            }
        });

        new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setView(layout)
                .setCancelable(true)
                .setNeutralButton(R.string.done, new DialogInterface.OnClickListener() {
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
