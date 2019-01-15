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

package com.guerinet.mymartlet.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for [Term]
 */
class TermTest {

    /**
     * Terms are used as part of the url for course schedules.
     */
    @Test
    fun termToString() {
        assertEquals("201809", Term(Season.FALL, 2018).toString())
        assertEquals("201901", Term(Season.WINTER, 2019).toString())
        assertEquals("201805", Term(Season.SUMMER, 2018).toString())
    }

    /**
     * Ensures that term comparisons are valid.
     * Comparisons should be based on chronological order.
     */
    @Test
    fun comparisons() {
        assertTrue(
            Term(Season.FALL, 2018) < Term(Season.FALL, 2019),
            "Term with later year should be greater"
        )
        assertTrue(
            Term(Season.WINTER, 2018) < Term(Season.SUMMER, 2018),
            "Term with later season and same year should be greater"
        )
        assertTrue(
            Term(Season.FALL, 2018) < Term(Season.WINTER, 2019),
            "Term with later year and earlier season should be greater"
        )
    }
}