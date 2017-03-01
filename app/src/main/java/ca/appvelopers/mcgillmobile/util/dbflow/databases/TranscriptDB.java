/*
 * Copyright 2014-2017 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.util.dbflow.databases;

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Database;

import java.util.Collections;

import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.retrofit.TranscriptConverter.TranscriptResponse;
import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse;
import ca.appvelopers.mcgillmobile.util.dbflow.DBUtils;

/**
 * Database that holds the {@link Transcript}
 * @author Julien Guerinet
 * @since 2.4.0
 */
@Database(name = TranscriptDB.NAME, version = TranscriptDB.VERSION)
public class TranscriptDB {
    static final String NAME = "Transcript";
    public static final String FULL_NAME = NAME + ".db";
    static final int VERSION = 1;

    /**
     * Saves all of the info from the {@link TranscriptResponse} to the appropriate DBs
     *
     * @param context  App context
     * @param response Received {@link TranscriptResponse}
     */
    public static void saveTranscript(Context context, TranscriptResponse response) {
        // Replace the Transcript
        DBUtils.replaceDB(context, TranscriptDB.NAME, Transcript.class,
                Collections.singletonList(response.transcript), null);

        // Replace the Semesters
        DBUtils.replaceDB(context, SemestersDB.NAME, Semester.class, response.semesters, null);

        // Replace the classes
        DBUtils.replaceDB(context, TranscriptCoursesDB.NAME, TranscriptCourse.class,
                response.courses, null);
    }
}
