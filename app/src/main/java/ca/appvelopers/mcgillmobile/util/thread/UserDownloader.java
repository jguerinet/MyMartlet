/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.util.thread;

import android.content.Context;
import android.net.ConnectivityManager;

import com.guerinet.utils.Utils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.model.exception.NoInternetException;
import ca.appvelopers.mcgillmobile.model.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
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
     * {@link ConnectivityManager} instance
     */
    @Inject
    protected ConnectivityManager connectivityManager;
    /**
     * The {@link McGillManager} instance
     */
    @Inject
    protected McGillManager mcGillManager;
    /**
     * The {@link McGillService} instance
     */
    @Inject
    protected McGillService mcGillService;
    /**
     * True if everything should be downloaded, false otherwise (defaults to false)
     */
    protected boolean downloadEverything = false;

    /**
     * Default Constructor
     *
     * @param context            App context
     */
    public UserDownloader(Context context) {
        App.component(context).inject(this);
    }

    @Override
    public void run() {
        synchronized (this) {
            //If we're not connected to the internet, don't continue
            if (!Utils.isConnected(connectivityManager)) {
                return;
            }

            //Transcript
            if (downloadEverything) {
                update(context.getString(R.string.downloading_transcript));
            }

            try {
                if (Test.LOCAL_TRANSCRIPT) {
                    Test.testTranscript();
                } else {
                    Parser.parseTranscript(mcGillManager.get(mcGillService.transcript()));
                }
            } catch (NoInternetException ignored) {
            } catch (MinervaException e) {
                //TODO
            } catch(IOException e) {
                Timber.e(e, "Transcript Exception");
            }

            //Semesters
            if (Test.LOCAL_SCHEDULE) {
                //Test mode : only one semester to do
                Test.testSchedule();
            } else {
                //The current term
                Term currentTerm = Term.currentTerm();
                //List of semesters
                List<Semester> semesters = App.getTranscript().getSemesters();

                //Go through the semesters
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
                            Parser.parseCourses(term,
                                    mcGillManager.get(mcGillService.schedule(term)));
                        } catch (NoInternetException ignored) {
                        } catch (MinervaException e) {
                            //TODO
                        } catch (IOException e) {
                            Timber.e("Term: %s", term.getId());
                            Timber.e(e, "Term Exception");
                        }
                    }
                }

                //TODO Move this
                //Set the default term if there is none set yet
                if (App.getDefaultTerm() == null) {
                    App.setDefaultTerm(currentTerm);
                }
            }

            //Ebill
            if (downloadEverything) {
                update(context.getString(R.string.downloading_ebill));
            }

            //Download the eBill and user info
            try {
                Parser.parseEbill(mcGillManager.get(mcGillService.ebill()));
            } catch (NoInternetException ignored) {

            } catch (MinervaException e) {
                //TODO
            } catch(Exception e) {
                Timber.e(e, "Ebill Exception");
            }
            notify();
        }
    }

    /**
     * Executes the thread synchronously
     */
    public void execute() {
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
    }

    public abstract void update(String section);
}

