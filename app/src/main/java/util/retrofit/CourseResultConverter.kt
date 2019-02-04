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

package com.guerinet.mymartlet.util.retrofit

import androidx.core.util.Pair
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.DayUtils
import com.squareup.moshi.Types
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.ArrayList

/**
 * Retrofit converter to parse a list of course results when searching for courses
 * @author Julien Guerinet
 * @since 2.2.0
 */
class CourseResultConverter : Converter.Factory(), Converter<ResponseBody, List<CourseResult>> {

    /** [ParameterizedType] representing a list of [CourseResult]s */
    private val type = Types.newParameterizedType(List::class.java, CourseResult::class.java)

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (type?.toString() != this.type.toString()) {
            //This can only convert a list of course results
            null
        } else CourseResultConverter()
    }

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): List<CourseResult> {
        val html = value.string()
        val courses = ArrayList<CourseResult>()
        val document = Jsoup.parse(html, "UTF-8")
        //Parse the response body into a list of rows
        val rows = document.getElementsByClass("dddefault")

        // Parse the currentTerm from the page header
        val header = document.getElementsByClass("staticheaders")[0]
        val term = Term.parseTerm(header.childNode(2).toString())

        // Get the table in the form of a set of rows
        val table = document.getElementsByClass("datadisplaytable")[0].select("tbody")[0]

        // Go through the rows in the table
        for (row in table.select("tr")) {
            // Check that there at least 19 elements in the row
            val rowElements = row.select("td")
            if (rowElements.size < 19) {
                // If there aren't, it must not be a course row
                continue
            }

            // Create a new course object with the default values
            var credits = 99.0
            var subject: String? = null
            var number: String? = null
            var title = ""
            var type = ""
            val days = ArrayList<DayOfWeek>()
            var crn = 0
            var instructor = ""
            var location = ""
            //So that the rounded start time will be 0
            var startTime = ScheduleConverter.defaultStartTime
            var endTime = ScheduleConverter.defaultEndTime
            var capacity = 0
            var seatsRemaining = 0
            var waitlistRemaining = 0
            var startDate: LocalDate? = LocalDate.now()
            var endDate: LocalDate? = LocalDate.now()

            try {
                var i = 0
                while (i < rowElements.size) {
                    if (rowElements[i].toString().contains("&nbsp;")) {
                        // Empty row: continue
                        i++
                        continue
                    }
                    var rowString = rowElements[i].text()

                    when (i) {
                        // CRN
                        1 -> crn = Integer.parseInt(rowString)
                        // Subject
                        2 -> subject = rowString
                        // Number
                        3 -> number = rowString
                        // Type
                        5 -> type = rowString
                        // Number of credits
                        6 -> credits = java.lang.Double.parseDouble(rowString)
                        // Course title
                        7 ->
                            //Remove the extra period at the end of the course title
                            title = rowString.substring(0, rowString.length - 1)
                        // Days of the week
                        8 -> if (rowString == "TBA") {
                            // TBA Stuff: no time associated so skip the next one
                            // and add a dummy to keep the index correct
                            rowElements.add(9, null)
                            i++
                        } else {
                            // Day Parsing
                            rowString = rowString.replace('\u00A0', ' ').trim { it <= ' ' }
                            for (k in 0 until rowString.length) {
                                days.add(DayUtils.getDay(rowString[k]))
                            }
                        }
                        // Time
                        9 -> {
                            val times =
                                rowString.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            try {
                                var startHour =
                                    Integer.parseInt(
                                        times[0].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split(
                                            ":".toRegex()
                                        ).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                                    )
                                val startMinute =
                                    Integer.parseInt(
                                        times[0].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split(
                                            ":".toRegex()
                                        ).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                                    )
                                var endHour =
                                    Integer.parseInt(
                                        times[1].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split(
                                            ":".toRegex()
                                        ).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                                    )
                                val endMinute =
                                    Integer.parseInt(
                                        times[1].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split(
                                            ":".toRegex()
                                        ).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                                    )

                                //If it's PM, then add 12 hours to the hours for 24 hours format
                                //Make sure it isn't noon
                                val startPM =
                                    times[0].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                                if (startPM == "PM" && startHour != 12) {
                                    startHour += 12
                                }

                                val endPM =
                                    times[1].split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                                if (endPM == "PM" && endHour != 12) {
                                    endHour += 12
                                }

                                startTime = LocalTime.of(startHour, startMinute)
                                endTime = LocalTime.of(endHour, endMinute)
                            } catch (e: NumberFormatException) {
                                //Courses sometimes don't have assigned times
                                startTime = ScheduleConverter.defaultStartTime
                                endTime = ScheduleConverter.defaultEndTime
                            }
                        }
                        // Capacity
                        10 -> capacity = Integer.parseInt(rowString)
                        // Seats remaining
                        12 -> seatsRemaining = Integer.parseInt(rowString)
                        // Waitlist remaining
                        15 -> waitlistRemaining = Integer.parseInt(rowString)
                        // Instructor
                        16 -> instructor = rowString
                        // Start/end date
                        17 -> {
                            val dates = parseDateRange(term, rowString)
                            startDate = dates.first
                            endDate = dates.second
                        }
                        // Location
                        18 -> location = rowString
                    }
                    i++
                }
            } catch (e: Exception) {
                Timber.e(e, "Course Results Parser Error")
            }

            // Don't add any courses with errors
            if (subject != null && number != null) {
                // Create a new course object and add it to list
                // TODO Should we be parsing the course section?
                courses.add(
                    CourseResult(
                        term, subject, number, title, crn, "", startTime,
                        endTime, days, type, location, instructor, credits, startDate!!, endDate!!,
                        capacity, seatsRemaining, waitlistRemaining
                    )
                )
            }
        }

        return courses
    }

    /**
     * Parses a String into a LocalDate object
     *
     * @param term Current currentTerm
     * @param date The date String
     * @return The corresponding local date
     */
    fun parseDate(term: Term, date: String): LocalDate {
        val dateFields = date.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return LocalDate.of(
            term.year, Integer.parseInt(dateFields[0]),
            Integer.parseInt(dateFields[1])
        )
    }

    /**
     * Parses the date range String into 2 dates
     *
     * @param term Current currentTerm
     * @param dateRange The date range String
     * @return A pair representing the starting and ending dates of the range
     * @throws IllegalArgumentException
     */
    @Throws(IllegalArgumentException::class)
    fun parseDateRange(term: Term, dateRange: String): Pair<LocalDate, LocalDate> {
        //Split the range into the 2 date Strings
        val dates = dateRange.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val startDate = dates[0].trim { it <= ' ' }
        val endDate = dates[1].trim { it <= ' ' }

        //Parse the dates, return them as a pair
        return Pair(parseDate(term, startDate), parseDate(term, endDate))
    }
}