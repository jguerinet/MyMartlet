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

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
		return LocalTime.MIDNIGHT.withHourOfDay(hour).toString(DateTimeFormat.forPattern("h a"));
	}

	/**
	 * Returns a String representation of the time (ex: 8:00 AM)
	 *
	 * @param time    The time
	 * @return The String representation of the time
	 */
	public static String getTimeString(LocalTime time) {
		return time.toString(DateTimeFormat.forPattern("hh:mm a"));
	}

	/**
	 * Returns a String representation of a date depending on the language chosen (in short format)
	 *
	 * @param date The date
	 * @return A locale-dependent short String representation of the date
	 */
	public static String getShortDateString(LocalDate date) {
		//Depending on the language chosen
		DateTimeFormatter fmt;
        //TODO Take the local directly from the context, and let the formatter do the work
//		if (language == LanguageManager.ENGLISH) {
			fmt = DateTimeFormat.forPattern("MMM dd, yyyy");
//		} else {
//			fmt = DateTimeFormat.forPattern("dd MMM yyyy");
//		}

		return fmt.print(date);
	}

	/**
	 * Returns a String representation of a date depending on the language chosen
	 *
	 * @param date The date
	 * @return A locale-dependent String representation of the date
	 */
	public static String getDateString(LocalDate date) {
		//Depending on the language chosen
		DateTimeFormatter fmt;
        //TODO Take the local directly from the context, and let the formatter do the work
//		if (language == LanguageManager.ENGLISH) {
			fmt = DateTimeFormat.forPattern("MMMM dd, yyyy");
//		} else {
//			fmt = DateTimeFormat.forPattern("dd MMMM yyyy");
//		}

		return fmt.print(date);
	}
}
