package ca.appvelopers.mcgillmobile.util;

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Season;
import ca.appvelopers.mcgillmobile.object.Term;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-11 10:54 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class Test {
    /**
     * Switch this to true if you want to read the transcript locally
     */
    public static boolean LOCAL_TRANSCRIPT = true;
    /**
     * Switch this to true if you want to read the schedule locally
     */
    public static boolean LOCAL_SCHEDULE = false;

    /**
     * Test the transcript by reading from a local one
     * @param context The app context
     */
    public static String testTranscript(Context context){
        //Read from the file
        String transcriptString = Help.readFromFile(context, R.raw.test_transcript);

        return Parser.parseTranscript(transcriptString);
    }

    /**
     * Test the schedule by reading from a local one
     * @param context The app context
     */
    public static String testSchedule(Context context){
        //Choose file to read from here
        String scheduleString = Help.readFromFile(context, R.raw.sched_nursing);

        //Choose term that this schedule is for here
        Term term = new Term(Season.FALL, 2014);

        return Parser.parseClassList(term, scheduleString);
    }
}
