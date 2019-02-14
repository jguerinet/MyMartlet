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

import com.guerinet.mymartlet.model.Statement
import org.threeten.bp.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [parseSchedule]
 *
 * @author Allan Wang
 * @since 2.3.2
 */
class EbillTest : ParseTestBase() {

    /**
     * Given a valid html segment for student schedule,
     * extract all courses.
     */
    @Test
    fun validStatementList() {
        val statements = getHtml("webpages/ebill-2019-02.html").parseEbill(debugger)

        val expectedStatements = listOf(
            Statement(
                date = LocalDate.of(2019, 2, 7),
                dueDate = LocalDate.of(2019, 2, 28),
                amount = 0.0
            ),
            Statement(
                date = LocalDate.of(2019, 1, 9),
                dueDate = LocalDate.of(2019, 1, 31),
                amount = -1234.56
            ),
            Statement(
                date = LocalDate.of(2018, 12, 10),
                dueDate = LocalDate.of(2019, 1, 4),
                amount = 1333.33
            ),
            Statement(date = LocalDate.of(2018, 10, 5), dueDate = LocalDate.of(2018, 10, 31), amount = 0.0),
            Statement(
                date = LocalDate.of(2018, 8, 6),
                dueDate = LocalDate.of(2018, 8, 31),
                amount = 9135148.75
            ),
            Statement(date = LocalDate.of(2018, 1, 10), dueDate = LocalDate.of(2018, 1, 31), amount = 0.0),
            Statement(
                date = LocalDate.of(2017, 11, 6),
                dueDate = LocalDate.of(2017, 11, 30),
                amount = -27.37
            ),
            Statement(
                date = LocalDate.of(2017, 10, 6),
                dueDate = LocalDate.of(2017, 10, 31),
                amount = -27.37
            ),
            Statement(
                date = LocalDate.of(2017, 9, 8),
                dueDate = LocalDate.of(2017, 9, 29),
                amount = 0.0
            ),
            Statement(date = LocalDate.of(2017, 6, 5), dueDate = LocalDate.of(2017, 6, 29), amount = -29.6),
            Statement(
                date = LocalDate.of(2017, 5, 4),
                dueDate = LocalDate.of(2017, 5, 30),
                amount = -29.6
            ),
            Statement(date = LocalDate.of(2017, 1, 10), dueDate = LocalDate.of(2017, 1, 31), amount = 0.0),
            Statement(
                date = LocalDate.of(2016, 12, 7),
                dueDate = LocalDate.of(2017, 1, 5),
                amount = 111111.11
            )
        )

        assertEquals(expectedStatements, statements, "Statement list mismatch")
    }
}
