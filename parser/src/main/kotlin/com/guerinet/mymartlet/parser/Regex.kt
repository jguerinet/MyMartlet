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

import java.time.DayOfWeek

/**
 * Parses time
 * Example:
 * - 10:10 am
 */
internal val REGEX_TIME = Regex("(\\d+):(\\d+)\\s*(am|pm)", RegexOption.IGNORE_CASE)

/**
 * Parses course title, number, and section.
 * It is assumed that
 * - the title ends with a period
 * - the components are separated by '-'
 * - the section is a number
 * Example:
 * - Algorithm Design. - COMP 360 - 001
 */
internal val REGEX_COURSE_NUMBER_SECTION = Regex("(.+?)\\.\\s*-\\s*(.+?)\\s+(.+?)\\s*-\\s*(\\d+)")

