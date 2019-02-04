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

import com.guerinet.mymartlet.model.Statement
import com.squareup.moshi.Types
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.Type
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

/**
 * Retrofit converter to parse the user's ebill
 * @author Julien Guerinet
 * @since 1.0.0
 */
class EbillConverter : Converter.Factory(), Converter<ResponseBody, List<Statement>> {

    private val type = Types.newParameterizedType(List::class.java, Statement::class.java)

    private val dtf: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.US)

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (type?.toString() != this.type.toString()) {
            // This can only convert a list of statements
            null
        } else EbillConverter()
    }

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): List<Statement> {
        val statements = mutableListOf<Statement>()

        // Get the table
        //  If there is no table (no statements), return an empty list
        val table = Jsoup.parse(value.string()).getElementsByClass("datadisplaytable").first()
            ?: return statements

        // Go through the rows and extract the necessary information
        val rows = table.getElementsByTag("tr")
        var i = 2
        while (i < rows.size) {
            // Get the cells for the current row
            val cells = rows[i].getElementsByTag("td")

            // Parse the statement and due dates
            val date = LocalDate.parse(cells[0].text().trim(), dtf)
            val dueDate = LocalDate.parse(cells[3].text().trim(), dtf)

            // Get the amount String without the $ sign at the beginning
            val amountString = cells[5].text().trim().substring(1)

            var amount = -1.0
            try {
                if (amountString.endsWith("-")) {
                    // If the String ends with a dash (McGill owes the student),
                    //  remove it and parse the resulting amount
                    amount = NumberFormat.getNumberInstance(java.util.Locale.US)
                        .parse(amountString.substring(0, amountString.length - 1))
                        .toDouble()
                    // Negate the amount
                    amount *= -1.0
                } else {
                    // If not, just parse the amount
                    amount = NumberFormat.getNumberInstance(java.util.Locale.US)
                        .parse(amountString)
                        .toDouble()
                }
            } catch (e: ParseException) {
                Timber.e(e, "Ebill Parser Error: Amount")
            }

            // Add the new statement
            statements.add(Statement(date, dueDate, amount))
            i += 2
        }
        return statements
    }
}
