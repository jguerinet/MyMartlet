/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.util;

import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Contains useful static methods relating to dates
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class DateUtils {

	/**
	 * Returns a String of the hour (ex: 8 AM)
	 *
	 * @param hour    The hour
	 * @return The String representation of the hour
	 */
	public static String getHourString(int hour) {
        return LocalTime.MIDNIGHT.withHour(hour).format(DateTimeFormatter.ofPattern("h a"));
	}
	public static String getHourStringTwentyFourHrFmt(int hour) {
		return LocalTime.MIDNIGHT.withHour(hour).format(DateTimeFormatter.ofPattern("HH:mm"));
	}
}
