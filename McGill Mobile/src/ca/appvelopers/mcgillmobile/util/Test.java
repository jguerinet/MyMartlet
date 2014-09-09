package ca.appvelopers.mcgillmobile.util;

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 10:54 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class Test {
    /**
     * Switch this to true if you want to read the transcript locally
     */
    public static boolean LOCAL_TRANSCRIPT = false;

    /**
     * Test the transcript by reading from a local one
     * @param context The app context
     */
    public static void testTranscript(Context context){
        //Read from the file
        String transcriptString ="";// Help.readFromFile(context, R.raw.missingfall2014);

        Parser.parseTranscript(transcriptString);
    }
}
