package ca.appvelopers.mcgillmobile.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;

/**
 * Author : Julien
 * Date :  2014-06-03 9:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class HelpFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_help, null);

        //Set up the Report a Bug Feature
        Button reportBug = (Button)view.findViewById(R.id.report_bug);
        reportBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Inflate the view
                View dialogView = View.inflate(getActivity(), R.layout.dialog_edittext, null);

                //Create the Builder
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                //Set up the view
                alertDialogBuilder.setView(dialogView);
                //EditText
                final EditText userInput = (EditText) dialogView.findViewById(R.id.dialog_input);

                //Set up the dialog
                alertDialogBuilder.setCancelable(false)
                        .setNegativeButton(getResources().getString(android.R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(getResources().getString(android.R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        GoogleAnalytics.sendEvent(getActivity(), "About", "Report a Bug",
                                                null, null);

                                        //Get the user input
                                        final String summary = userInput.getText().toString();
                                        //Get the other necessary info
                                        //App Version Name & Number
                                        PackageInfo packageInfo = null;
                                        try {
                                            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                                        } catch (PackageManager.NameNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        String appVersionName = "";
                                        int appVersionNumber = 0;
                                        if (packageInfo != null) {
                                            appVersionName = packageInfo.versionName;
                                            appVersionNumber = packageInfo.versionCode;
                                        }

                                        //OS Version
                                        String osVersion = Build.VERSION.RELEASE;
                                        //SDK Version Number
                                        int sdkVersionNumber = Build.VERSION.SDK_INT;

                                        //Manufacturer
                                        String manufacturer = Build.MANUFACTURER;
                                        //Model
                                        String model = Build.MODEL;

                                        String phoneModel;
                                        if (!model.startsWith(manufacturer)) {
                                            phoneModel = manufacturer + " " + model;
                                        } else {
                                            phoneModel = model;
                                        }

                                        //Prepare the email
                                        Intent bugEmail = new Intent(Intent.ACTION_SEND);
                                        //Recipient
                                        bugEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.REPORT_A_BUG_EMAIL});
                                        //Title
                                        bugEmail.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.help_bug_title,
                                                "Android") + " " + summary);
                                        //Message
                                        bugEmail.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.help_bug_summary,
                                                "Android",
                                                App.getLanguage().getLanguageString(),
                                                appVersionName,
                                                appVersionNumber,
                                                osVersion,
                                                sdkVersionNumber,
                                                phoneModel));
                                        //Type(Email)
                                        bugEmail.setType("message/rfc822");
                                        startActivity(Intent.createChooser(bugEmail, getResources().getString(R.string.about_email_picker_title)));
                                    }
                                })
                        .create().show();
            }
        });

        return view;
    }
}