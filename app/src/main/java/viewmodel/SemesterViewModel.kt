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

import androidx.lifecycle.ViewModel
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.transcript.TranscriptCourse
import com.guerinet.mymartlet.util.room.daos.TranscriptDao

/**
 * [ViewModel] for the [Semester]
 * @author Julien Guerinet
 * @since 2.0.0
 */
class SemesterViewModel(private val transcriptDao: TranscriptDao) : BaseViewModel() {

    /**
     * Returns the observable [Semester] for the [semesterId]
     */
    fun getSemester(semesterId: Int) = transcriptDao.getSemester(semesterId)

    /**
     * Returns the observable list of [TranscriptCourse]s for the [semesterId]
     */
    fun getTranscriptCourses(semesterId: Int) = transcriptDao.getTranscriptCourses(semesterId)
}