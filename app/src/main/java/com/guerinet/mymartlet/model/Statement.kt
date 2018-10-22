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

package com.guerinet.mymartlet.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.LocalDate

/**
 * One statement in the user's ebill
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 *
 * @param date      Statement date
 * @param dueDate   Due date
 * @param amount    Total amount due or owed
 * @param id        Randomly generated Id for this statement, used as a primary key
 */
@Entity
data class Statement(
        val date: LocalDate,
        val dueDate: LocalDate,
        val amount: Double,
        @PrimaryKey(autoGenerate = true) var id: Int = 0
)