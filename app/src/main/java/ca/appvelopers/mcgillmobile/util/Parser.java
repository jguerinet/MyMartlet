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

import android.support.v4.util.Pair;
import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Day;
import ca.appvelopers.mcgillmobile.model.Season;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Token;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.TranscriptCourse;
import ca.appvelopers.mcgillmobile.model.User;

/**
 * Parses the given HTML Strings to get the necessary objects
 * @author Ryan Singzon
 * @author Quang Dao
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class Parser {
    private static final String TAG = "Parser";

    /**
     * Parses the HTML String to form a transcript
     *
     * @param html The String to parse
     */
    public static String parseTranscript(String html){
        //TODO Refactor this
        String transcriptError = null;

        //Parse the String into a document
        Document transcriptDocument = Jsoup.parse(html);

        //Extract program, scholarships, total credits, and CGPA
        Elements rows = transcriptDocument.getElementsByClass("fieldmediumtext");

        /*
         * Main loop:
         * This will iterate through every row of the transcript data and check for various tokens
         * Once a match is found, the value in the appropriate row will be saved to a variable
         */
        // row iterates through all of the rows in the transcript searching for tokens
        // dataRow finds the rows containing the data once a token is found
        Element dataRow;

        List<Semester> semesters = new ArrayList<Semester>();
        double cgpa = 0;
        double totalCredits = 0;

        int index = 0;
        for (Element row : rows){
            //Check the text at the start of the row
            //If it matches one of the tokens, take the corresponding data out
            //of one of the following rows, depending on the HTML layout

            //CGPA
            if(row.text().startsWith(Token.CUM_GPA.getString())){
                dataRow = rows.get(index+1);
                try{
                    cgpa = Double.parseDouble(dataRow.text());
                }
                catch (NumberFormatException e){
                    cgpa = -1;
                    Analytics.getInstance().sendEvent("Parsing Bug", "Transcript", "CGPA");
                    transcriptError = "CGPA";
                }
            }
            //Credits
            if(row.text().startsWith(Token.TOTAL_CREDITS.getString())){
                dataRow = rows.get(index+1);
                try{
                    totalCredits = Double.parseDouble(dataRow.text());
                }
                catch (NumberFormatException e){
                    totalCredits = -1;
                    Analytics.getInstance().sendEvent("Parsing Bug", "Transcript", "Total Credits");
                    transcriptError = "Total Credits";
                }
            }
            //Semester Information
            if(row.text().startsWith(Token.FALL.getString()) ||
                    row.text().startsWith(Token.WINTER.getString()) ||
                    row.text().startsWith(Token.SUMMER.getString()) ||
                    row.text().startsWith(Token.READMITTED_FALL.getString()) ||
                    row.text().startsWith(Token.READMITTED_WINTER.getString()) ||
                    row.text().startsWith(Token.READMITTED_SUMMER.getString()) ||
                    row.text().startsWith(Token.CHANGE_PROGRAM.getString())){

                //Initialize variables
                String scheduleSemester = row.text().trim();
                String[] scheduleSemesterItems = scheduleSemester.split("\\s+");

                //Find the right season and year, making sure to get the right array index
                Season season;
                int year;

                if(row.text().startsWith(Token.FALL.getString()) ||
                        row.text().startsWith(Token.WINTER.getString()) ||
                        row.text().startsWith(Token.SUMMER.getString()) ){

                    season = Season.findSeason(scheduleSemesterItems[0]);
                    try{
                        year = Integer.valueOf(scheduleSemesterItems[1]);
                    }
                    catch(NumberFormatException e){
                        Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                "Semester Year");
                        transcriptError = season.toString();
                        year = 2000;
                    }
                }
                else if(row.text().startsWith(Token.CHANGE_PROGRAM.getString())){
                    season = Season.findSeason(scheduleSemesterItems[3]);
                    try{
                        year = Integer.valueOf(scheduleSemesterItems[4]);
                    }
                    catch(NumberFormatException e){
                        Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                "Semester Year");
                        transcriptError = season.toString();
                        year = 2000;
                    }
                }
                else{
                    season = Season.findSeason(scheduleSemesterItems[1]);
                    try{
                        year = Integer.valueOf(scheduleSemesterItems[2]);
                    }
                    catch(NumberFormatException e){
                        Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                "Semester Year");
                        transcriptError = season.toString();
                        year = 2000;
                    }
                }

                //Log.e("TRANSCRIPT PARSER", season + " " + year);
                String program = "";
                String bachelor = "";
                int programYear = 99;
                double termCredits = 0;
                double termGPA = 0.0;
                boolean fullTime = false;
                boolean satisfactory = false;
                List<TranscriptCourse> courses = new ArrayList<TranscriptCourse>();

                //Search rows until the end of the semester is reached
                //Conditions for end of semester:
                //1. End of transcript is reached
                //2. The words "Fall" "Summer" or "Winter" appear
                int semesterIndex = index +1;
                dataRow = rows.get(semesterIndex);

                while(true){

                    //Student has graduated
                    if(dataRow.text().contains(Token.GRANTED.getString())){
                        break;
                    }

                    //Semester Info
                    else if(dataRow.text().startsWith(Token.DIPLOMA.getString()) ||
                            dataRow.text().startsWith(Token.BACHELOR.getString()) ||
                            dataRow.text().startsWith(Token.MASTER.getString()) ||
                            dataRow.text().startsWith(Token.DOCTOR.getString())){

                        //Example string:
                        //"Bachelor&nbps;of&nbsp;Engineering"<br>
                        //"Full-time&nbsp;Year&nbsp;0"<br>
                        //"Electrical&nbsp;Engineering"

                        String[] degreeDetails = dataRow.text().split(" ");
                        bachelor = degreeDetails[0];

                        //Check if student is full time
                        if(degreeDetails[1].startsWith("Full-time")){
                            fullTime = true;
                        }

                        else if(degreeDetails[1].contains("0")){
                            programYear = 0;
                        }
                        else if(degreeDetails[1].contains("1")){
                            programYear = 1;
                        }
                        else if(degreeDetails[1].contains("2")){
                            programYear = 2;
                        }
                        else if(degreeDetails[1].contains("3")){
                            programYear = 3;
                        }
                        else if(degreeDetails[1].contains("4")){
                            programYear = 4;
                        }
                        else if(degreeDetails[1].contains("5")){
                            programYear = 5;
                        }
                        else if(degreeDetails[1].contains("6")){
                            programYear = 6;
                        }

                        int detailsIndex = 0;
                        for(String detail : degreeDetails){
                            //Skip first two lines --> these are for bachelor and full time/part time
                            if(detailsIndex >= 2){
                                program += detail + " ";
                            }
                            detailsIndex++;
                        }
                    }
                    //Term GPA
                    else if(dataRow.text().startsWith(Token.TERM_GPA.getString())){
                        try{
                            termGPA = Double.parseDouble(rows.get(semesterIndex + 1).text());
                        }
                        catch (NumberFormatException e){
                            Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                    "Term GPA");
                            transcriptError = season.toString() + year;
                        }
                    }
                    //Term Credits
                    else if(dataRow.text().startsWith(Token.TERM_CREDITS.getString())){
                        try{
                            termCredits = Double.parseDouble(rows.get(semesterIndex + 2).text());
                        }
                        catch (NumberFormatException e){
                            Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                    "Term Credits");
                            transcriptError = season.toString() + year;
                        }
                    }

                    //Extract course information if row contains a course code
                    //Regex looks for a string in the form "ABCD ###"
                    else if(dataRow.text().matches("[A-Za-z]{4} [0-9]{3}.*") ||
                            dataRow.text().matches("[A-Za-z]{3}[0-9] [0-9]{3}")){
                        String courseCode = "";
                        //One semester courses are in the form ABCD ###
                        if(dataRow.text().matches("[A-Za-z]{4} [0-9]{3}")){
                            courseCode = dataRow.text();
                        }
                        //Some courses have the form ABC#
                        else if(dataRow.text().matches("[A-Za-z]{3}[0-9] [0-9]{3}")){
                            courseCode = dataRow.text();
                        }
                        //Multi semester courses are in the form ABCD ###D#
                        else{
                            //Extract first seven characters from string
                            try{
                                courseCode = dataRow.text().substring(0, 10);
                            }
                            catch(Exception e){
                                Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                        "Course Code");
                                transcriptError = season.toString() + year;
                                e.printStackTrace();
                            }
                        }

                        String courseTitle = rows.get(semesterIndex + 2).text();

                        //Failed courses are missing the earned credits row
                        double credits = 0;

                        //Check row to see if earned credit exists
                        try{
                            credits = Double.parseDouble(rows.get(semesterIndex + 6).text());
                        }
                        catch(NumberFormatException e){
                            //Course failed -> Earned credit = 0
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            //Log.e("TRANSCRIPT_URL PARSER", "Semester: " + season + " " + year + " NumberFormatException" + sw.toString());
                        }
                        catch(IndexOutOfBoundsException e){
                            //Log.e("TRANSCRIPT_URL PARSER", "IndexOutOfBoundsException" + e.toString());
                        }

                        //Obtain user's grade
                        String userGrade = rows.get(semesterIndex+4).text();

                        //Check for deferred classes
                        if(userGrade.equals("L")){
                            userGrade = rows.get(semesterIndex + 13).text();
                        }

                        //If average grades haven't been released on minerva, index will be null
                        String averageGrade = "";
                        try{
                            //Regex looks for a letter grade
                            if(rows.get(semesterIndex+7).text().matches("[ABCDF].|[ABCDF]")){
                                averageGrade = rows.get(semesterIndex+7).text();
                            }
                            //Failed course, average grade appears one row earlier
                            else if(rows.get(semesterIndex+6).text().matches("[ABCDF].|[ABCDF]")){
                                averageGrade = rows.get(semesterIndex+6).text();
                            }
                        }
                        catch(IndexOutOfBoundsException e){
                            //String not found
                            //Log.e("TRANSCRIPT_URL PARSER", "IndexOutOfBounds" + e.getMessage());
                        }
                        courses.add(new TranscriptCourse(new Term(season, year), courseTitle, courseCode, credits,
                                userGrade, averageGrade));
                    }

                    //Extract transfer credit information
                    else if(dataRow.text().startsWith(Token.CREDIT_EXEMPTION.getString())){
                        String courseTitle;
                        String courseCode;
                        String userGrade = "N/A";
                        String averageGrade = "";
                        double credits = 0;

                        //Individual transferred courses not listed
                        if(!rows.get(semesterIndex + 3).text().matches("[A-Za-z]{4}.*")){
                            courseCode = rows.get(semesterIndex + 2).text();

                            //Extract the number of credits granted
                            try{
                                credits = extractCredits(courseCode);
                            }
                            catch(Exception e){
                                Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                        "Credits");
                                transcriptError = season.toString() + year;
                                credits = 99;
                            }

                            TranscriptCourse course = new TranscriptCourse(new Term(season, year), "", courseCode, credits, userGrade,
                                    averageGrade);
                            courses.add(course);
                        }

                        //Individual transferred courses listed
                        else{
                            //Try checking for the number of credits transferred per course
                            try{
                                courseCode = rows.get(semesterIndex + 2).text();
                                courseTitle = rows.get(semesterIndex + 3).text() + " " + rows.get(semesterIndex+4).text();
                                credits = Double.parseDouble(rows.get(semesterIndex + 5).text());

                                TranscriptCourse course = new TranscriptCourse(new Term(season, year), courseTitle, courseCode, credits,
                                        userGrade, averageGrade);
                                courses.add(course);
                            }

                            //Number of credits per course not listed
                            catch(NumberFormatException e){
                                try{
                                    courseCode = rows.get(semesterIndex + 2).text();
                                    courseTitle = "";

                                    credits = extractCredits(courseCode);

                                    //Add the course codes for transferred courses
                                    int addedIndex = 3;
                                    boolean first = true;
                                    while(rows.get(semesterIndex + addedIndex).text().matches("[A-Za-z]{4}.*")){
                                        if(!first){
                                            courseTitle += "\n";
                                        }
                                        courseTitle = courseTitle + rows.get(semesterIndex + addedIndex).text() + " " + rows.get(semesterIndex+addedIndex+1).text();
                                        addedIndex = addedIndex + 2;
                                        first = false;
                                    }

                                    TranscriptCourse course = new TranscriptCourse(new Term(season, year), courseTitle, courseCode, credits,
                                            userGrade, averageGrade);
                                    courses.add(course);

                                }
                                catch(IndexOutOfBoundsException e2){
                                    //Log.e("TRANSCRIPT_URL PARSER", "IndexOutOfBounds" + e2.getMessage());
                                    e.printStackTrace();
                                }
                                catch(Exception e3){
                                    //Log.e("TRANSCRIPT_URL PARSER", "Generic error" + e3.getMessage());
                                    e.printStackTrace();
                                    Analytics.getInstance().sendEvent("Parsing Bug", "Transcript",
                                            "Credits");
                                    transcriptError = season.toString() + year;
                                    credits = 99;
                                }
                            }
                        }

                        termCredits = credits;
                    }

                    /**
                     * Breaks the loop if the next semester is reached
                     */
                    if(dataRow.text().startsWith(Token.FALL.getString()) ||
                            dataRow.text().startsWith(Token.WINTER.getString()) ||
                            dataRow.text().startsWith(Token.SUMMER.getString()) ||
                            dataRow.text().startsWith(Token.READMITTED_FALL.getString()) ||
                            dataRow.text().startsWith(Token.READMITTED_WINTER.getString()) ||
                            dataRow.text().startsWith(Token.READMITTED_SUMMER.getString()) ||
                            dataRow.text().startsWith(Token.CHANGE_PROGRAM.getString())){

                        break;
                    }

                    semesterIndex++;

                    //Reached the end of the transcript, break loop
                    try{
                        dataRow = rows.get(semesterIndex);
                    }
                    catch(IndexOutOfBoundsException e){
                        break;
                    }
                }

                //Check if there are any courses associated with the semester
                //If not, don't add the semester to the list of semesters
                if(!courses.isEmpty()){
                    Semester semester = new Semester(new Term(season, year), program, bachelor, programYear,
                            termCredits, termGPA, fullTime, satisfactory, courses);

                    semesters.add(semester);
                }

            }
            index++;
        }
        //Log.e("Log", "Setting transcript, CGPA: "+cgpa+" credits: "+totalCredits);
        App.setTranscript(new Transcript(cgpa, totalCredits, semesters));

        return transcriptError;
    }

    //Extracts the number of credits
    private static double extractCredits(String creditString) throws Exception{
        double numCredits;

        creditString = creditString.replaceAll("\\s", "");
        String[] creditArray = creditString.split("-");
        creditArray = creditArray[1].split("credits");
        numCredits = Double.parseDouble(creditArray[0]);
        return numCredits;
    }

    /**
     * Parses an HTML String to generate a list of classes (from a schedule)
     *
     * @param term The term for these classes
     * @param html The HTML String to parse
     * @return The Term String if there were any errors, null if none
     */
    public static String parseCourses(Term term, String html){
        String classError = null;

        //Get the list of classes
        List<Course> classItems = App.getClasses();
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
                } catch (StringIndexOutOfBoundsException e) {
                    Log.e(TAG, "Exception in Subject parsing in Class Parser", e);
                    Analytics.getInstance().sendEvent("Parsing Bug", "Class List",
                            "Course subject Substring");
                    classError = term.toString();
                }

                String number = "";
                try {
                    number = code.substring(5, 8);
                } catch (StringIndexOutOfBoundsException e) {
                    Log.e(TAG, "Exception in Number parsing in Class Parser", e);
                    Analytics.getInstance().sendEvent("Parsing Bug", "Class List",
                            "Course Number Substring");
                    classError = term.toString();
                }

                //CRN
                row = currentElement.getElementsByTag("tr").get(1);
                String crnString = row.getElementsByTag("td").first().text();
                int crn = -1;
                try{
                    crn = Integer.parseInt(crnString);
                }
                catch (NumberFormatException e){
                    Log.e(TAG, "CRN Exception in Class Parser", e);
                    Analytics.getInstance().sendEvent("Parsing Bug", "Class List", "crn");
                    classError = term.toString();
                }

                //Credits
                row = currentElement.getElementsByTag("tr").get(5);
                String creditString = row.getElementsByTag("td").first().text();
                double credits = -1;
                try{
                    credits = Double.parseDouble(creditString);
                }
                catch (NumberFormatException e){
                    Log.e(TAG, "Credits Exception in Class Parser", e);
                    Analytics.getInstance().sendEvent("Parsing Bug", "Class List", "credits");
                    classError = term.toString();
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
                        char[] dayCharacters = {};
                        String location = "";
                        String dateRange = "";
                        String type = "";
                        String instructor = "";

                        try{
                            times = cells.get(0).text().split(" - ");
                            dayCharacters = cells.get(1).text().toCharArray();
                            location = cells.get(2).text();
                            dateRange = cells.get(3).text();
                            type = cells.get(4).text();
                            instructor = cells.get(5).text();
                        }
                        catch(IndexOutOfBoundsException e){
                            Log.e(TAG, "Time/Days/Location/Type/Instructor Exception in " +
                                    "Class Parser", e);
                            Analytics.getInstance().sendEvent("Parsing Bug", "Class List",
                                    "IndexOutOfBounds on Info");
                            classError = term.toString();
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

                            startTime = new LocalTime(startHour, startMinute);
                            endTime = new LocalTime(endHour, endMinute);
                        }
                        //Try/Catch for classes with no assigned times
                        catch (NumberFormatException e) {
                            startTime = getDefaultStartTime();
                            endTime = getDefaultEndTime();
                        }

                        //Day Parsing
                        List<Day> days = new ArrayList<>();
                        for (char dayCharacter : dayCharacters) {
                            days.add(Day.getDay(dayCharacter));
                        }

                        //Date Range parsing
                        LocalDate startDate = LocalDate.now();
                        LocalDate endDate = LocalDate.now();
                        try{
                            Pair<LocalDate, LocalDate> dates = parseDateRange(dateRange);
                            startDate = dates.first;
                            endDate = dates.second;
                        } catch (IllegalArgumentException e){
                            Log.e(TAG, "Date Range Parsing Exception in Class Parser", e);
                            Analytics.getInstance().sendEvent("Parsing Bug", "Class List",
                                    "Start/End Date Parsing");
                            classError = term.toString();
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
        App.setClasses(classItems);

        return classError;
    }

    /**
     * Parses the HTML String to return a list of classes (from a class search result)
     *
     * @param term The term for these classes
     * @param html The HTML String to parse
     * @return The list of resulting classes
     */
    public static List<Course> parseClassResults(Term term, String html){
        List<Course> classItems = new ArrayList<>();

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
            List<Day> days = new ArrayList<>();
            int crn = 0;
            String instructor = "";
            String location = "";
            //So that the rounded start time will be 0
            LocalTime startTime = getDefaultStartTime();
            LocalTime endTime = getDefaultEndTime();
            int capacity = 0;
            int seatsAvailable = 0;
            int seatsRemaining = 0;
            int waitlistCapacity = 0;
            int waitlistAvailable = 0;
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
                            if(dayString.equals("TBA")){
                                days.add(Day.TBA);
                                i = 10;
                                rowNumber++;
                            }
                            else{
                                char[] dayCharacters = dayString.toCharArray();
                                for(char dayChar : dayCharacters){
                                    days.add(Day.getDay(dayChar));
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

                                startTime = new LocalTime(startHour, startMinute);
                                endTime = new LocalTime(endHour, endMinute);
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
                        // Seats available
                        case 11:
                            seatsAvailable = Integer.parseInt(row.text());
                            break;
                        // Seats remaining
                        case 12:
                            seatsRemaining = Integer.parseInt(row.text());
                            break;
                        // Waitlist capacity
                        case 13:
                            waitlistCapacity = Integer.parseInt(row.text());
                            break;
                        // Waitlist available
                        case 14:
                            waitlistAvailable = Integer.parseInt(row.text());
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
                    Log.e(TAG, "Error in Class Results Parser", e);
                }
            }
            rowsSoFar = 0;
            if( !subject.equals("ERROR") && !number.equals("ERROR")){
                //Create a new course object and add it to list
                classItems.add(new Course(term, subject, number, title, crn, "", startTime,
                        endTime, days, type, location, instructor, credits, startDate, endDate,
                        capacity, seatsAvailable, seatsRemaining, waitlistCapacity,
                        waitlistAvailable, waitlistRemaining));
            }
        }
        return classItems;
    }

    /**
     * Parses the Minerva Quick Add/Drop page after registering to check if any errors have occurred
     * @param resultHTML The HTML string
     * @return CRNs with registration errors and the associated error
     */
    public static Map<String, String> parseRegistrationErrors(String resultHTML){
        Map<String, String> registrationErrors = new HashMap<String, String>();

        Document document = Jsoup.parse(resultHTML, "UTF-8");
        Elements dataRows = document.getElementsByClass("plaintable");

        for(Element row : dataRows){

            //Check if an error exists
            if(row.toString().contains("errortext")){

                //If so, determine what error is present
                Elements links = document.select("a[href]");

                //Insert list of CRNs and errors into a map
                for(Element link : links){

                    if(link.toString().contains(Connection.REGISTRATION_ERROR_URL)){
                        String CRN = link.parent().parent().child(1).text();
                        String error = link.text();
                        registrationErrors.put(CRN, error);
                    }
                }
            }
        }
        return registrationErrors;
    }

    /**
     * Parses an HTML String into a list of statements
     *
     * @param html The HTML String
     */
    public static void parseEbill(String html){
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementsByClass("datadisplaytable").first();

        /* USER INFO */
        //Parse the user info
        Elements header = table.getElementsByTag("caption");
        String userInfo = header.get(0).text().replace("Statements for ", "");
        String[] userItems = userInfo.split(" - ");

        App.setUserInfo(new User(userItems[1].trim(), userItems[0].trim()));

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
            double amount;
            if(amountString.endsWith("-")){
                //Remove it and parse the resulting amount
                amount = Double.parseDouble(amountString.substring(0, amountString.length() - 1));
                //Negate the amount
                amount *= -1;
            }
            //If not, just parse the amount
            else{
                amount = Double.parseDouble(amountString);
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
    private static LocalTime getDefaultStartTime(){
        return new LocalTime(0, 5);
    }

    /**
     * @return An end time that will yield 0 for the rounded end time
     */
    private static LocalTime getDefaultEndTime(){
        return new LocalTime(0, 55);
    }

    /**
     * Parses a String into a LocalDate object
     *
     * @param date The date String
     * @return The corresponding local date
     */
    private static LocalDate parseDate(String date){
        //Set up the formatter we're going to use to parse these Strings
        DateTimeFormatter dtf =  DateTimeFormat.forPattern("MMM dd, yyyy").withLocale(Locale.US);

        return dtf.parseLocalDate(date);
    }

    /**
     * Parses the date range String into 2 dates
     *
     * @param dateRange The date range String
     * @return A pair representing the starting and ending dates of the range
     */
    private static Pair<LocalDate, LocalDate> parseDateRange(String dateRange)
            throws IllegalArgumentException{
        //Split the range into the 2 date Strings
        String startDate = dateRange.split(" - ")[0];
        String endDate = dateRange.split(" - ")[1];

        //Parse the dates, return them as a pair
        return new Pair<>(parseDate(startDate), parseDate(endDate));
    }
}
