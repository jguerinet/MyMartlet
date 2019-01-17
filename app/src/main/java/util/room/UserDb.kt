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

package com.guerinet.mymartlet.util.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Statement
import com.guerinet.mymartlet.model.transcript.Transcript
import com.guerinet.mymartlet.model.transcript.TranscriptCourse
import com.guerinet.mymartlet.util.room.converters.DayOfWeekListConverter
import com.guerinet.mymartlet.util.room.converters.TermConverter
import com.guerinet.mymartlet.util.room.daos.CourseDao
import com.guerinet.mymartlet.util.room.daos.CourseResultDao
import com.guerinet.mymartlet.util.room.daos.SemesterDao
import com.guerinet.mymartlet.util.room.daos.StatementDao
import com.guerinet.mymartlet.util.room.daos.TranscriptCourseDao
import com.guerinet.mymartlet.util.room.daos.TranscriptDao
import com.guerinet.room.converter.LocalDateConverter
import com.guerinet.room.converter.LocalTimeConverter

/**
 * Database with all of the user's information
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Database(
    exportSchema = false,
    entities = [Course::class, CourseResult::class, Semester::class, Statement::class,
        Transcript::class, TranscriptCourse::class],
    version = 1
)
@TypeConverters(
    DayOfWeekListConverter::class, LocalDateConverter::class,
    LocalTimeConverter::class, TermConverter::class
)
abstract class UserDb : RoomDatabase() {

    abstract fun courseDao(): CourseDao

    abstract fun courseResultDao(): CourseResultDao

    abstract fun semesterDao(): SemesterDao

    abstract fun statementDao(): StatementDao

    abstract fun transcriptDao(): TranscriptDao

    abstract fun transcriptCourseDao(): TranscriptCourseDao

    companion object {

        fun init(context: Context): UserDb =
            Room.databaseBuilder(context, UserDb::class.java, "user-db").build()
    }
}