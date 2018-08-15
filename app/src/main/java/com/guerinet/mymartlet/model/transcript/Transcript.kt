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

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * User's unofficial transcript
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 *
 * @param totalCredits  User's total number of credits
 * @param cgpa          User's cumulative GPA
 * @param id            Transcript Id
 */
@Entity
class Transcript(
        val cgpa: Double,
        val totalCredits: Double,
        // Note: I'm putting this at the end as it has a default value of 0 (only 1 transcript)
        @PrimaryKey val id: Int = 0
)