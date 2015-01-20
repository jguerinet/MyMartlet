package ca.appvelopers.mcgillmobile.thread;

import android.app.Activity;
import android.content.Context;
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
    private Context mContext;
    private boolean mShowErrors;

    public TranscriptDownloader(Context context){
    	mContext = context;
        mShowErrors = true;
    }

    //This is to not show 2 errors in the Schedule page
    public TranscriptDownloader(Context context, boolean showErrors){
        mContext = context;
        mShowErrors = showErrors;
    }

	@Override
    protected abstract void onPreExecute();

    @Override
    protected Boolean doInBackground(Void... params) {
        if(!Test.LOCAL_TRANSCRIPT){
            String transcriptString = Connection.getInstance().getUrl(mContext, Connection.TRANSCRIPT, mShowErrors);

            if(transcriptString == null){
            	if(mContext instanceof Activity){
            		final Activity mActivity = (Activity) mContext;
                    if(mShowErrors){
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
                    }
            	}
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

