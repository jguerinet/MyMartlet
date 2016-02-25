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

package ca.appvelopers.mcgillmobile.model.retrofit;

import android.support.v4.util.Pair;

import com.squareup.moshi.Types;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.util.DayUtils;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Retrofit converter to parse a list of course results when searching for courses
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class CourseResultConverter extends Converter.Factory
        implements Converter<ResponseBody, List<CourseResult>> {
    /**
     * {@link ParameterizedType} representing a list of {@link CourseResult}s
     */
    private final ParameterizedType type =
            Types.newParameterizedType(List.class, CourseResult.class);

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
            Annotation[] annotations, Retrofit retrofit) {
        if (!type.equals(this.type)) {
            //This can only convert a list of course results
            return null;
        }
        return new CourseResultConverter();
    }

    @Override
    public List<CourseResult> convert(ResponseBody value) throws IOException {
        List<CourseResult> courses = new ArrayList<>();
        //Parse the response body into a list of rows
        Elements rows = Jsoup.parse(value.string(), "UTF-8").getElementsByClass("dddefault");

        int rowNumber = 0;
        boolean loop = true;
        while (loop) {
            // Create a new course object with the default values
            double credits = 99;
            String subject = "ERROR";
            String number = "ERROR";
            String title = "";
            String type = "";
            List<DayOfWeek> days = new ArrayList<>();
            int crn = 0;
            String instructor = "";
            String location = "";
            //So that the rounded start time will be 0
            LocalTime startTime = ScheduleConverter.getDefaultStartTime();
            LocalTime endTime = ScheduleConverter.getDefaultEndTime();
            int capacity = 0;
            int seatsRemaining = 0;
            int waitlistRemaining = 0;
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();

            int i = 0;
            while (true) {
                try {
                    // Get the HTML row
                    String rowHTML = rows.get(rowNumber).toString();
                    rowNumber++;

                    // End condition: Empty row encountered or "Notes
                    if (rowHTML.contains("&nbsp;") || rowHTML.contains("NOTES:")) {
                        break;
                    }

                    //Get the row text
                    String row = rows.get(rowNumber).text();
                    switch (i) {
                        // CRN
                        case 1:
                            crn = Integer.parseInt(row);
                            break;
                        //Subject
                        case 2:
                            subject = row;
                            break;
                        //Number
                        case 3:
                            number = row;
                            break;
                        //Type
                        case 5:
                            type = row;
                            break;
                        // Number of credits
                        case 6:
                            credits = Double.parseDouble(row);
                            break;
                        // Course title
                        case 7:
                            //Remove the extra period at the end of the course title
                            title = row.substring(0, row.length() - 1);
                            break;
                        // Days of the week
                        case 8:
                            String dayString = row;
                            //TBA Stuff
                            if (dayString.equals("TBA")) {
                                i = 10;
                                rowNumber ++;
                            } else {
                                //Day Parsing
                                dayString = dayString.replace('\u00A0',' ').trim();
                                for (int k = 0; k < dayString.length(); k ++) {
                                    days.add(DayUtils.getDay(dayString.charAt(k)));
                                }
                            }
                            break;
                        // Time
                        case 9:
                            String[] times = row.split("-");
                            try {
                                int startHour =
                                        Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
                                int startMinute =
                                        Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
                                int endHour =
                                        Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
                                int endMinute =
                                        Integer.parseInt(times[1].split(" ")[0].split(":")[1]);

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
                                //Courses sometimes don't have assigned times
                                startTime = ScheduleConverter.getDefaultStartTime();
                                endTime = ScheduleConverter.getDefaultEndTime();
                            }
                            break;
                        // Capacity
                        case 10:
                            capacity = Integer.parseInt(row);
                            break;
                        // Seats remaining
                        case 12:
                            seatsRemaining = Integer.parseInt(row);
                            break;
                        // Waitlist remaining
                        case 15:
                            waitlistRemaining = Integer.parseInt(row);
                            break;
                        // Instructor
                        case 16:
                            instructor = row;
                            break;
                        // Start/end date
                        case 17:
                            Pair<LocalDate, LocalDate> dates =
                                    ScheduleConverter.parseDateRange(row);
                            startDate = dates.first;
                            endDate = dates.second;
                            break;
                        // Location
                        case 18:
                            location = row;
                            break;
                    }
                    i ++;
                } catch (IndexOutOfBoundsException e) {
                    loop = false;
                    break;
                } catch (Exception e) {
                    Timber.e(e, "Course Results Parser Error");
                }
            }
            //Don't add any courses with errors
            if (!subject.equals("ERROR") && !number.equals("ERROR")) {
                //Create a new course object and add it to list
                //TODO Should we be parsing the course section?
                courses.add(new CourseResult(subject, number, title, crn, "", startTime,
                        endTime, days, type, location, instructor, credits, startDate, endDate,
                        capacity, seatsRemaining, waitlistRemaining));
            }
        }

        return courses;
    }
}
