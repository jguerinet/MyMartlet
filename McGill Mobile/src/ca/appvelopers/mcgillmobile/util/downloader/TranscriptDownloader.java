package ca.appvelopers.mcgillmobile.util.downloader;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author : Julien
 * Date :  2014-06-18 7:36 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public abstract class TranscriptDownloader extends AsyncTask<Void, Void, Boolean>{
    private Activity mActivity;
    public TranscriptDownloader(Activity activity){
        mActivity = activity;
    }

    @Override
    protected abstract void onPreExecute();

    @Override
    protected Boolean doInBackground(Void... params) {
        if(!Test.LOCAL_TRANSCRIPT){
            String transcriptString = Connection.getInstance().getUrl(mActivity, Connection.TRANSCRIPT);

            if(transcriptString == null){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DialogHelper.showNeutralAlertDialog(mActivity, mActivity.getResources().getString(R.string.error),
                                    mActivity.getResources().getString(R.string.error_other));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
            //Empty String: no need for an alert dialog but no need to reload
            else if(TextUtils.isEmpty(transcriptString)){
                return false;
            }

            //Parse the transcript
            Parser.parseTranscript(transcriptString);

            return true;
        }

        return false;
    }

    @Override
    protected abstract void onPostExecute(Boolean loadInfo);
}

