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
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.transcript.Transcript
import com.guerinet.mymartlet.model.transcript.TranscriptCourse

/**
 * Dao for accessing all Transcript related models
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
interface TranscriptDao : BaseDao {

    /**
     * Returns the [Transcript] instance
     */
    @Query("SELECT * FROM Transcript")
    fun getTranscript(): LiveData<Transcript>

    /**
     * Returns the [Semester] with the [semesterId]
     */
    @Query("SELECT * FROM Semester WHERE id = :semesterId")
    fun getSemester(semesterId: Int): LiveData<Semester>

    /**
     * Returns the list of all [Semester]s
     */
    @Query("SELECT * FROM Semester")
    fun getSemesters(): LiveData<List<Semester>>

    /**
     * Returns the list of [TranscriptCourse]s for the [semesterId]
     */
    @Query("SELECT * FROM TranscriptCourse WHERE semesterId = :semesterId")
    fun getTranscriptCourses(semesterId: Int): LiveData<List<TranscriptCourse>>

    /**
     * Updates the [transcript] instance
     */
    @Update
    fun updateTranscript(transcript: Transcript)

    /**
     * Deletes all of the stored [Semester]s
     */
    @Query("DELETE FROM Semester")
    fun deleteSemesters()

    /**
     * Adds the list of [semesters]
     */
    @Insert
    fun addSemesters(semesters: List<Semester>)

    /**
     * Updates the stored [semesters]
     */
    @Transaction
    fun updateSemesters(semesters: List<Semester>) =
            update(semesters, this::deleteSemesters, this::addSemesters)

    /**
     * Deletes all of the stored [TranscriptCourse]s
     */
    @Query("DELETE FROM TranscriptCourse")
    fun deleteTranscriptCourses()

    /**
     * Adds the list of [transcriptCourses]
     */
    @Insert
    fun addTranscriptCourses(transcriptCourses: List<TranscriptCourse>)

    /**
     * Updates the stored [transcriptCourses]
     */
    @Transaction
    fun updateTranscriptCourses(transcriptCourses: List<TranscriptCourse>) =
            update(transcriptCourses, this::deleteTranscriptCourses, this::addTranscriptCourses)
}