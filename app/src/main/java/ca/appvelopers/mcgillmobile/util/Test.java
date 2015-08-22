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

package ca.appvelopers.mcgillmobile.util;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Season;
import ca.appvelopers.mcgillmobile.model.Term;

/**
 * Various utility methods to test sections of the app
 * @author Julien Guerinet
 * @author Ryan Singzon
 * @version 2.0.0
 * @since 1.0.0
 */
public class Test {
    /**
     * Switch this to true if you want to read the transcript locally
     */
    public static boolean LOCAL_TRANSCRIPT = false;
    /**
     * Switch this to true if you want to read the schedule locally
     */
    public static boolean LOCAL_SCHEDULE = false;

    /**
     * Test the transcript by reading from a local one
     */
    public static String testTranscript(){
        //Read from the file
        String transcriptString = Help.readFromFile(App.getContext(), R.raw.test_transcript);

        return Parser.parseTranscript(transcriptString);
    }

    /**
     * Test the schedule by reading from a local one
     */
    public static String testSchedule(){
        //Choose file to read from here
        String scheduleString = Help.readFromFile(App.getContext(), R.raw.sched_nursing);

        //Choose term that this schedule is for here
        Term term = new Term(Season.FALL, 2014);

        return Parser.parseCourses(term, scheduleString);
    }
}
