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

import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;

/**
 * Downloads, parses and saves the user's classes for a given term
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public abstract class ClassDownloader extends InfoDownloader{
    private static final String TAG = "ClassDownloader";
    /**
     * The calling activity
     */
    private Activity mActivity;
    /**
     * The term to download the classes for
     */
    private Term mTerm;

    /**
     * Default Constructor
     *
     * @param activity The calling activity
     * @param term     The term to download the classes for
     */
    public ClassDownloader(Activity activity, Term term){
        this.mActivity = activity;
        this.mTerm = term;
    }

    @Override
    public void run() {
        String classes = get(TAG, Connection.getScheduleURL(mTerm), mActivity);

        //If there is a body response, parse it
        if(classes != null){
            Parser.parseClassList(mTerm, classes);
        }
    }
}
