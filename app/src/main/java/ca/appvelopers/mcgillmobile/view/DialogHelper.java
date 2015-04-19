/*
 * Copyright 2014-2015 Appvelopers Inc.
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

package ca.appvelopers.mcgillmobile.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;

import com.instabug.library.Instabug;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;

/**
 * Author: Julien
 * Date: 2014-03-02 20:05
 */
public class DialogHelper {
    public static void showNeutralAlertDialog(Context context, String title, String message){
        //Creates an alert dialog with the given string as a message, an OK button, and Error as the title
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Show this dialog when there was a bug in the parsing of the transcript or schedule
     * @param context The calling context
     * @param transcriptBug True if it's a bug on the transcript, false if it's a bug on the schedule
     * @param term The class that the bug is in
     */
    public static void showBugDialog(final Context context, final boolean transcriptBug, final String term) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View checkboxLayout = View.inflate(context, R.layout.dialog_checkbox, null);
        final CheckBox dontShowAgain = (CheckBox) checkboxLayout.findViewById(R.id.skip);
        builder.setView(checkboxLayout);
        builder.setTitle(context.getString(R.string.warning));
        builder.setMessage(transcriptBug ? context.getString(R.string.bug_parser_transcript) : context.getString(R.string.bug_parser_semester, term));
        builder.setPositiveButton(context.getString(R.string.bug_parser_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Send the bug report
                Instabug.getInstance().sendFeedback(transcriptBug ?
                                context.getString(R.string.bug_parser_transcript_title, term) :
                                context.getString(R.string.bug_parser_semester_title),
                        null, new Instabug.b(){});

                //Save the do not show option
                Save.saveParserErrorDoNotShow(context, dontShowAgain.isChecked());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(context.getString(R.string.bug_parser_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Save the do not show again
                Save.saveParserErrorDoNotShow(context, dontShowAgain.isChecked());

                dialog.dismiss();
            }
        });

        //Only show if they have not checked "Do not show again" already
        if(!Load.loadParserErrorDoNotShow(context)) {
            builder.show();
        }
    }
}
