/*
 * Copyright 2014-2019 Julien Guerinet
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

package com.guerinet.mymartlet.parser

import org.threeten.bp.DayOfWeek
import kotlin.test.Test
import kotlin.test.fail

/**
 * Tests for parser.General.kt
 */
class GeneralTest {

    /**
     * While not all characters have an associated day,
     * all [DayOfWeek] should have an associated character
     */
    @Test
    fun `all DayOfWeek enums accounted for`() {
        DayOfWeek.values().forEach {
            try {
                DayUtils.dayToChar(it)
            } catch (e: Exception) {
                fail("Could not get associated char for ${it.name}")
            }
        }
    }

}