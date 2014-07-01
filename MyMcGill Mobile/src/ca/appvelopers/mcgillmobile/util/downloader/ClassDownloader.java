package ca.appvelopers.mcgillmobile.util.downloader;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author : Julien
 * Date :  2014-06-13 8:23 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public abstract class ClassDownloader extends AsyncTask<Void, Void, Boolean> {
    private Activity mActivity;
    private Term mTerm;

    public ClassDownloader(Activity activity, Term term){
        mActivity = activity;
        mTerm = term;
    }

    @Override
    protected abstract void onPreExecute();

    @Override
    protected Boolean doInBackground(Void... params) {
        String scheduleString;

        scheduleString = Connection.getInstance().getUrl(mActivity, Connection.getScheduleURL(mTerm));

        if(scheduleString == null){
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogHelper.showNeutralAlertDialog(mActivity, mActivity.getResources().getString(R.string.error),
                            mActivity.getResources().getString(R.string.error_other));
                }
            });
            return false;
        }
        //Empty String: no need for an alert dialog but no need to reload
        else if(TextUtils.isEmpty(scheduleString)){
            return false;
        }

        //Get the new schedule
        Parser.parseClassList(mTerm, scheduleString);

        return true;
    }

    @Override
    protected abstract void onPostExecute(Boolean loadInfo);
}
