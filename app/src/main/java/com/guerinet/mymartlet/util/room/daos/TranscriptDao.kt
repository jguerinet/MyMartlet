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

package com.guerinet.mymartlet.util.room.daos

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import android.arch.persistence.room.Update
import com.guerinet.mymartlet.model.transcript.Transcript
import com.guerinet.mymartlet.model.transcript.TranscriptCourse

/**
 * Dao for accessing all Transcript related models
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
abstract class TranscriptDao : BaseDao<Transcript>() {

    /**
     * Returns the [Transcript] instance
     */
    @Query("SELECT * FROM Transcript")
    abstract fun getTranscript(): LiveData<Transcript>

    /**
     * Returns the list of [TranscriptCourse]s for the [semesterId]
     */
    @Query("SELECT * FROM TranscriptCourse WHERE semesterId = :semesterId")
    abstract fun getTranscriptCourses(semesterId: Int): LiveData<List<TranscriptCourse>>

    /**
     * Updates the [transcript] instance
     */
    @Update
    abstract fun updateTranscript(transcript: Transcript)

    /**
     * Deletes all of the stored [TranscriptCourse]s
     */
    @Query("DELETE FROM TranscriptCourse")
    abstract fun deleteTranscriptCourses()

    /**
     * Adds the list of [transcriptCourses]
     */
    @Insert
    abstract fun addTranscriptCourses(transcriptCourses: List<TranscriptCourse>)

    /**
     * Updates the stored [transcriptCourses]
     */
    @Transaction
    open fun updateTranscriptCourses(transcriptCourses: List<TranscriptCourse>) {
    }
//            update(transcriptCourses, this::deleteTranscriptCourses, this::addTranscriptCourses)
}