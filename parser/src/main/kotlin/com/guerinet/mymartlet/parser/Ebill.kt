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
import com.guerinet.mymartlet.model.Statement
import com.guerinet.mymartlet.model.Term
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.util.Locale

private const val SCHEDULE_TABLE_QUERY = "table.datadisplaytable"

/**
 * Parses student ebill
 * Data found through paths
 * https://horizon.mcgill.ca/pban1/bztkcbil.pm_viewbills
 *
 * @author Allan Wang
 * @since 2.3.2
 */
internal fun Element.parseEbill(debugger: ParseDebugger = ParseDebuggerNoOp): List<Statement> {
    fun notFound(message: String): List<Statement> {
        debugger.notFound("parseEbill: $message")
        return emptyList()
    }

    val table = selectFirst(SCHEDULE_TABLE_QUERY) ?: return notFound(SCHEDULE_TABLE_QUERY)

    // Drop first element as it is the header; avoids unnecessary debugger logs
    return table.getElementsByTag("tr").asSequence().drop(1).mapNotNull { it.parseStatement(debugger) }.toList()
}

private fun Element.parseStatement(debugger: ParseDebugger = ParseDebuggerNoOp): Statement? {
    fun notFound(message: String): Statement? {
        debugger.notFound("parseStatement: $message")
        return null
    }
    if (tagName() != "tr") {
        return notFound("Invalid tag ${tagName()}")
    }
    val tds = getElementsByTag("td")
        .map { it.text() }
        .filter { it.isNotBlank() }

    if (tds.isEmpty()) {
        // Every other row is empty, so this is expected
        return null
    }

    val dates = tds.mapNotNull { it.parseDateAbbrev() }

    if (dates.size != 2) {
        return notFound("Invalid date count ${dates.size}")
    }

    val amount = tds.asSequence().mapNotNull { it.parseAmount() }.firstOrNull()
        ?: return notFound("No dollar amount found")

    return Statement(dates[0], dates[1], amount)
}

private fun String.parseAmount(): Double? {
    if (!contains('$')) {
        return null
    }
    val negative = endsWith('-')
    // Instead of filtering for numbers,
    // We will explicitly remove characters that we know should be in the string
    // to avoid over trimming
    val value = filter { it != ',' && it != '$' && it != '-' }
        .toDoubleOrNull() ?: return null
    return if (negative) -value else value
}

/**
 * Extracts a single course from an element list and a specified index.
 * Not that all elements are required as a course may span multiple subelements.
 *
 * Any classes that are found will be added to the [collector].
 *
 * Returns a new index to examine, which is guaranteed to always be greater than the received [index].
 *
 * @author Allan Wang
 * @since 2.3.2
 */
private fun Elements.parseCourse(
    index: Int,
    collector: MutableList<Course>,
    debugger: ParseDebugger = ParseDebuggerNoOp
): Int {
    /**
     * Log missing data and increment index
     */
    fun notFound(message: String): Int {
        debugger.notFound("parseCourse $index: $message")
        return index + 1
    }

    val row = getOrNull(index) ?: return notFound("Elements is empty")
    val nextRow = getOrNull(index + 1) ?: return notFound("Element only has one row")
    // Fast track; not expected data
    if (!row.`is`(SCHEDULE_TABLE_QUERY) || !nextRow.`is`(SCHEDULE_TABLE_QUERY)) {
        return notFound("Elements are not $SCHEDULE_TABLE_QUERY")
    }
    // Fast track; second row doesn't match
    if (nextRow.attr("summary") != "This table lists the scheduled meeting times and assigned instructors for this class..") {
        return notFound("Second row does not match scheduled meeting summary")
    }
    // Get content from first row
    val caption =
        row.getElementsByTag("caption").first()?.text() ?: return notFound("No caption found")
    val courseValues =
        REGEX_COURSE_NUMBER_SECTION.matchEntire(caption)?.groupValues
            ?: return notFound("No course value found")
    val (_, title, subject, number, section) = courseValues
    val tds = row.getElementsByTag("tr").mapNotNull { it.getElementsByTag("td").first() }
    if (tds.size < 5) {
        return notFound("Not enough bullet points")
    }
    val crn = tds[1].text().toIntOrNull() ?: return notFound("No valid crn found")
    val credits = tds[5].text().toDoubleOrNull() ?: return notFound("No valid credits found")

    fun warning(message: String) {
        debugger.debug("parseCourse:timeRow: $message")
    }

    val dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.US)

    nextRow.getElementsByTag("tr")
        .map { it.getElementsByTag("td") }
        .filter { it.size >= 5 }
        .map { it.map { e -> e.text().trim() } }
        .forEach { cells ->

            val (startTime, endTime) = cells[0]
                .split("-")
                .mapNotNull { it.parseTime() }
                .takeIf { it.size == 2 }
                ?: return@forEach warning("Time range mismatch; expected two entries")

            val days = cells[1].mapNotNull { DayUtils.charToDay(it) }

            val location = cells[2]

            val (startDate, endDate) = cells[3]
                .split("-")
                .mapNotNull {
                    try {
                        LocalDate.parse(it.trim(), dtf)
                    } catch (e: DateTimeParseException) {
                        debugger.debug("Date parse error: ${e.message}")
                        null
                    }
                }.takeIf { it.size == 2 }
                ?: return@forEach warning("Date range mismatch; expected two entries")

            val type = cells[4]

            val instructor = cells[5]

            val course = Course(
                term = Term.currentTerm(),
                subject = subject,
                number = number,
                title = title,
                crn = crn,
                section = section,
                startTime = startTime,
                endTime = endTime,
                days = days,
                type = type,
                location = location,
                instructor = instructor,
                credits = credits,
                startDate = startDate,
                endDate = endDate
            )

            collector.add(course)
        }

    return index + 2
}