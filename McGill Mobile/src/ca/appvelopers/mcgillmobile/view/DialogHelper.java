package ca.appvelopers.mcgillmobile.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Help;
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
     * Show this dialog when there was a bug in the parsing of the transcript
     * @param className The class that the bug is in
     * @param exception The exception that was thrown
     */
    public static void showTranscriptBugDialog(final String className, final String exception) {
        final Context context = App.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View checkboxLayout = View.inflate(context, R.layout.dialog_checkbox, null);
        final CheckBox dontShowAgain = (CheckBox) checkboxLayout.findViewById(R.id.skip);
        builder.setView(checkboxLayout);
        builder.setTitle(context.getString(R.string.warning));
        builder.setMessage(context.getString(R.string.bug_parser_transcript));
        builder.setPositiveButton(context.getString(R.string.bug_parser_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Send the bug report
                Help.sendBugReport(context, context.getString(R.string.bug_parser_transcript_title, className), exception);

                //Save the do not show option
                Save.saveParserErrorDoNotShow(context, dontShowAgain.isChecked());
            }
        });
        builder.setNegativeButton(context.getString(R.string.bug_parser_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss the dialog
                dialog.dismiss();

                //Save the do not show again
                Save.saveParserErrorDoNotShow(context, dontShowAgain.isChecked());
            }
        });

        //Only show if they have not checked "Do not show again" already
        if(!Load.loadParserErrorDoNotShow(context)){
            builder.show();
        }
    }

    /**
     * Show this dialog when there was a bug in the parsing of the semester
     * @param term The term that the bug is in
     * @param className The class that the bug is in
     * @param exception The exception that was thrown
     */
    public static void showSemesterBugDialog(final String term, final String className, final String exception) {
        final Context context = App.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View checkboxLayout = View.inflate(context, R.layout.dialog_checkbox, null);
        final CheckBox dontShowAgain = (CheckBox) checkboxLayout.findViewById(R.id.skip);
        builder.setView(checkboxLayout);
        builder.setTitle(context.getString(R.string.warning));
        builder.setMessage(context.getString(R.string.bug_parser_semester, term));
        builder.setPositiveButton(context.getString(R.string.bug_parser_yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Send the bug report
                Help.sendBugReport(context, context.getString(R.string.bug_parser_semester_title, term, className), exception);

                //Save the do not show option
                Save.saveParserErrorDoNotShow(context, dontShowAgain.isChecked());
            }
        });
        builder.setNegativeButton(context.getString(R.string.bug_parser_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss the dialog
                dialog.dismiss();

                //Save the do not show again
                Save.saveParserErrorDoNotShow(context, dontShowAgain.isChecked());
            }
        });

        //Only show if they have not checked "Do not show again" already
        if(!Load.loadParserErrorDoNotShow(context)){
            builder.show();
        }
    }
}
