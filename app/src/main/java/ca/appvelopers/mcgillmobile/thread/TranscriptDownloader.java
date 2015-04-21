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

import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.Test;

/**
 * Downloads, parses and saves the user's transcript
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class TranscriptDownloader extends InfoDownloader{
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
        synchronized(this){
            //If we are using the local transcript, don't download the new one
            if(!Test.LOCAL_TRANSCRIPT){
                //Figure out the activity instance: if we should show errors and the context is an
                //  instance of activity, pass it along. Otherwise, pass null
                Activity activity = (mContext instanceof Activity && mShowErrors) ?
                        (Activity)mContext : null;
                String transcript = get(TAG, activity, Connection.TRANSCRIPT_URL);

                //If there is a String, parse it
                if(transcript != null){
                    Parser.parseTranscript(transcript);
                }
            }
        }
        notify();
    }
}

