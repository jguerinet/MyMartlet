///*
// * Copyright 2014-2019 Julien Guerinet
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.guerinet.mymartlet.util.retrofit
//
//import com.guerinet.mymartlet.model.CourseResult
//import com.guerinet.mymartlet.model.Term
//import com.guerinet.mymartlet.util.DayUtils
//import com.squareup.moshi.Types
//import com.sun.xml.internal.ws.commons.xmlutil.Converter
//import okhttp3.ResponseBody
//import org.jsoup.Jsoup
//import org.jsoup.select.Elements
//import org.threeten.bp.DayOfWeek
//import org.threeten.bp.LocalDate
//import org.threeten.bp.LocalTime
//import retrofit2.Converter
//import retrofit2.Retrofit
//import timber.log.Timber
//import java.io.IOException
//import java.lang.reflect.ParameterizedType
//import java.lang.reflect.Type
//import java.time.DayOfWeek
//import java.time.LocalTime
//
///**
// * Retrofit converter to parse a list of course results when searching for courses
// * @author Julien Guerinet
// * @since 2.2.0
// */
//class CourseResultConverter : Converter.Factory(), Converter<ResponseBody, List<CourseResult>> {
//
//    /** [ParameterizedType] representing a list of [CourseResult]s */
//    private val type = Types.newParameterizedType(List::class.java, CourseResult::class.java)
//
//    /**
//     * Returns self assuming that the type matches
//     */
//    override fun responseBodyConverter(
//        type: Type, annotations: Array<Annotation>,
//        retrofit: Retrofit
//    ): Converter<ResponseBody, *>? = takeIf {
//        type.toString() != this.type.toString()
//    }
//
//    private val timeParser = Regex("([0-9]+):([0-9]+) (am|pm)", RegexOption.IGNORE_CASE)
//
//    private fun String.getTime(): LocalTime? {
//        val matches = timeParser.find(this)?.groupValues ?: return null
//        var hour = matches[0].toInt()
//        val minute = matches[1].toInt()
//        val pm = matches[2].equals("pm", ignoreCase = true)
//        // If it's PM, then add 12 hours to the hours for 24 hours format
//        // Make sure it isn't noon
//        // We can ignore midnight as there are no classes then
//        if (pm && hour != 12) {
//            hour += 12
//        }
//        return LocalTime.of(hour, minute)
//    }
//
//    private fun Elements.parseCourse(term: Term): CourseResult? {
//        if (size < 19)
//        // Not enough elements
//            return null
//
//        // Create a new course object with the default values
//        var credits = 99.0
//        var subject: String? = null
//        var number: String? = null
//        var title = ""
//        var type = ""
//        var days: List<DayOfWeek> = emptyList()
//        var crn = 0
//        var instructor = ""
//        var location = ""
//        //So that the rounded start time will be 0
//        var startTime = ScheduleConverter.defaultStartTime
//        var endTime = ScheduleConverter.defaultEndTime
//        var capacity = 0
//        var seatsRemaining = 0
//        var waitlistRemaining = 0
//        var startDate = LocalDate.now()
//        var endDate = LocalDate.now()
//
//        // TODO note that the only cause of failure here is now the DayUtils conversion
//        try {
//            // Note that a while loop is necessary as the list may be modified during the loop
//            var i = 0
//            while (i < size) {
//                val element = getOrNull(i) ?: break
//                if (element.toString().contains("&nbsp;")) {
//                    // Empty row: continue
//                    i++
//                    continue
//                }
//
//                val rowString: String = element.text()
//
//                when (i) {
//                    // CRN
//                    1 -> crn = rowString.toIntOrNull() ?: return null
//                    // Subject
//                    2 -> subject = rowString
//                    // Number
//                    3 -> number = rowString
//                    // Type
//                    5 -> type = rowString
//                    // Number of credits
//                    6 -> credits = rowString.toDoubleOrNull() ?: return null
//                    // Course title
//                    7 -> title = rowString.trimEnd('.')
//                    // Days of the week
//                    8 -> if (rowString == "TBA") {
//                        // TBA Stuff: no time associated so skip the next one
//                        // and add a dummy to keep the index correct
//                        add(9, null)
//                        i++
//                    } else {
//                        // Day Parsing
//                        days = rowString.replace('\u00A0', ' ').trim().map { DayUtils.getDay(it) }
//                    }
//                    // Time
//                    9 -> {
//                        val times =
//                            rowString.split("-", limit = 3)
//
//                        startTime = times.getOrNull(0)?.getTime() ?: startTime
//                        endTime = times.getOrNull(0)?.getTime() ?: endTime
//                    }
//                    // Capacity
//                    10 -> capacity = rowString.toIntOrNull() ?: return null
//                    // Seats remaining
//                    12 -> seatsRemaining = rowString.toIntOrNull() ?: return null
//                    // Waitlist remaining
//                    15 -> waitlistRemaining = rowString.toIntOrNull() ?: return null
//                    // Instructor
//                    16 -> instructor = rowString
//                    // Start/end date
//                    17 -> {
//                        val dates = parseDateRange(term, rowString)
//                        startDate = dates.first
//                        endDate = dates.second
//                    }
//                    // Location
//                    18 -> location = rowString
//                }
//                i++
//            }
//        } catch (e: Exception) {
//            Timber.e(e, "Course Results Parser Error")
//            return null
//        }
//
//        // Validate course
//        if (subject == null || number == null)
//            return null
//
//        return CourseResult(
//            term, subject, number, title, crn, "", startTime,
//            endTime, days, type, location, instructor, credits, startDate, endDate,
//            capacity, seatsRemaining, waitlistRemaining
//        )
//    }
//
//    @Throws(IOException::class)
//    override fun convert(value: ResponseBody): List<CourseResult> {
//        val html = value.string()
//        val document = Jsoup.parse(html, "UTF-8")
//        //Parse the response body into a list of rows
//        // todo unused
//        val rows = document.getElementsByClass("dddefault")
//
//        // Parse the currentTerm from the page header
//        val header = document.getElementsByClass("staticheaders")[0]
//        val term = Term.parseTerm(header.childNode(2).toString())
//
//        // Get the table in the form of a set of rows
//        val table = document.getElementsByClass("datadisplaytable")[0].select("tbody")[0]
//
//        // Go through the rows in the table
//        return table.select("tr").map { it.select("td") }.mapNotNull { it.parseCourse(term) }
//    }
//
//    /**
//     * Parses a String into a LocalDate object
//     *
//     * TODO should just use a regex and return null if not found?
//     *
//     * @param term Current currentTerm
//     * @param date The date String
//     * @return The corresponding local date
//     */
//    @Throws(Exception::class)
//    fun parseDate(term: Term, date: String): LocalDate {
//        val (month, day) = date.split("/")
//        return LocalDate.of(
//            term.year, month.toInt(),
//            day.toInt()
//        )
//    }
//
//    /**
//     * Parses the date range String into 2 dates
//     *
//     * @param term      Current currentTerm
//     * @param dateRange The date range String
//     * @return A pair representing the starting and ending dates of the range
//     * @throws IllegalArgumentException
//     */
//    @Throws(IllegalArgumentException::class)
//    fun parseDateRange(term: Term, dateRange: String): Pair<LocalDate, LocalDate> {
//        //Split the range into the 2 date Strings
//        val dates = dateRange.split("-")
//        val startDate = dates[0].trim()
//        val endDate = dates[1].trim()
//
//        //Parse the dates, return them as a pair
//        return parseDate(term, startDate) to parseDate(term, endDate)
//    }
//}
