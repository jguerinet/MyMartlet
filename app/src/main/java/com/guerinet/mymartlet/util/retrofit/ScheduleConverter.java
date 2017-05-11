/*
 * Copyright 2014-2017 Julien Guerinet
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

package com.guerinet.mymartlet.util.retrofit;

import android.support.v4.util.Pair;

import com.guerinet.mymartlet.model.Course;
import com.guerinet.mymartlet.util.DayUtils;
import com.squareup.moshi.Types;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Retrofit converter to parse the user's schedule as a list of courses (for one term)
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class ScheduleConverter extends Converter.Factory
        implements Converter<ResponseBody, List<Course>> {
    /**
     * {@link ParameterizedType} representing a list of {@link Course}s
     */
    private final ParameterizedType type = Types.newParameterizedType(List.class, Course.class);
    /**
     * {@link DateTimeFormatter} instance to parse dates
     */
    private final DateTimeFormatter dtf;

    /**
     * Default Constructor
     */
    public ScheduleConverter() {
        // Set up the DateTimeFormatter
        dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.US);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
            Retrofit retrofit) {
        if (!type.toString().equals(this.type.toString())) {
            //This can only convert a list of courses
            return null;
        }
        return new ScheduleConverter();
    }

    @Override
    public List<Course> convert(ResponseBody value) throws IOException {
        List<Course> courses = new ArrayList<>();

        //Parse the body into a Document
        Elements scheduleTable = Jsoup.parse(value.string()).getElementsByClass("datadisplaytable");

        //Go through the schedule table
        for (int i = 0; i < scheduleTable.size(); i += 2) {
            //Get the current row in the schedule table
            Element row = scheduleTable.get(i);

            //Course title, code, and section
            String[] texts = row.getElementsByTag("caption").first().text().split(" - ");
            String title = texts[0].substring(0, texts[0].length() - 1);
            String code = texts[1];
            String section = texts[2];

            //Parse the subject from the code
            String subject = "";
            try {
                subject = code.substring(0, 4);
            } catch (StringIndexOutOfBoundsException e) {
                Timber.e(e, "Schedule Parser Error: Subject");
            }

            String number = "";
            try {
                number = code.substring(5, 8);
            } catch (StringIndexOutOfBoundsException e) {
                Timber.e(e, "Schedule Parser Error: Number");
            }

            //CRN
            String crnString = row.getElementsByTag("tr").get(1).getElementsByTag("td").first()
                    .text();
            int crn = -1;
            try {
                crn = Integer.parseInt(crnString);
            } catch (NumberFormatException e) {
                Timber.e(e, "Schedule Parser Error: CRN");
            }

            //Credits
            String creditString = row.getElementsByTag("tr").get(5).getElementsByTag("td").first()
                    .text();
            double credits = -1;
            try {
                credits = Double.parseDouble(creditString);
            } catch (NumberFormatException e) {
                Timber.e(e, "Schedule Parser Error: Credits");
            }

            //Time, Days, Location, Type, Instructor
            if (i + 1 < scheduleTable.size() && scheduleTable.get(i + 1).attr("summary")
                    .equals("This table lists the scheduled meeting times and assigned " +
                            "instructors for this class..")) {

                //Get the rows with the schedule times
                Elements timeRows = scheduleTable.get(i + 1).getElementsByTag("tr");
                for (int j = 1; j < timeRows.size(); j++) {
                    //Get all of the cells of the current rows
                    Elements cells = timeRows.get(j).getElementsByTag("td");

                    String[] times = {};
                    List<DayOfWeek> days = new ArrayList<>();
                    String location = "";
                    String dateRange = "";
                    String type = "";
                    String instructor = "";

                    try {
                        times = cells.get(0).text().split(" - ");

                        //Day Parsing
                        String dayString = cells.get(1).text().replace('\u00A0', ' ').trim();
                        for (int k = 0; k < dayString.length(); k++) {
                            days.add(DayUtils.getDay(dayString.charAt(k)));
                        }

                        location = cells.get(2).text();
                        dateRange = cells.get(3).text();
                        type = cells.get(4).text();
                        instructor = cells.get(5).text();
                    } catch (IndexOutOfBoundsException e) {
                        Timber.e(e, "Schedule Parser Error: Course Info");
                    }

                    //Time parsing
                    LocalTime startTime, endTime;
                    try {
                        int startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
                        int startMinute = Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
                        int endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
                        int endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);

                        //If it's PM, then add 12 hours to the hours for 24 hours format
                        //Make sure it isn't noon
                        String startPM = times[0].split(" ")[1];
                        if (startPM.equals("PM") && startHour != 12) {
                            startHour += 12;
                        }

                        String endPM = times[1].split(" ")[1];
                        if (endPM.equals("PM") && endHour != 12) {
                            endHour += 12;
                        }

                        startTime = LocalTime.of(startHour, startMinute);
                        endTime = LocalTime.of(endHour, endMinute);
                    } catch (NumberFormatException e) {
                        //Some classes don't have assigned times
                        startTime = getDefaultStartTime();
                        endTime = getDefaultEndTime();
                    }

                    //Date Range parsing
                    LocalDate startDate, endDate;
                    try {
                        Pair<LocalDate, LocalDate> dates = parseDateRange(dateRange);
                        startDate = dates.first;
                        endDate = dates.second;
                    } catch (IllegalArgumentException e) {
                        Timber.e(e, "Schedule Parser Error: Date Range");
                        //Use today as the date if there's an error
                        startDate = LocalDate.now();
                        endDate = LocalDate.now();
                    }

                    //Add the course
                    courses.add(new Course(subject, number, title, crn, section, startTime, endTime,
                            days, type, location, instructor, credits, startDate, endDate));
                }
            } else {
                //If there is no data to parse, reset i and continue
                i --;
            }
        }

        return courses;
    }

    /**
     * @return A start time that will yield 0 for the rounded start time
     */
    public static LocalTime getDefaultStartTime() {
        return LocalTime.of(0, 5);
    }

    /**
     * @return An end time that will yield 0 for the rounded end time
     */
    public static LocalTime getDefaultEndTime() {
        return LocalTime.of(0, 55);
    }

    /**
     * Parses the date range String into 2 dates
     *
     * @param dateRange The date range String
     * @return A pair representing the starting and ending dates of the range
     */
    public Pair<LocalDate, LocalDate> parseDateRange(String dateRange)
            throws IllegalArgumentException {
        //Split the range into the 2 date Strings
        String[] dates = dateRange.split("-");
        String startDate = dates[0].trim();
        String endDate = dates[1].trim();

        //Parse the dates, return them as a pair
        return new Pair<>(LocalDate.parse(startDate, dtf), LocalDate.parse(endDate, dtf));
    }
}
