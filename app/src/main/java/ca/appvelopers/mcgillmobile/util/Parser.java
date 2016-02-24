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

import android.support.v4.util.Pair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.User;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Parses the given HTML Strings to get the necessary objects
 * @author Ryan Singzon
 * @author Quang Dao
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Parser {

    /**
     * TODO
     * Parses an HTML String to generate a list of classes (from a schedule)
     *
     * @param term The term for these classes
     * @param response
     * @return The Term String if there were any errors, null if none
     * @throws IOException
     */
    public static String parseCourses(Term term, Response<ResponseBody> response) throws IOException {
        String classError = null;
        String html = response.body().string();

        //Get the list of classes
        List<Course> classItems = App.getCourses();
        //If there are none, just use an empty list
        if(classItems == null){
            classItems = new ArrayList<>();
        }

        //Remove all of the classes for this semester
        List<Course> classesToRemove = new ArrayList<>();
        for(Course classItem : classItems){
            if(classItem.getTerm().equals(term)){
                classesToRemove.add(classItem);
            }
        }
        classItems.removeAll(classesToRemove);

        //Parse the String into a Document
        Document doc = Jsoup.parse(html);
        Elements scheduleTable = doc.getElementsByClass("datadisplaytable");
        if(!scheduleTable.isEmpty()){
            //Go through the schedule table
            for (int i = 0; i < scheduleTable.size(); i += 2) {
                Element row;
                Element currentElement = scheduleTable.get(i);

                //Course title, code, and section
                row = currentElement.getElementsByTag("caption").first();
                String[] texts = row.text().split(" - ");
                String title = texts[0].substring(0, texts[0].length() - 1);
                String code = texts[1];
                String section = texts[2];

                //Parse the subject from the code
                String subject = "";
                try {
                    subject = code.substring(0, 4);
                } catch(StringIndexOutOfBoundsException e){
                    Timber.e(e, "Course Subject Parsing Bug");
                    classError = term.getId();
                }

                String number = "";
                try {
                    number = code.substring(5, 8);
                } catch(StringIndexOutOfBoundsException e){
                    Timber.e(e, "Course Number Parsing Bug");
                    classError = term.getId();
                }

                //CRN
                row = currentElement.getElementsByTag("tr").get(1);
                String crnString = row.getElementsByTag("td").first().text();
                int crn = -1;
                try{
                    crn = Integer.parseInt(crnString);
                }
                catch (NumberFormatException e){
                    Timber.e(e, "Course CRN Parsing Bug");
                    classError = term.getId();
                }

                //Credits
                row = currentElement.getElementsByTag("tr").get(5);
                String creditString = row.getElementsByTag("td").first().text();
                double credits = -1;
                try{
                    credits = Double.parseDouble(creditString);
                }
                catch (NumberFormatException e){
                    Timber.e(e, "Course Credits Parsing Bug");
                    classError = term.getId();
                }

                //Time, Days, Location, Type, Instructor
                if (i + 1 < scheduleTable.size() && scheduleTable.get(i + 1).attr("summary")
                        .equals("This table lists the scheduled meeting times and assigned " +
                                "instructors for this class..")) {

                    Elements scheduledTimesRows = scheduleTable.get(i+1).getElementsByTag("tr");
                    for (int j = 1; j < scheduledTimesRows.size(); j++) {
                        row = scheduledTimesRows.get(j);
                        Elements cells = row.getElementsByTag("td");

                        String[] times = {};
                        List<DayOfWeek> days = new ArrayList<>();
                        String location = "";
                        String dateRange = "";
                        String type = "";
                        String instructor = "";

                        try{
                            times = cells.get(0).text().split(" - ");
                            //Day Parsing
                            String dayString = cells.get(1).text().replace('\u00A0',' ').trim();
                            for (int k = 0; k < dayString.length(); k ++) {
                                days.add(DayUtils.getDay(dayString.charAt(k)));
                            }
                            location = cells.get(2).text();
                            dateRange = cells.get(3).text();
                            type = cells.get(4).text();
                            instructor = cells.get(5).text();
                        }
                        catch(IndexOutOfBoundsException e){
                            Timber.e(e, "Course Info Parsing Bug");
                            classError = term.getId();
                        }

                        //Time parsing
                        LocalTime startTime, endTime;
                        try{
                            int startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
                            int startMinute =
                                    Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
                            int endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
                            int endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
                            String startPM = times[0].split(" ")[1];
                            String endPM = times[1].split(" ")[1];

                            //If it's PM, then add 12 hours to the hours for 24 hours format
                            //Make sure it isn't noon
                            if (startPM.equals("PM") && startHour != 12) {
                                startHour += 12;
                            }
                            if (endPM.equals("PM") && endHour != 12) {
                                endHour += 12;
                            }

                            startTime = LocalTime.of(startHour, startMinute);
                            endTime = LocalTime.of(endHour, endMinute);
                        }
                        //Try/Catch for classes with no assigned times
                        catch (NumberFormatException e) {
                            startTime = getDefaultStartTime();
                            endTime = getDefaultEndTime();
                        }

                        //Date Range parsing
                        LocalDate startDate = LocalDate.now();
                        LocalDate endDate = LocalDate.now();
                        try{
                            Pair<LocalDate, LocalDate> dates = parseDateRange(dateRange);
                            startDate = dates.first;
                            endDate = dates.second;
                        } catch (IllegalArgumentException e){
                            Timber.e(e, "Course Date Range Parsing Bug");
                            classError = term.getId();
                        }

                        //Add the course
                        classItems.add(new Course(term, subject, number, title, crn, section,
                                startTime, endTime, days, type, location, instructor,  credits,
                                startDate, endDate));
                    }
                }
                //If there is no data to parse, reset i and continue
                else {
                    i = i - 1;
                }
            }
        }

        //Save it to the instance variable in Application class
        App.setCourses(classItems);

        return classError;
    }

    /**
     * TODO
     * Parses the HTML String to return a list of classes (from a class search result)
     *
     * @param term The term for these classes
     * @param response
     * @return The list of resulting classes
     * @throws IOException
     */
    public static List<Course> parseClassResults(Term term, Response<ResponseBody> response) throws IOException {
        List<Course> classItems = new ArrayList<>();
        String html = response.body().string();

        //Parse the String into a document
        Document document = Jsoup.parse(html, "UTF-8");
        //Find rows of HTML by class
        Elements dataRows = document.getElementsByClass("dddefault");

        int rowNumber = 0;
        int rowsSoFar = 0;
        boolean loop = true;
        while (loop) {
            // Create a new course object with the default valued
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
            LocalTime startTime = getDefaultStartTime();
            LocalTime endTime = getDefaultEndTime();
            int capacity = 0;
            int seatsRemaining = 0;
            int waitlistRemaining = 0;
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();

            int i = 0;
            while (true) {
                try {
                    // Get the HTML row
                    Element row = dataRows.get(rowNumber);
                    rowNumber++;

                    // End condition: Empty row encountered
                    if (row.toString().contains("&nbsp;") && rowsSoFar > 10) {
                        break;
                    }
                    else if(row.toString().contains("NOTES:")){
                        break;
                    }

                    switch (i) {
                        // CRN
                        case 1:
                            crn = Integer.parseInt(row.text());
                            break;
                        //Subject
                        case 2:
                            subject = row.text();
                            break;
                        //Number
                        case 3:
                            number = row.text();
                            break;
                        //Type
                        case 5:
                            type = row.text();
                            break;
                        // Number of credits
                        case 6:
                            credits = Double.parseDouble(row.text());
                            break;
                        // Course title
                        case 7:
                            //Remove the extra period at the end of the course title
                            title = row.text().substring(0, row.text().length() - 1);
                            break;
                        // Days of the week
                        case 8:
                            String dayString = row.text();
                            //TBA Stuff
                            if (dayString.equals("TBA")) {
                                i = 10;
                                rowNumber++;
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
                            String[] times = row.text().split("-");
                            try {
                                int startHour =
                                        Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
                                int startMinute =
                                        Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
                                int endHour =
                                        Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
                                int endMinute =
                                        Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
                                String startPM = times[0].split(" ")[1];
                                String endPM = times[1].split(" ")[1];

                                //If it's PM, then add 12 hours to the hours for 24 hours format
                                //Make sure it isn't noon
                                if (startPM.equals("PM") && startHour != 12) {
                                    startHour += 12;
                                }
                                if (endPM.equals("PM") && endHour != 12) {
                                    endHour += 12;
                                }

                                startTime = LocalTime.of(startHour, startMinute);
                                endTime = LocalTime.of(endHour, endMinute);
                            }
                            //Try/Catch for classes with no assigned times
                            catch (NumberFormatException e) {
                                startTime = getDefaultStartTime();
                                endTime = getDefaultEndTime();
                            }
                            break;
                        // Capacity
                        case 10:
                            capacity = Integer.parseInt(row.text());
                            break;
                        // Seats remaining
                        case 12:
                            seatsRemaining = Integer.parseInt(row.text());
                            break;
                        // Waitlist remaining
                        case 15:
                            waitlistRemaining = Integer.parseInt(row.text());
                            break;
                        // Instructor
                        case 16:
                            instructor = row.text();
                            break;
                        // Start/end date
                        case 17:
                            Pair<LocalDate, LocalDate> dates = parseDateRange(row.text());
                            startDate = dates.first;
                            endDate = dates.second;
                            break;
                        // Location
                        case 18:
                            location = row.text();
                            break;
                    }
                    i++;
                }
                catch (IndexOutOfBoundsException e){
                    loop = false;
                    break;
                }
                catch (Exception e){
                    Timber.e(e, "Error in Class Results Parser");
                }
            }
            rowsSoFar = 0;
            if( !subject.equals("ERROR") && !number.equals("ERROR")){
                //Create a new course object and add it to list
                classItems.add(new Course(term, subject, number, title, crn, "", startTime,
                        endTime, days, type, location, instructor, credits, startDate, endDate,
                        capacity, seatsRemaining, waitlistRemaining));
            }
        }
        return classItems;
    }

    /**
     * TODO
     * Parses the Minerva Quick Add/Drop page after registering to check if any errors have occurred
     *
     * @param courses The list of courses the user was trying to register or unregister for/from
     * @return The error String, null if no errors
     */
    public static String parseRegistrationErrors(Response<ResponseBody> response, List<Course> courses) throws IOException {
        String html = response.body().string();
        Map<String, String> errors = new HashMap<>();

        Document document = Jsoup.parse(html, "UTF-8");
        Elements dataRows = document.getElementsByClass("plaintable");

        for(Element row : dataRows){
            //Check if an error exists
            if(row.toString().contains("errortext")){

                //If so, determine what error is present
                Elements links = document.select("a[href]");

                //Insert list of CRNs and errors into a map
                for(Element link : links){
                    if(link.toString().contains("http://www.is.mcgill.ca/whelp/sis_help/rg_errors.htm")){
                        String CRN = link.parent().parent().child(1).text();
                        String error = link.text();
                        errors.put(CRN, error);
                    }
                }
            }
        }

        //There are errors: set up the error message
        String error = null;
        if(!errors.isEmpty()){
            error = "";
            for(String crn : errors.keySet()){
                Timber.e("(Un)registration error for %s: %s", crn, errors.get(crn));

                //Find the corresponding course
                List<Course> errorCourses = new ArrayList<>();
                for(Course course : courses){
                    if(course.getCRN() == Integer.valueOf(crn)){
                        //Remove the course from the list of courses
                        errorCourses.add(course);
                        //Add this class to the error message
                        error += course.getCode() +  " (" + course.getType() + ") - " +
                                errors.get(crn) + "\n";
                        break;
                    }
                }
                courses.removeAll(errorCourses);
            }
        }

        return error;
    }

    /**
     * TODO
     * Parses an HTML String into a list of statements
     *
     * @param repsonse
     * @throws IOException
     */
    public static void parseEbill(Response<ResponseBody> repsonse) throws IOException {
        String html = repsonse.body().string();
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementsByClass("datadisplaytable").first();

        //If there is nothing to parse, don't continue
        if(table == null){
            App.setUser(new User("", ""));
            return;
        }

        /* USER INFO */
        //Parse the user info
        Elements header = table.getElementsByTag("caption");
        String userInfo = header.get(0).text().replace("Statements for ", "");
        String[] userItems = userInfo.split(" - ");

        App.setUser(new User(userItems[1].trim(), userItems[0].trim()));

        /* EBILL */
        List<Statement> statements = new ArrayList<>();

        //Go through the rows and extract the necessary information
        Elements rows = table.getElementsByTag("tr");
        for (int i = 2; i < rows.size(); i+=2) {
            Element row = rows.get(i);
            Elements cells = row.getElementsByTag("td");
            LocalDate date = parseDate(cells.get(0).text().trim());
            LocalDate dueDate = parseDate(cells.get(3).text().trim());
            String amountString = cells.get(5).text().trim();

            //Remove the $ sign at the beginning
            amountString = amountString.substring(1);

            //Check if the String ends with a dash (McGill owes the student)
            double amount = -1;
            try{
                if(amountString.endsWith("-")){
                    //Remove it and parse the resulting amount
                    amount = NumberFormat.getNumberInstance(java.util.Locale.US)
                            .parse(amountString.substring(0, amountString.length() - 1))
                            .doubleValue();
                    //Negate the amount
                    amount *= -1;
                }
                //If not, just parse the amount
                else{
                    amount = NumberFormat.getNumberInstance(java.util.Locale.US)
                            .parse(amountString).doubleValue();
                }
            } catch(ParseException e){
                Timber.e(e, "Ebill amount parse exception");
            }

            //Add the new statement
            statements.add(new Statement(date, dueDate, amount));
        }

        App.setEbill(statements);
    }

    /* HELPERS */

    /**
     * @return A start time that will yield 0 for the rounded start time
     */
    private static LocalTime getDefaultStartTime() {
        return LocalTime.of(0, 5);
    }

    /**
     * @return An end time that will yield 0 for the rounded end time
     */
    private static LocalTime getDefaultEndTime() {
        return LocalTime.of(0, 55);
    }

    /**
     * Parses a String into a LocalDate object
     *
     * @param date The date String
     * @return The corresponding local date
     */
    private static LocalDate parseDate(String date) {
        //Set up the formatter we're going to use to parse these Strings
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy").withLocale(Locale.US);

        return LocalDate.parse(date, dtf);
    }

    /**
     * Parses the date range String into 2 dates
     *
     * @param dateRange The date range String
     * @return A pair representing the starting and ending dates of the range
     */
    private static Pair<LocalDate, LocalDate> parseDateRange(String dateRange)
            throws IllegalArgumentException {
        //Split the range into the 2 date Strings
        String startDate = dateRange.split(" - ")[0];
        String endDate = dateRange.split(" - ")[1];

        //Parse the dates, return them as a pair
        return new Pair<>(parseDate(startDate), parseDate(endDate));
    }
}
