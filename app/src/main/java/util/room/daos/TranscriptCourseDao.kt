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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.guerinet.mymartlet.model.transcript.TranscriptCourse

/**
 * Dao for accessing the TranscriptCourse model
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Dao
abstract class TranscriptCourseDao : BaseDao<TranscriptCourse>() {

    /**
     * Returns the list of [TranscriptCourse]s for the [semesterId]
     */
    @Query("SELECT * FROM TranscriptCourse WHERE semesterId = :semesterId")
    abstract fun get(semesterId: Int): LiveData<List<TranscriptCourse>>

    /**
     * Deletes all of the stored [TranscriptCourse]s
     */
    @Query("DELETE FROM TranscriptCourse")
    abstract fun deleteAll()

    /**
     * Updates the stored [transcriptCourses]
     */
    @Transaction
    open fun update(transcriptCourses: List<TranscriptCourse>) =
        update(transcriptCourses, this::deleteAll)
}