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

package com.guerinet.mymartlet.util.thread;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.Course;
import com.guerinet.mymartlet.model.Semester;
import com.guerinet.mymartlet.model.Statement;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.model.exception.MinervaException;
import com.guerinet.mymartlet.util.Constants;
import com.guerinet.mymartlet.util.dbflow.DBUtils;
import com.guerinet.mymartlet.util.dbflow.databases.CourseDB;
import com.guerinet.mymartlet.util.dbflow.databases.StatementDB;
import com.guerinet.mymartlet.util.dbflow.databases.TranscriptDB;
import com.guerinet.mymartlet.util.retrofit.McGillService;
import com.guerinet.mymartlet.util.retrofit.TranscriptConverter;
import com.guerinet.suitcase.util.Utils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.net.ssl.SSLException;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Downloads the user's information
 * @author Julien Guerinet
 * @since 2.2.0
 */
public abstract class UserDownloader extends Thread {
    /**
     * App context
     */
    @Inject
    protected Context context;
    /**
     * {@link McGillService} instance
     */
    @Inject
    protected McGillService mcGillService;
    /**
     * True if everything should be downloaded, false otherwise (defaults to false)
     */
    protected boolean downloadEverything = false;
    /**
     * {@link IOException} that potentially occurred during the downloading of info
     */
    private IOException exception;

    /**
     * Default Constructor
     *
     * @param context App context
     */
    public UserDownloader(Context context) {
        App.Companion.component(context).inject(this);
    }

    @Override
    public void run() {
        synchronized (this) {
            //If we're not connected to the internet, don't continue
            if (!Utils.isConnected(context)) {
                return;
            }

            //Transcript
            if (downloadEverything) {
                update(context.getString(R.string.downloading_transcript));
            }

            try {
                TranscriptConverter.TranscriptResponse transcriptResponse =
                        mcGillService.transcript().execute().body();
                TranscriptDB.saveTranscript(context, transcriptResponse);
            } catch(IOException e) {
                handleException(e, "Transcript");
            }

            //The current term
            Term currentTerm = Term.currentTerm();

            // Go through the semesters
            List<Semester> semesters = SQLite.select()
                    .from(Semester.class)
                    .queryList();
            for (Semester semester: semesters) {
                //Get the term of this semester
                Term term = semester.getTerm();

                //If we are not downloading everything, only download it if it's the
                //  current or future term
                if (downloadEverything || term.equals(currentTerm) ||
                        term.isAfter(currentTerm)) {
                    if (downloadEverything) {
                        update(context.getString(R.string.downloading_semester,
                                term.getString(context)));
                    }

                    //Download the schedule
                    try {
                        List<Course> courses = mcGillService.schedule(term).execute().body();
                        CourseDB.setCourses(term, courses, null);
                    } catch (IOException e) {
                        handleException(e, term.getId());
                    }
                }
            }

            //Ebill
            if (downloadEverything) {
                update(context.getString(R.string.downloading_ebill));
            }

            // Download the ebill
            try {
                Response<List<Statement>> response = mcGillService.ebill().execute();
                DBUtils.replaceDB(context, StatementDB.NAME, Statement.class, response.body(),
                        null);
            } catch (IOException e) {
                handleException(e, "Ebill");
            }
            notify();
        }
    }

    /**
     * Handles any exception while downloading the user info
     *
     * @param e       Exception instance
     * @param section Section that the exception happened in
     */
    private void handleException(IOException e, String section) {
        if (e instanceof MinervaException) {
            LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(new Intent(Constants.BROADCAST_MINERVA));
        } else if (!(e instanceof SSLException)) {
            // Don't log SSLExceptions
            Timber.e(new Exception("Exception: " + section, e), "");
        }
        exception = e;
    }

    /**
     * Executes the thread synchronously
     *
     * @throws IOException
     */
    public void execute() throws IOException {
        //If we are downloading this synchronously, then we're downloading everything
        downloadEverything = true;

        //Start the download
        start();

        //Wait until the thread has been fully executed
        synchronized(this) {
            try {
                wait();
            } catch(InterruptedException ignored){}
        }

        //If there's an exception, throw it
        if (exception != null) {
            throw exception;
        }
    }

    public abstract void update(String section);
}

