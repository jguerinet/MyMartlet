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

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ca.appvelopers.mcgillmobile.object.ClassItem;

/**
 * iCalendar exporter, may be used with ClassItems.
 * @author Selim Belhaouane
 */
public class CourseCalendar {
	private static final long serialVersionUID = 1L;
	
	//The following is from Outlook. 
	//Using a timezone is necessary for recurrence rules, as per 
	//	RFC5445 (page 122).
	private final String mTZName = "Eastern Standard Time";
	private final String mTZInfo = "BEGIN:VTIMEZONE\n"
			   + "TZID:" + mTZName + "\n"
			   + "BEGIN:STANDARD\n"
			   + "DTSTART:16011104T020000\n"
			   + "RRULE:FREQ=YEARLY;BYDAY=1SU;BYMONTH=11\n"
			   + "TZOFFSETFROM:-0400\n"
			   + "TZOFFSETTO:-0500\n"
			   + "END:STANDARD\n"
			   + "BEGIN:DAYLIGHT\n"
			   + "DTSTART:16010311T020000\n"
			   + "RRULE:FREQ=YEARLY;BYDAY=2SU;BYMONTH=3\n"
			   + "TZOFFSETFROM:-0500\n"
			   + "TZOFFSETTO:-0400\n"
			   + "END:DAYLIGHT\n"
			   + "END:VTIMEZONE";
	private final String mTZID = String.format("TZID=\"%s\"", mTZName);
	
	private String mPattern;
	private boolean mRecurring;
	private boolean mRounded;
	private List<ClassItem> mClasses;
	
    /**
     * Constructor for the CourseCalendar class
     * @param classes The list of classes to be exported
     * @param pattern Course contents for summary and description separated by 
	 * 				  "-" (see parsePattern()). 
	 * 		    e.g. CS-T will write Code and Section to Summary 
	 * 				 and Title to Description
	 * @param recurring Whether to set the event as recurring over the semester
	 * @param rounded Whether to use rounded times
     */
	public CourseCalendar(List<ClassItem> classes, String pattern, 
			              boolean recurring, boolean rounded) {
		mClasses = classes;
		mPattern = pattern;
		mRecurring = recurring;
		mRounded = rounded;
	}
	
	/**
	 * Default constructor for the CourseCalendar class. 
	 * Pattern   : SUMMARY contains course code
	 *             DESCRIPTION contains course title and section type
	 * Recurring : True
	 * Rounded   : False
	 * @param classes The list of classes to be exported
	 */
	public CourseCalendar(List<ClassItem> classes) {
		mClasses = classes;
		mPattern = "C-TY";
		mRecurring = true;
		mRounded = false;
	}
	
	public void writeCalendar(File file) {
		String prefix = "BEGIN:VCALENDAR" + "\n"
			          + "VERSION:2.0" + "\n"
				      + "PRODID:-//MyMartlet//CourseCalendar//" + 
			               serialVersionUID + "\n" 
				      + mTZInfo + "\n";
		String suffix = "END:VCALENDAR";
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(prefix);
			for (ClassItem item : mClasses) {
				bw.write(makeEvent(item));
			}
			bw.write(suffix);
			bw.flush();
			bw.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	/**
	 * Write entire EVENT to String from ClassItem
	 * @param item Event to be written
	 * @return EVENT as String
	 */
	public String makeEvent(ClassItem item) {
		// TODO: Make recurrence and rounding handling more elegant 
		//       (use lambda?)
		
		String event;
		
		//----------------
		//Parse summary-description request
		String[] splitted = mPattern.split("-");
		String summary = parsePattern(item, splitted[0]);
		String description;
		try {
			description = parsePattern(item,splitted[1]);
		}
		catch (ArrayIndexOutOfBoundsException e){
			description = "";
		}
		String location = item.getLocation();
		
		//-----------------
		//Get start and end date time
		//NOTES : 
		// o firstClassBegin and firstClassEnd represent the date-time 
		// 	 object for the beginning and end of the first lecture of 
		//	 the course. 
		// o lastDay is the last day of the semester (used for recurrence)
		DateTime firstClassBegin, firstClassEnd, lastDay, startDate;
		LocalTime startTime, endTime;
		if (mRounded) {
			startTime = item.getRoundedStartTime();
			endTime = item.getRoundedEndTime();
		} else {
			startTime = item.getStartTime();
			endTime = item.getEndTime();
		}
		startDate = item.getStartDate();
		firstClassBegin = startDate.withTime(startTime.getHourOfDay(),
				                             startTime.getMinuteOfHour(),
				                             0,
				                             0);
		firstClassEnd   = startDate.withTime(endTime.getHourOfDay(),
				                             endTime.getMinuteOfHour(),
				                             0,
				                             0);
		lastDay = item.getEndDate().withTime(23,0,0,0);
		if (mRecurring) {
			event = makeEvent(summary, description, location, firstClassBegin, 
							   firstClassEnd, lastDay);
		} else {
			event = makeEvent(summary, description, location, firstClassBegin, 
					           firstClassEnd);
		}
		return event;
	}
	
	/**
	 * General EVENT writer WITH weekly recurrence.
	 * All String parameters may be blank. 
	 * @param summary Summary of event (this is what appears on calendar)
	 * @param description Description of event -- Can be a blank string
	 * @param location Location of event
	 * @param start Start time of event
	 * @param end End time of event
	 * @param lastDay Date corresponding to end of recurrence
	 * @return EVENT as iCal-ready String
	 */
	public String makeEvent(String summary, String description, 
							 String location, DateTime start, DateTime end,
							 DateTime lastDay) {
		String prefix = "BEGIN:VEVENT\n";
		String suffix = "END:VEVENT\n";
		StringBuilder event = new StringBuilder();

		event.append(prefix);
		event.append(makeEventSummary(summary));
		event.append(makeEventDescription(description));
		event.append(makeEventLocation(location));
		event.append(makeEventStamp());
		event.append(makeEventStart(start));
		event.append(makeEventEnd(end));
		event.append(makeEventRecurrence(lastDay));
		event.append(suffix);

		return event.toString();
	}
	
	/**
	 * General EVENT writer WITHOUT recurrence.
	 * All String parameters may be blank. 
	 * @param summary Summary of event (this is what appears on calendar)
	 * @param description Description of event -- Can be a blank string
	 * @param location Location of event
	 * @param start Start time of event
	 * @param end End time of event
	 * @return EVENT as iCal-ready String
	 */
	public String makeEvent(String summary, String description, 
							 String location, DateTime start, DateTime end) {
		String prefix = "BEGIN:VEVENT\n";
		String suffix = "END:VEVENT\n";
		StringBuilder event = new StringBuilder();
		
		event.append(prefix);
		event.append(makeEventSummary(summary));
		event.append(makeEventDescription(description));
		event.append(makeEventLocation(location));
		event.append(makeEventStamp());
		event.append(makeEventStart(start));
		event.append(makeEventEnd(end));
		event.append(suffix);
		
		return event.toString();
	}
	
	/**
	 * Parse requested contents  
	 * Used for Summary and Description properties
	 *       
	 * Format: XX 
	 * X can be any of the symbols below.
	 *  
	 * Symbol  Meaning           Examples               Method Used
	 * ------  -------           --------               -----------
	 * C       Course Code       ECSE 200               getCourseCode
	 * T       Course Title      Electric Circuits 1    getCourseTitle
	 * S       Section           001                    getSection
	 * Y       Section Type      Lecture                getSectionType
	 * --------------------------------------------------------------
	 *  
	 * NOTES: 
	 *     o If multiple symbols are used for a single property, they are 
	 * 			separated by a " - ", e.g. "CS" will give : ECSE 200 - 001
	 * 	   o Parser only acts when it sees a known symbol
	 * 	   o Lower and upper case are accepted
	 * 
	 * @param pattern Pattern (see above)
	 * @param item Item from which to get code, title, section and/or type  
	 * @return Requested course attributes
 	 */
    public String parsePattern(ClassItem item, String pattern) {
    	StringBuilder attributes = new StringBuilder();
    	pattern = pattern.toLowerCase();
    	for (char symbol : pattern.toCharArray()) {
    		if (attributes.length() != 0) {
    			attributes.append(" - ");
    		}
			switch (symbol) {
			case 'c': attributes.append(item.getCode()); break;
			case 't': attributes.append(item.getTitle()); break;
			case 's': attributes.append(item.getSection()); break;
			case 'y': attributes.append(item.getType()); break;
			}
		}
    	
    	return attributes.toString();
    }
    
    /* iCal EVENT property makers */
    /* The list of properties written with this code is:
     *  o SUMMARY : Summary (Title) 
     *  o DESCRIPTION : Description -- Optional
     *  o LOCATION : Location
     *  o DTSTART : The Start Date-Time   
     *  o DTEND   : The End Date-Time  
     *  o DTSTAMP : The Stamp Date-Time (when the the event was written)
     *  o RRULE : Recurrence rule
     *  
     * There are two declarations for each property maker: 
     *  o One for a general DateTime or String
     *  o One for a ClassItem where the appropriate parameter is fetched from
     *    the ClassItem object and passed to the general method. These are not
     *    necessary for now, but might be helpful if the code needs to be 
     *    refactored. 
     */

	/**
	 * Write SUMMARY property of event
	 * @param name Name/Summary of event 
	 * @return SUMMARY property as String
	 */
	private String makeEventSummary(String name){
		return "SUMMARY:" + name + "\n";
	}
	
	/**
	 * Write SUMMARY property of ClassItem
	 * @param item The ClassItem
	 * @param pattern Contents of summary (see parsePattern())
	 * @return SUMMARY property as String
	 */
	@SuppressWarnings("unused")
	private String makeEventSummmary(ClassItem item, String pattern){
		String summary = parsePattern(item, pattern);
		return makeEventSummary(summary);
	}

	/**
	 * Write DESCRIPTION property of event
	 * @param description Description of event
	 * @return DESCRIPTION property as String
	 */
	private String makeEventDescription(String description){
		return "DESCRIPTION:" + description + "\n";
	}
	
	/**
	 * Write DESCRIPTION property of ClassItem
	 * @param item The ClassItem
	 * @param pattern Contents of description (see parsePattern())
	 * @return DESCRIPTION property as String
	 */
	@SuppressWarnings("unused")
	private String makeEventDescription(ClassItem item, String pattern){
		String description = parsePattern(item, pattern);
		return makeEventDescription(description);
	}

	/**
	 * Write LOCATION property of event
	 * @param location Location of event
	 * @return LOCATION property as String
	 */
	private String makeEventLocation(String location){
		return "LOCATION:" + location + "\n";
	}
	
	/**
	 * Write LOCATION property of ClassItem
	 * @param item The ClassItem
	 * @return LOCATION property as String
	 */
	@SuppressWarnings("unused")
	private String makeEventLocation(ClassItem item){
		String location = item.getLocation();
		return makeEventLocation(location);
	}
	
	/**
	 * Write DTSTART property of event
	 * @param date Start date-time
	 * @return DTSTART property as String
	 */
	private String makeEventStart(DateTime date){
		String name = "DTSTART";
		return formatTimeProperty(date, name) + "\n" ;
	}
	/**
	 * Write DTSTART property of ClassItem
	 * @param item The ClassItem
	 * @return DTSTART property as String
	 */
	@SuppressWarnings("unused")
	private String makeEventStart(ClassItem item){
		LocalTime startTime;
		DateTime startDate;
		if (mRounded) {
			startTime = item.getRoundedStartTime();
		} else {
			startTime = item.getStartTime();
		}
		startDate = item.getStartDate().withTime(startTime.getHourOfDay(),
				                             	 startTime.getMinuteOfHour(),
				                             	 0,
				                             	 0);
		return makeEventStart(startDate);
	}
	
	/**
	 * Write DTEND property of event
	 * @param date End date-time of event
	 * @return DTEND property as String
	 */
	private String makeEventEnd(DateTime date){
		String name = "DTEND";
		return formatTimeProperty(date,name) + "\n" ;
	}
	
	/**
	 * Write DTEND property of ClassItem
	 * @param item The ClassItem
	 * @return DTEND property as String
	 */
	@SuppressWarnings("unused")
	private String makeEventEnd(ClassItem item){
		LocalTime endTime;
		DateTime endDate;
		if (mRounded) {
			endTime = item.getRoundedEndTime();
		} else {
			endTime = item.getEndTime();
		}
		endDate = item.getEndDate().withTime(endTime.getHourOfDay(),
                							 endTime.getMinuteOfHour(),
                							 0,
                							 0);
		return makeEventEnd(endDate);
	}
	
	/**
	 * Write DTSTAMP property of event
	 * Stamp should be date created. DateTime constructor returns today's date.
	 * @return DTSTAMP property as String
	 */
	private String makeEventStamp(){
		String name = "DTSTAMP";
		DateTime date = new DateTime();
		return formatTimeProperty(date,name) + "\n";
	}
	
	/**
	 * Write DTSTAMP property given ClassItem
	 * @param item The ClassItem
	 * @return DTSTAMP property as String
	 */
	@SuppressWarnings("unused")
	private String makeEventStamp(ClassItem item){
		return makeEventStamp();
	}
	
	/**
	 * Write RRULE property of event given an UNTIL date
	 * The frequency of the recurrence is hard-coded to WEEKLY
	 * @param lastDay End of recurrence
	 * @return RRULE property as String
	 */
	private String makeEventRecurrence(DateTime lastDay){
		return "RRULE:FREQ=WEEKLY;UNTIL=" + formatTimeToICS(lastDay)+"Z"+"\n";
	}
	
	/**
	 * Write RRULE property of event given ClassItem using getEndDate()
	 * @param item The ClassItem
	 * @return RRULE property as String
	 */
	@SuppressWarnings("unused")
	private String makeEventRecurrence(ClassItem item){
		DateTime lastDay = item.getEndDate().withTime(23,0,0,0);
		return makeEventRecurrence(lastDay);
	}
	
	/* ICS FORMATTING MEHODS */
	
	/**
	 * Write general date-time property of event such as DTSTART
	 * @param date Date-time of property
	 * @param name Property name
	 * @return Property as String 
	 */
	private String formatTimeProperty(DateTime date, String name){
		String property;
		String time = formatTimeToICS(date);
		property = name + ";" + mTZID + ":" + time;
		return property;
	}
	
	/**
	 * Make ICS-compatible time out of date
	 * @param date Event to make date out of
	 * @return Formatted time
	 */
	private String formatTimeToICS(DateTime date){
		String time;
		time = date.toString("YYYYMMdd")+"T"+date.toString("HHmmss");
		return time;
	}
	
	//Joda DateTime's getDayOfWeek method returns an integer where 0 is Sunday.  
	static String[] iCalDays = {"SU","MO","TU","WE","TH","FR","SA"};
	
    /**
     * Returns the day of the week in iCalendar format, e.g. MO,TH 
     * @param date The given date
     * @return Day of the week
     */
    public static String toDayOfWeek(DateTime date){
        return iCalDays[date.getDayOfWeek()];
    }
    
    public static void main(String[] args) {
    	
	}
    
    /**
     * This is an example usage for this class.
     * @param classes The list of classes to be exported
     * @param pattern Course contents for summary and description separated by 
	 * 				  "-" (see parsePattern()). 
	 * 		    e.g. CS-T will write Code and Section to Summary 
	 * 				 and Title to Description
	 * @param recurring Whether to set the event as recurring over the semester
	 * @param rounded Whether to use rounded times
	 * @param file File to write iCal to
     */
    public static void example(List<ClassItem> classes, String pattern, 
    		                   boolean recurring, boolean rounded, File file) {
    	CourseCalendar courseCal = new CourseCalendar(classes, pattern, 
    			                                      recurring, rounded);
    	courseCal.writeCalendar(file);
    }
    
    /**
     * Creates an iCal file with the given classes and save it to the given file
     *
     * @param classes The list of classes to be exported
     * @param file    File to write iCal to
     */
    public static void createICalFile(List<ClassItem> classes, File file) {
    	CourseCalendar courseCal = new CourseCalendar(classes);
    	courseCal.writeCalendar(file);
    }
}
