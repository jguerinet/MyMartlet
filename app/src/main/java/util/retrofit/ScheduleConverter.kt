/*
 * Copyright 2014-2022 Julien Guerinet
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
import com.guerinet.mymartlet.model.Course
import com.guerinet.mymartlet.model.LocalTime
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.util.DayUtils
import com.squareup.moshi.Types
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.toKotlinLocalDate
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Retrofit converter to parse the user's schedule as a list of courses (for one currentTerm)
 * @author Julien Guerinet
 * @since 1.0.0
 */
class ScheduleConverter : Converter.Factory(), Converter<ResponseBody, List<Course>> {

    /** [ParameterizedType] representing a list of [Course]s */
    private val type = Types.newParameterizedType(List::class.java, Course::class.java)

    /** [DateTimeFormatter] instance to parse dates */
    private val dtf: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.US)

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (type?.toString() != this.type.toString()) {
            //This can only convert a list of courses
            null
        } else ScheduleConverter()
    }

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): List<Course> = convert(value.string())

    /**
     * Returns the list of parsed [Course]s from the [html]
     */
    private fun convert(html: String): List<Course> {
        val courses = mutableListOf<Course>()

        //Parse the body into a Document
        val scheduleTable = Jsoup.parse(html).getElementsByClass("datadisplaytable")

        // Go through the schedule table
        var i = 0
        while (i < scheduleTable.size) {
            // Get the current row in the schedule table
            val row = scheduleTable[i]

            //Course title, code, and section
            val texts = row.getElementsByTag("caption").first().text().split(" - ".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()
            val title = texts[0].substring(0, texts[0].length - 1)
            val code = texts[1]
            val section = texts[2]

            //Parse the subject from the code
            var subject = ""
            try {
                subject = code.substring(0, 4)
            } catch (e: StringIndexOutOfBoundsException) {
                Timber.e(e, "Schedule Parser Error: Subject")
            }

            var number = ""
            try {
                number = code.substring(5, 8)
            } catch (e: StringIndexOutOfBoundsException) {
                Timber.e(e, "Schedule Parser Error: Number")
            }

            //CRN
            val crnString = row.getElementsByTag("tr")[1].getElementsByTag("td").first()
                .text()
            var crn = -1
            try {
                crn = Integer.parseInt(crnString)
            } catch (e: NumberFormatException) {
                Timber.e(e, "Schedule Parser Error: CRN")
            }

            //Credits
            val creditString = row.getElementsByTag("tr")[5].getElementsByTag("td").first()
                .text()
            var credits = -1.0
            try {
                credits = java.lang.Double.parseDouble(creditString)
            } catch (e: NumberFormatException) {
                Timber.e(e, "Schedule Parser Error: Credits")
            }

            //Time, Days, Location, Type, Instructor
            if (i + 1 < scheduleTable.size && scheduleTable[i + 1].attr("summary") == "This table lists the scheduled meeting times and assigned " + "instructors for this class..") {

                //Get the rows with the schedule times
                val timeRows = scheduleTable[i + 1].getElementsByTag("tr")
                for (j in 1 until timeRows.size) {
                    //Get all of the cells of the current rows
                    val cells = timeRows[j].getElementsByTag("td")

                    var times = arrayOf<String>()
                    val days = ArrayList<DayOfWeek>()
                    var location = ""
                    var dateRange = ""
                    var type = ""
                    var instructor = ""

                    try {
                        times = cells[0].text().split(" - ".toRegex())
                            .dropLastWhile { it.isEmpty() }.toTypedArray()

                        //Day Parsing
                        val dayString = cells[1].text().replace('\u00A0', ' ').trim { it <= ' ' }
                        for (k in 0 until dayString.length) {
                            days.add(DayUtils.getDay(dayString[k]))
                        }

                        location = cells[2].text()
                        dateRange = cells[3].text()
                        type = cells[4].text()
                        instructor = cells[5].text()
                    } catch (e: IndexOutOfBoundsException) {
                        Timber.e(e, "Schedule Parser Error: Course Info")
                    }

                    //Time parsing
                    var startTime: LocalTime
                    var endTime: LocalTime
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
                        if ((startPM == "PM" || startPM == "pm") && startHour != 12) {
                            startHour += 12
                        }

                        val endPM =
                            times[1].split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()[1]
                        if ((endPM == "PM" || endPM == "pm") && endHour != 12) {
                            endHour += 12
                        }

                        startTime = LocalTime(startHour, startMinute)
                        endTime = LocalTime(endHour, endMinute)
                    } catch (e: NumberFormatException) {
                        //Some classes don't have assigned times
                        startTime = defaultStartTime
                        endTime = defaultEndTime
                    }

                    //Date Range parsing
                    var startDate: LocalDate
                    var endDate: LocalDate
                    try {
                        val dates = parseDateRange(dateRange)
                        startDate = dates.first ?: throw IllegalArgumentException("Null start date")
                        endDate = dates.second ?: throw IllegalArgumentException("Null end date")
                    } catch (e: IllegalArgumentException) {
                        Timber.e(e, "Schedule Parser Error: Date Range")
                        //Use today as the date if there's an error
                        startDate = LocalDate.now()
                        endDate = LocalDate.now()
                    }

                    //Add the course
                    courses.add(
                        Course(
                            // Placeholder term, will be replaced
                            Term.currentTerm(),
                            subject, number, title, crn, section, startTime, endTime,
                            days, type, location, instructor, credits,
                            startDate.toKotlinLocalDate(), endDate.toKotlinLocalDate()
                        )
                    )
                }
            } else {
                //If there is no data to parse, reset i and continue
                i--
            }
            i += 2
        }

        return courses
    }

    /**
     * Parses the date range String into 2 dates
     *
     * @param dateRange The date range String
     * @return A pair representing the starting and ending dates of the range
     */
    @Throws(IllegalArgumentException::class)
    fun parseDateRange(dateRange: String): Pair<LocalDate, LocalDate> {
        //Split the range into the 2 date Strings
        val dates = dateRange.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val startDate = dates[0].trim { it <= ' ' }
        val endDate = dates[1].trim { it <= ' ' }

        //Parse the dates, return them as a pair
        return Pair(LocalDate.parse(startDate, dtf), LocalDate.parse(endDate, dtf))
    }

    companion object {

        /**
         * @return A start time that will yield 0 for the rounded start time
         */
        val defaultStartTime: LocalTime
            get() = LocalTime(0, 5)

        /**
         * @return An end time that will yield 0 for the rounded end time
         */
        val defaultEndTime: LocalTime
            get() = LocalTime(0, 55)
    }
}
