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
import org.jsoup.nodes.Element

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

    val table = selectFirst(DISPLAY_TABLE_QUERY) ?: return notFound(DISPLAY_TABLE_QUERY)

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
