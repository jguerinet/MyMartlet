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

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Statement
import com.guerinet.mymartlet.model.transcript.Transcript
import com.guerinet.mymartlet.model.transcript.TranscriptCourse
import com.guerinet.mymartlet.util.room.converters.LocalDateConverter
import com.guerinet.mymartlet.util.room.converters.TermConverter
import com.guerinet.mymartlet.util.room.daos.StatementDao
import com.guerinet.mymartlet.util.room.daos.TranscriptDao

/**
 * Database with all of the user's information
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Database(entities = [Semester::class, Statement::class, Transcript::class,
    TranscriptCourse::class], version = 1)
@TypeConverters(LocalDateConverter::class, TermConverter::class)
abstract class UserDb : RoomDatabase() {

    abstract fun ebillDao(): StatementDao

    abstract fun transcriptDao(): TranscriptDao

    companion object {

        fun init(context: Context): UserDb =
                Room.databaseBuilder(context, UserDb::class.java, "user-db").build()
    }
}