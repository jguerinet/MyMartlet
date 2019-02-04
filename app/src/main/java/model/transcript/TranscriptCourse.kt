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

package com.guerinet.mymartlet.model.transcript

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Term

/**
 * A course that is part of the transcript
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @property semesterId Id of the [Semester] this is for
 * @property term Course currentTerm
 * @property courseCode Course code (e.g. ECSE 428)
 * @property courseTitle Course title
 * @property credits Course credits
 * @property userGrade User's grade in this course
 * @property averageGrade Average grade in this course
 * @property id Self managed Id, used as primary key
 */
@Entity
data class TranscriptCourse(
    val semesterId: Int,
    val term: Term,
    val courseCode: String,
    val courseTitle: String,
    val credits: Double,
    val userGrade: String,
    val averageGrade: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)
