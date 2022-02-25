/*
 * Copyright 2014-2022 Julien Guerinet
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

package com.guerinet.mymartlet.util.thread

import android.content.Context
import android.content.Intent
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.model.exception.MinervaException
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.retrofit.McGillService
import com.guerinet.mymartlet.util.room.daos.CourseDao
import com.guerinet.mymartlet.util.room.daos.SemesterDao
import com.guerinet.mymartlet.util.room.daos.StatementDao
import com.guerinet.mymartlet.util.room.daos.TranscriptCourseDao
import com.guerinet.mymartlet.util.room.daos.TranscriptDao
import com.guerinet.suitcase.coroutines.bgDispatcher
import com.guerinet.suitcase.coroutines.uiDispatcher
import com.guerinet.suitcase.util.extensions.isConnected
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import javax.net.ssl.SSLException
import kotlin.concurrent.withLock

/**
 * TODO
 * Downloads the user's information
 * @author Julien Guerinet
 * @since 2.2.0
 */
abstract class UserDownloader(val context: Context) : Thread(), KoinComponent {

    private val mcGillService by inject<McGillService>()

    private val courseDao by inject<CourseDao>()

    private val semesterDao by inject<SemesterDao>()

    private val statementDao by inject<StatementDao>()

    private val transcriptDao by inject<TranscriptDao>()

    private val transcriptCourseDao by inject<TranscriptCourseDao>()

    private val lock = ReentrantLock()

    private val condition = lock.newCondition()

    /**
     * True if everything should be downloaded, false otherwise (defaults to false)
     */
    protected var downloadEverything = false
    /**
     * [IOException] that potentially occurred during the downloading of info
     */
    private var exception: IOException? = null

    override fun run() {
        lock.withLock {
            //If we're not connected to the internet, don't continue
            if (!context.isConnected) {
                return
            }

            //Transcript
            if (downloadEverything) {
                update(context.getString(R.string.downloading_transcript))
            }

            try {
                val transcriptResponse = mcGillService.oldTranscript().execute().body()
                if (transcriptResponse != null) {
                    transcriptDao.insert(transcriptResponse.transcript)
                    transcriptCourseDao.update(transcriptResponse.courses)
                    semesterDao.update(transcriptResponse.semesters)
                }
            } catch (e: IOException) {
                handleException(e, "Transcript")
            }

            val currentTerm = Term.currentTerm()

            // Go through the semesters
            val semesters = semesterDao.getSemesters()
            for (semester in semesters) {
                //Get the currentTerm of this semester
                val term = semester.term

                //If we are not downloading everything, only download it if it's the
                //  current or future currentTerm
                if (downloadEverything || term == currentTerm || term.isAfter(currentTerm)) {
                    if (downloadEverything) {
                        update(
                            context.getString(
                                R.string.downloading_semester,
                                term.getString(context)
                            )
                        )
                    }

                    //Download the schedule
                    try {
                        val courses = mcGillService.schedule(term).execute().body()
                        if (courses != null) {
                            courseDao.update(courses, term)
                        }
                    } catch (e: IOException) {
                        handleException(e, term.id)
                    }
                }
            }

            // Ebill
            if (downloadEverything) {
                update(context.getString(R.string.downloading_ebill))
            }

            GlobalScope.launch(uiDispatcher) {
                try {
                    val response = mcGillService.ebill().await()
                    withContext(bgDispatcher) {
                        statementDao.update(response)
                    }
                } catch (e: IOException) {
                    handleException(e, "Ebill")
                }
            }
            condition.signal()
        }
    }

    /**
     * Handles any exception while downloading the user info
     *
     * @param e Exception instance
     * @param section Section that the exception happened in
     */
    private fun handleException(e: IOException, section: String) {
        if (e is MinervaException) {
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(context)
                .sendBroadcast(Intent(Constants.BROADCAST_MINERVA))
        } else if (e !is SSLException) {
            // Don't log SSLExceptions
            Timber.e(Exception("Exception: $section", e))
        }
        exception = e
    }

    /**
     * Executes the thread synchronously
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun execute() {
        //If we are downloading this synchronously, then we're downloading everything
        downloadEverything = true

        //Start the download
        start()

        //Wait until the thread has been fully executed
        lock.withLock {
            condition.await()
        }

        //If there's an exception, throw it
        exception?.apply { throw this }
    }

    abstract fun update(section: String)
}
