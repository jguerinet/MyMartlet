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
import android.content.Context
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.model.place.Place

/**
 * Database with all of the config information
 * @author Julien Guerinet
 * @since 2.0.0
 */
@Database(entities = [Category::class, Place::class], version = 1)
abstract class ConfigDb : RoomDatabase() {

//    abstract fun mapDao(): MapDao

    companion object {

        fun init(context: Context): ConfigDb =
                Room.databaseBuilder(context, ConfigDb::class.java, "config-db").build()
    }
}