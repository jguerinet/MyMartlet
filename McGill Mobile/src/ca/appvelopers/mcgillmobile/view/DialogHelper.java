package ca.appvelopers.mcgillmobile.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Help;

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
     * @param activity The calling activity
     * @param className The class that the bug is in
     * @param exception The exception that was thrown
     */
    public static void showTranscriptBugDialog(final Activity activity, final String className, final String exception) {
        //Creates an alert dialog with the
        //given string as a message, an OK button, and Error as the title
        new AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.bug_parser_transcript))
                .setPositiveButton(activity.getString(R.string.bug_parser_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Help.sendBugReport(activity, activity.getString(R.string.bug_parser_transcript_title, className), exception);
                    }
                })
                .setNegativeButton(activity.getString(R.string.bug_parser_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    /**
     * Show this dialog when there was a bug in the parsing of the semester
     * @param activity The calling activity
     * @param term The term that the bug is in
     * @param className The class that the bug is in
     * @param exception The exception that was thrown
     */
    public static void showSemesterBugDialog(final Activity activity, final String term, final String className, final String exception) {
        new AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.bug_parser_semester, term))
                .setPositiveButton(activity.getString(R.string.bug_parser_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Help.sendBugReport(activity, activity.getString(R.string.bug_parser_semester_title, term, className), exception);
                    }
                })
                .setNegativeButton(activity.getString(R.string.bug_parser_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
