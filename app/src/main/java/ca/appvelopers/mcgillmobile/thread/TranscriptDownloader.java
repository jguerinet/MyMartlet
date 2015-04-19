/*
 * Copyright 2014-2015 Appvelopers
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

package ca.appvelopers.mcgillmobile.thread;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Downloads, parses and saves the user's transcript
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public abstract class TranscriptDownloader implements Runnable {
    /**
     * The app context
     */
    private Context mContext;
    /**
     * True if we should be showing errors in the parsing of the transcript, false otherwise
     */
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
    public void run() {
        if(!Test.LOCAL_TRANSCRIPT){
            String transcriptString = Connection.getInstance().getUrl(mContext, Connection.TRANSCRIPT_URL, mShowErrors);

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
}

