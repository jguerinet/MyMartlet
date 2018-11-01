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

package com.guerinet.mymartlet.util.room.converters

import com.guerinet.room.converter.BaseListConverter
import org.threeten.bp.DayOfWeek

/**
 * Room Converter for a list of [DayOfWeek]s
 * @author Julien Guerinet
 * @since 2.0.0
 */
class DayOfWeekListConverter : BaseListConverter<DayOfWeek>() {

    override fun objectToString(value: DayOfWeek): String = value.value.toString()

    override fun objectFromString(value: String): DayOfWeek = DayOfWeek.of(value.toInt())
}