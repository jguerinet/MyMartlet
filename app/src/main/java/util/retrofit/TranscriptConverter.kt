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

import com.guerinet.mymartlet.model.Season
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.model.transcript.Transcript
import com.guerinet.mymartlet.model.transcript.TranscriptCourse
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type

/**
 * Retrofit converter to parse the user's transcript
 * @author Julien Guerinet
 * @since 1.0.0
 */
class TranscriptConverter : Converter.Factory(),
    Converter<ResponseBody, TranscriptConverter.TranscriptResponse> {

    override fun responseBodyConverter(
        type: Type?, annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (type != TranscriptResponse::class.java) {
            // This can only convert transcripts
            null
        } else TranscriptConverter()
    }

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): TranscriptResponse {
        // Parse ResponseBody HTML String into a document
        val rows = Jsoup.parse(value.string()).getElementsByClass("fieldmediumtext")

        /*
         * Main loop:
         * This will iterate through every row of the transcript data and check for various tokens
         * Once a match is found, the value in the appropriate row will be saved to a variable
         */
        val semesters = mutableListOf<Semester>()
        val courses = mutableListOf<TranscriptCourse>()
        var cgpa = -1.0
        var totalCredits = -1.0
        var semesterId = 0

        for (index in rows.indices) {
            var text = rows[index].text()
            // Check the text at the start of the row
            //  If it matches one of the tokens, take the corresponding data out
            //  of one of the following rows, depending on the HTML layout

            if (text.startsWith("CUM GPA")) {
                // CGPA
                runCodeWithException("CGPA") {
                    cgpa = rows[index + 1].text().toDouble()
                }
            } else if (text.startsWith("TOTAL CREDITS:")) {
                // Credits
                runCodeWithException("Total Credits") {
                    totalCredits = rows[index + 1].text().toDouble()
                }
            } else if (isSemesterStart(text)) {
                // Semester
                var year = -1
                var program = ""
                var bachelor = ""
                var termCredits = 0.0
                var termGPA = 0.0
                var isFullTime = false
                var hasCourse = false

                // Divide the semester info into separate items
                val semesterItems = text.trim().split("\\s+".toRegex())

                // Find the right season and year, making sure to get the right array index
                val (season, yearString) = when {
                    // Normal Semester that starts with a season name
                    text.startsWith(Season.FALL.title) ||
                        text.startsWith(Season.WINTER.title) ||
                        text.startsWith(Season.SUMMER.title) ->
                        Pair(Season.getSeasonFromTitle(semesterItems[0]), semesterItems[1])
                    // Change Semester
                    text.startsWith("Change") -> Pair(
                        Season.getSeasonFromTitle(semesterItems[3]),
                        semesterItems[4]
                    )
                    // Readmitted
                    else -> Pair(Season.getSeasonFromTitle(semesterItems[1]), semesterItems[2])
                }

                runCodeWithException("Semester Year") {
                    year = yearString.toInt()
                }

                // Increment the index before starting the semester loop
                var semesterIndex = index + 1

                // Search rows until the end of the semester is reached
                // Conditions for end of semester:
                //  1. End of transcript is reached
                //  2. The words "Fall" "Summer" or "Winter" appear
                while (true) {
                    // Get the current data row text
                    text = rows[semesterIndex].text()

                    if (text.contains("Granted")) {
                        // Student has graduated
                        break
                    } else if (text.startsWith("Dip") || text.startsWith("Bachelor") ||
                        text.startsWith("Master") || text.startsWith("Doctor")
                    ) {
                        // Semester Info

                        // Example strings:
                        //  "Bachelor&nbps;of&nbsp;Engineering"<br>
                        //  "Full-time&nbsp;Year&nbsp;0"<br>
                        //  "Electrical&nbsp;Engineering"

                        val degreeDetails = text.split(" ")

                        bachelor = degreeDetails[0]

                        // Check if student is full time
                        if (degreeDetails[1].startsWith("Full-time")) {
                            isFullTime = true
                        }

                        // Skip first two lines: these are for bachelor and full time/part time
                        for (i in 2 until degreeDetails.size) {
                            program += degreeDetails[i] + " "
                        }
                    } else if (text.startsWith("TERM GPA")) {
                        // Term GPA
                        runCodeWithException("Term GPA") {
                            termGPA = rows[semesterIndex + 1].text().toDouble()
                        }
                    } else if (text.startsWith("TERM TOTALS:")) {
                        // Term Credits
                        runCodeWithException("Term Credits") {
                            termCredits = rows[semesterIndex + 2].text().toDouble()
                        }
                    } else if (text.matches("[A-Za-z]{4} [0-9]{3}.*".toRegex()) ||
                        text.matches("[A-Za-z]{3}[0-9] [0-9]{3}".toRegex()) ||
                        text.startsWith("Credits/Exemptions")
                    ) {
                        // Course Info
                        var title = ""
                        var code = ""
                        var grade = "N/A"
                        var averageGrade = ""
                        var credits = -1.0

                        // Extract course information if row contains a course code
                        //  Regex looks for a string in the form "ABCD ###"
                        if (text.matches("[A-Za-z]{4} [0-9]{3}.*".toRegex()) ||
                            text.matches("[A-Za-z]{3}[0-9] [0-9]{3}".toRegex())
                        ) {
                            if (text.matches("[A-Za-z]{4} [0-9]{3}".toRegex()) ||
                                text.matches("[A-Za-z]{3}[0-9] [0-9]{3}".toRegex())
                            ) {
                                // One semester courses are in the form ABCD ###
                                // Some courses have the form ABC#
                                code = text
                            } else {
                                // Multi semester courses are in the form ABCD ###D#
                                runCodeWithException("Course Code") {
                                    // Extract first seven characters from string
                                    code = text.substring(0, 10)
                                }
                            }

                            title = rows[semesterIndex + 2].text()

                            // Failed courses are missing the earned credits row
                            //  Check row to see if earned credit exists
                            runCodeWithException {
                                credits = rows[semesterIndex + 6].text().toDouble()
                            }

                            // Obtain user's grade
                            grade = rows[semesterIndex + 4].text()

                            // Check for deferred classes
                            if (grade == "L") {
                                grade = rows[semesterIndex + 13].text()
                            }

                            // If average grades haven't been released on minerva,
                            //  index will be null
                            averageGrade = ""
                            runCodeWithException {
                                if (rows[semesterIndex + 7].text()
                                        .matches("[ABCDF].|[ABCDF]".toRegex())
                                ) {
                                    // Regex looks for a letter grade
                                    averageGrade = rows[semesterIndex + 7].text()
                                } else if (rows[semesterIndex + 6].text()
                                        .matches("[ABCDF].|[ABCDF]".toRegex())
                                ) {
                                    // Failed course, average grade appears one row earlier
                                    averageGrade = rows[semesterIndex + 6].text()
                                }
                            }
                        } else {
                            // Extract transfer credit information
                            if (!rows[semesterIndex + 3].text()
                                    .matches("[A-Za-z]{4}.*".toRegex())
                            ) {
                                // Individual transferred courses not listed
                                code = rows[semesterIndex + 2].text()

                                // Extract the number of credits granted
                                runCodeWithException("Credits") {
                                    credits = getCredits(code)
                                }
                            } else {
                                // Individual transferred courses listed
                                try {
                                    // Try checking for the number of credits transferred per course
                                    code = rows[semesterIndex + 2].text()
                                    title = rows[semesterIndex + 3].text() + " " +
                                        rows[semesterIndex + 4].text()
                                    credits = rows[semesterIndex + 5].text().toDouble()
                                } catch (e: NumberFormatException) {
                                    // Number of credits per course not listed
                                    runCodeWithException("Credits") {
                                        code = rows[semesterIndex + 2].text()
                                        title = ""

                                        credits = getCredits(code)

                                        // Add the course codes for transferred courses
                                        var addedIndex = 3
                                        var first = true
                                        while (rows[semesterIndex + addedIndex].text()
                                                .matches("[A-Za-z]{4}.*".toRegex())
                                        ) {
                                            if (!first) {
                                                title += "\n"
                                            }
                                            first = false
                                            title = title +
                                                rows[semesterIndex + addedIndex].text() + " " +
                                                rows[semesterIndex + addedIndex + 1].text()
                                            addedIndex += 2
                                        }
                                    }
                                }
                            }
                            termCredits = credits
                        }

                        // There is at least one course
                        hasCourse = true
                        courses.add(
                            TranscriptCourse(
                                semesterId, Term(season, year), code,
                                title, credits, grade, averageGrade
                            )
                        )
                    }

                    // Breaks the loop if the next semester is reached
                    if (isSemesterStart(text)) {
                        break
                    }

                    // Increment the index
                    semesterIndex++

                    // Reached the end of the transcript, break loop
                    try {
                        rows[semesterIndex]
                    } catch (e: IndexOutOfBoundsException) {
                        break
                    }
                }

                // Check if there are any courses associated with the semester
                //  If not, don't add the semester to the list of semesters
                if (hasCourse) {
                    semesters.add(
                        Semester(
                            semesterId, Term(season, year), program,
                            bachelor, termCredits, termGPA, isFullTime
                        )
                    )
                    semesterId++
                }
            }
        }

        // Inverse the semesters to get them in reverse chronological order
        return TranscriptResponse(Transcript(cgpa, totalCredits), semesters.reversed(), courses)
    }

    /**
     * Runs a [block] of code and catches any exceptions thrown. If there is a [section]
     *  (defaults to null), the exception is logged
     */
    private fun runCodeWithException(section: String? = null, block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            section?.apply { Timber.e(e, "Transcript Parse Error: $section") }
        }
    }

    /**
     * Returns true if the [text] of the data row is the start of a semester, false otherwise
     */
    private fun isSemesterStart(text: String): Boolean =
        text.startsWith(Season.FALL.title) || text.startsWith(Season.WINTER.title) ||
            text.startsWith(Season.SUMMER.title) || text.startsWith("Readmitted Fall") ||
            text.startsWith("Readmitted Winter") || text.startsWith("Readmitted Summer") ||
            text.startsWith("Change")

    /**
     * Returns the number of credits extracted from the [credits] String
     * @throws Exception Thrown if parsing of credits failed
     */
    @Throws(Exception::class)
    private fun getCredits(credits: String): Double {
        val creditsString = credits.replace("\\s", "")
        val creditArray = creditsString.split("-")[1].split("credits")
        return creditArray[0].trim().toDouble()
    }

    /**
     * Response object with all of the parsed info
     */
    class TranscriptResponse(
        val transcript: Transcript, val semesters: List<Semester>,
        val courses: List<TranscriptCourse>
    )
}
