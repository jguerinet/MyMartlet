/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.transcript.Transcript
import com.guerinet.mymartlet.util.retrofit.McGillService
import com.guerinet.mymartlet.util.room.daos.SemesterDao
import com.guerinet.mymartlet.util.room.daos.TranscriptCourseDao
import com.guerinet.mymartlet.util.room.daos.TranscriptDao

/**
 * [ViewModel] for the transcript section
 * @author Julien Guerinet
 * @since 2.0.0
 */
class TranscriptViewModel(
    private val semesterDao: SemesterDao,
    private val transcriptDao: TranscriptDao,
    private val transcriptCourseDao: TranscriptCourseDao,
    private val mcGillService: McGillService
) : BaseViewModel() {

    val transcript: LiveData<Transcript> by lazy { transcriptDao.get() }

    val semesters: LiveData<List<Semester>> by lazy { semesterDao.getAll() }

    /**
     * Refreshes the data by requesting it from McGill
     */
    suspend fun refresh(): Exception? = update {
        try {
            // Make the request
            val response = mcGillService.transcript().execute().body()
                ?: return@update Exception("Body was null")

            // Save the response
            transcriptDao.update(response.transcript)
            semesterDao.update(response.semesters)
            transcriptCourseDao.update(response.courses)
            return@update null
        } catch (e: Exception) {
            return@update e
        }
    }
}