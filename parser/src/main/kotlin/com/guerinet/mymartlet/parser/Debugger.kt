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

/**
 * Debugger for parsers to track messages.
 * This is useful in identify when a parser fails.
 */
interface ParseDebugger {
    /**
     * Debug message from parser.
     */
    fun debug(message: String)

    /**
     * Message sent before a premature return.
     */
    fun notFound(message: String)
}

/**
 * Default parser debugger that ignores all messages.
 */
internal object ParseDebuggerNoOp : ParseDebugger {
    override fun debug(message: String) = Unit
    override fun notFound(message: String) = Unit
}