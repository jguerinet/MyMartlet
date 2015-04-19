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
import android.util.Log;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.exception.NoInternetException;
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
    private static final String TAG = "TranscriptDownloader";
    /**
     * The app context
     */
    private Context mContext;
    /**
     * True if we should be showing errors in the parsing of the transcript, false otherwise
     */
    private boolean mShowErrors;

    /**
     * Constructor with the option of disabling the showing of errors on the UI
     *  (for ScheduleFragment)
     *
     * @param context    The app context
     * @param showErrors True if we should show any errors on the UI, false otherwise
     */
    public TranscriptDownloader(Context context, boolean showErrors){
        mContext = context;
        mShowErrors = showErrors;
    }

    /**
     * Default Constructor
     *
     * @param context The app context
     */
    public TranscriptDownloader(Context context){
        this(context, true);
    }

    @Override
    public void run() {
        //If we are using the local transcript, don't download the new one
        if(!Test.LOCAL_TRANSCRIPT){
            try{
                //Get and parse the transcript
                Parser.parseTranscript(Connection.getInstance().get(Connection.TRANSCRIPT_URL));
            } catch(MinervaLoggedOutException e){
                //TODO Broadcast this
            } catch(Exception e){
                final boolean noInternet = e instanceof NoInternetException;
                Log.e(TAG, noInternet ? "No Internet" : "IOException", e);

                //Show an error message if possible and needed
                if(mContext instanceof  Activity && mShowErrors){
                    ((Activity)mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run(){
                            DialogHelper.showNeutralAlertDialog(mContext,
                                    mContext.getString(R.string.error),
                                    noInternet ? mContext.getString(R.string.error_no_internet) :
                                    mContext.getString(R.string.error_other));
                        }
                    });
                }
            }
        }
    }
}

