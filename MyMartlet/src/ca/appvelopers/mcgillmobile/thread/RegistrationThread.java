package ca.appvelopers.mcgillmobile.thread;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.List;
import java.util.Map;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-20 10:19 AM
 * Copyright (c) 2015 Appvelopers. All rights reserved.
 * Connects to Minerva in a new thread to register for courses
 */

public abstract class RegistrationThread extends AsyncTask<Void, Void, Boolean> {
    private Activity mActivity;
    private String mRegistrationURL;
    protected List<ClassItem> mRegistrationCourses;
    protected Map<String, String> mRegistrationErrors = null;

    public RegistrationThread(Activity activity, Term term, List<ClassItem> courses){
        this.mActivity = activity;
        this.mRegistrationCourses = courses;
        this.mRegistrationURL = Connection.getRegistrationURL(term, mRegistrationCourses, false);
        this.mRegistrationErrors = null;
    }

    @Override
    protected abstract void onPreExecute();

    //Retrieve page that contains registration status from Minerva
    @Override
    protected Boolean doInBackground(Void... params){
        String resultString = Connection.getInstance().getUrl(mActivity, mRegistrationURL);

        //If result string is null, there was an error
        if(resultString == null){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        DialogHelper.showNeutralAlertDialog(mActivity, mActivity.getString(R.string.error),
                                mActivity.getString(R.string.error_other));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return false;
        }
        //Otherwise, check for errors
        else{
            mRegistrationErrors = Parser.parseRegistrationErrors(resultString);
            return true;
        }
    }

    @Override
    protected abstract void onPostExecute(Boolean success);
}