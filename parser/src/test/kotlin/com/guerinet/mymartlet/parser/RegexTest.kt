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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Tests for parser.Regex.kt
 *
 * @author Allan Wang
 * @since 2.3.2
 */
class RegexTest {

    private fun Regex.assertMatches(input: String, vararg components: String) {
        val match =
            matchEntire(input)?.groupValues ?: fail("$pattern could not completely match $input")
        assertEquals(
            components.toList(),
            match.subList(1, match.size),
            "$pattern did not extract correct components from $input"
        )
    }

    private fun Regex.assertFind(input: String, vararg components: String) {
        val match = find(input)?.groupValues ?: fail("$pattern could not find $input")
        assertEquals(
            components.toList(),
            match.subList(1, match.size),
            "$pattern did not extract correct components from $input"
        )
    }

    /**
     * Tests for [REGEX_TIME]
     */
    @Test
    fun timeParse() {
        REGEX_TIME.assertMatches("07:10 am", "07", "10", "am")
        REGEX_TIME.assertMatches("1:59 PM", "1", "59", "PM")
        REGEX_TIME.assertFind("3:00am", "3", "00", "am")
    }

    /**
     * Tests for [REGEX_COURSE_NUMBER_SECTION]
     */
    @Test
    fun courseTitleSectionParse() {
        REGEX_COURSE_NUMBER_SECTION.assertMatches(
            "COURSE. -ABCD 123-  001",
            "COURSE",
            "ABCD",
            "123",
            "001"
        )

        REGEX_COURSE_NUMBER_SECTION.assertFind(
            "Multi-word course name. - asdf 123 - 002",
            "Multi-word course name",
            "asdf",
            "123",
            "002"
        )
    }

}