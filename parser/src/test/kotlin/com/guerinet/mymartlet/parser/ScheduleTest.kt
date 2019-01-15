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

import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.Season
import com.guerinet.mymartlet.model.Term
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [parseSchedule]
 */
class ScheduleTest : ParseTestBase() {

    /**
     * Given a valid html segment for student schedule,
     * extract all courses.
     */
    @Test
    fun validCourseList() {
        val courses = getHtml("webpages/schedule-2019-01.html").parseSchedule(debugger)

        val expectedCourses =
            listOf(
                Course(
                    term = Term(Season.WINTER, 2019),
                    subject = "COMP",
                    number = "360",
                    title = "Algorithm Design",
                    crn = 9182,
                    section = "001",
                    startTime = LocalTime.of(8, 35),
                    endTime = LocalTime.of(9, 55),
                    days = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                    type = "Lecture",
                    location = "Stewart Biology Building S1/4",
                    instructor = "Hamed Hatami",
                    credits = 3.0,
                    startDate = LocalDate.of(2018, 1, 8),
                    endDate = LocalDate.of(2018, 4, 16)
                ), Course(
                    term = Term(Season.WINTER, 2019),
                    subject = "COMP", number = "361D2", title = "Software Engineering Project",
                    crn = 9516, section = "001",
                    startTime = LocalTime.of(4, 5), endTime = LocalTime.of(5, 25),
                    days = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                    type = "Lecture", location = "Trottier Building 1100",
                    instructor = "Matthias Johannes Schöttle , Jorg Andreas Kienzle",
                    credits = 3.0,
                    startDate = LocalDate.of(2018, 1, 8),
                    endDate = LocalDate.of(2018, 4, 12)
                ), Course(
                    term = Term(Season.WINTER, 2019),
                    subject = "COMP",
                    number = "409",
                    title = "Concurrent Programming",
                    crn = 15254,
                    section = "001",
                    startTime = LocalTime.of(10, 5),
                    endTime = LocalTime.of(11, 25),
                    days = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                    type = "Lecture",
                    location = "Maass Chemistry Building 217",
                    instructor = "Clark Verbrugge",
                    credits = 3.0,
                    startDate = LocalDate.of(2018, 1, 8),
                    endDate = LocalDate.of(2018, 2, 12)
                ), Course(
                    term = Term(Season.WINTER, 2019),
                    subject = "COMP",
                    number = "409",
                    title = "Concurrent Programming",
                    crn = 15254,
                    section = "001",
                    startTime = LocalTime.of(10, 5),
                    endTime = LocalTime.of(11, 25),
                    days = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                    type = "Lecture",
                    location = "Burnside Hall 1B36",
                    instructor = "Clark Verbrugge",
                    credits = 3.0,
                    startDate = LocalDate.of(2018, 2, 13),
                    endDate = LocalDate.of(2018, 4, 12)
                ), Course(
                    term = Term(Season.WINTER, 2019),
                    subject = "MATH",
                    number = "223",
                    title = "Linear Algebra",
                    crn = 597,
                    section = "001",
                    startTime = LocalTime.of(8, 35),
                    endTime = LocalTime.of(9, 55),
                    days = listOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                    type = "Lecture",
                    location = "Maass Chemistry Building 112",
                    instructor = "Djivede Armel Kelome",
                    credits = 3.0,
                    startDate = LocalDate.of(2018, 1, 8),
                    endDate = LocalDate.of(2018, 4, 12)
                ), Course(
                    term = Term(Season.WINTER, 2019),
                    subject = "MATH",
                    number = "323",
                    title = "Probability",
                    crn = 607,
                    section = "001", startTime = LocalTime.of(1, 5),
                    endTime = LocalTime.of(2, 25),
                    days = listOf(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                    type = "Lecture", location = "Leacock Building 26", instructor = "Chien-Lin Su",
                    credits = 3.0, startDate = LocalDate.of(2018, 1, 8),
                    endDate = LocalDate.of(2018, 4, 13)
                )
            )

        assertEquals(expectedCourses, courses, "Course list mismatch")
    }
}