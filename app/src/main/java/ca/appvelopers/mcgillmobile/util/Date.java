/*
 * Copyright 2014-2015 Appvelopers
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

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Language;

/**
 * Contains useful static methods relating to dates
 * @author Julien Guerinet
 * @version 2.0
 * @since 2.0
 */
public class Date {

	/**
	 * Checks if the given hour is AM or PM
	 * @param hour The hour
	 * @return True if the hour is AM, false if it's PM
	 */
	private static boolean isAM(int hour){
		return hour / 12 == 0;
	}

	/**
	 * Returns a String of the hour (ex: 8 AM)
	 *
	 * @param context The app context
	 * @param hour    The hour
	 * @return The String representation of the hour
	 */
	public static String getHourString(Context context, int hour){
		//Get the right hours
		String hours = hour == 12 ? "12" : String.valueOf(hour % 12) ;

		return isAM(hour) ? context.getString(R.string.am, hours) :
				context.getString(R.string.pm, hours);
	}

	/**
	 * Returns a String representation of the time (ex: 8:00 AM)
	 *
	 * @param context The app context
	 * @param time    The time
	 * @return The String representation of the time
	 */
	public static String getTimeString(Context context, LocalTime time){
		//Get the right hours
		String hours = time.getHourOfDay() == 12 ? "12" : String.valueOf(time.getHourOfDay() % 12);

		//Get the right minutes (with 2 digits)
		String minutes = String.format("%02d", time.getMinuteOfHour());

		return (isAM(time.getHourOfDay())) ? context.getString(R.string.am_long, hours, minutes) :
				context.getString(R.string.pm_long, hours, minutes);
	}

	/**
	 * Returns a String represention of a date depending on the language chosen
	 *
	 * @param date The date
	 * @return A locale-dependent String representation of the date
	 */
	public static String getDateString(DateTime date){
		//Depending on the language chosen
		DateTimeFormatter fmt;
		if(App.getLanguage() == Language.ENGLISH){
			fmt = DateTimeFormat.forPattern("MMMM dd, yyyy");
		}
		else{
			fmt = DateTimeFormat.forPattern("dd MMMM yyyy");
		}

		return fmt.print(date);
	}
}
