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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

abstract class ParseTestBase {

    /**
     * Debugger to capture warning or error logs
     */
    protected lateinit var debugger: ParseDebuggerTest

    @BeforeTest
    fun baseBefore() {
        debugger = ParseDebuggerTest()
    }

    @AfterTest
    fun baseAfter() {
        debugger.check()
    }

}


class ParseDebuggerTest : ParseDebugger {

    private val debugMessages = mutableListOf<String>()
    private val notFoundMessages = mutableListOf<String>()

    override fun debug(message: String) {
        debugMessages.add(message)
    }

    override fun notFound(message: String) {
        notFoundMessages.add(message)
    }

    fun check() {
        assertTrue(
            debugMessages.isEmpty(),
            "Debug messages not empty:\n\t${debugMessages.joinToString("\n\t")}"
        )
        assertTrue(
            notFoundMessages.isEmpty(),
            "NotFound messages not empty\n\t${notFoundMessages.joinToString("\n\t")}"
        )
    }
}

fun getResource(resource: String): InputStream =
    ParseDebuggerTest::class.java.classLoader!!.getResource(resource).openStream()

fun getHtml(resource: String): Document =
    Jsoup.parseBodyFragment(getResource(resource).bufferedReader().use { it.readText() })