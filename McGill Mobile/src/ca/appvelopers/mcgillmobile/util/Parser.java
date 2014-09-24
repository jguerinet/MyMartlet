package ca.appvelopers.mcgillmobile.util;

import org.joda.time.DateTime;
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
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Course;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.object.EbillItem;
import ca.appvelopers.mcgillmobile.object.Season;
import ca.appvelopers.mcgillmobile.object.Semester;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.object.Token;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.object.UserInfo;

/**
 * Author : Julien
 * Date :  2014-05-31 2:35 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class Parser {

    /**
     * Parses the HTML String to form a transcript
     * @param stringHTML The String to parse
     */
    public static void parseTranscript(String stringHTML){
        Document transcriptDocument = Jsoup.parse(stringHTML);
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
                    GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "CGPA", null);
//                    DialogHelper.showTranscriptBugDialog((Activity)context, "CGPA", e.toString());
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
                    GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Total Credits", null);
//                    DialogHelper.showTranscriptBugDialog((Activity)context, "Total Credits", e.toString());
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
                        GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Semester Year", null);
//                        DialogHelper.showTranscriptBugDialog((Activity)context, season.toString(), e.toString());
                        year = 2000;
                    }
                }
                else if(row.text().startsWith(Token.CHANGE_PROGRAM.getString())){
                    season = Season.findSeason(scheduleSemesterItems[3]);
                    try{
                        year = Integer.valueOf(scheduleSemesterItems[4]);
                    }
                    catch(NumberFormatException e){
                        GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Semester Year", null);
//                        DialogHelper.showTranscriptBugDialog((Activity)context, season.toString(), e.toString());
                        year = 2000;
                    }
                }
                else{
                    season = Season.findSeason(scheduleSemesterItems[1]);
                    try{
                        year = Integer.valueOf(scheduleSemesterItems[2]);
                    }
                    catch(NumberFormatException e){
                        GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Semester Year", null);
//                        DialogHelper.showTranscriptBugDialog((Activity)context, season.toString(), e.toString());
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
                List<Course> courses = new ArrayList<Course>();

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
                            GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Term GPA", null);
//                            DialogHelper.showTranscriptBugDialog((Activity)context, season.toString() + year, e.toString());
                        }
                    }
                    //Term Credits
                    else if(dataRow.text().startsWith(Token.TERM_CREDITS.getString())){
                        try{
                            termCredits = Double.parseDouble(rows.get(semesterIndex + 2).text());
                        }
                        catch (NumberFormatException e){
                            GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Term Credits", null);
//                            DialogHelper.showTranscriptBugDialog((Activity)context, season.toString() + year, e.toString());
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
                                GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Course Code", null);
//                                DialogHelper.showTranscriptBugDialog((Activity)context, season.toString() + year, e.toString());
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
                            //Log.e("TRANSCRIPT PARSER", "Semester: " + season + " " + year + " NumberFormatException" + sw.toString());
                        }
                        catch(IndexOutOfBoundsException e){
                            //Log.e("TRANSCRIPT PARSER", "IndexOutOfBoundsException" + e.toString());
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
                            //Log.e("TRANSCRIPT PARSER", "IndexOutOfBounds" + e.getMessage());
                        }
                        courses.add(new Course(new Term(season, year), courseTitle, courseCode, credits,
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
                            credits = extractCredits(courseCode);

                            Course course = new Course(new Term(season, year), "", courseCode, credits, userGrade,
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

                                Course course = new Course(new Term(season, year), courseTitle, courseCode, credits,
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

                                    Course course = new Course(new Term(season, year), courseTitle, courseCode, credits,
                                            userGrade, averageGrade);
                                    courses.add(course);

                                }
                                catch(IndexOutOfBoundsException e2){
                                    //Log.e("TRANSCRIPT PARSER", "IndexOutOfBounds" + e2.getMessage());
                                    e.printStackTrace();
                                }
                                catch(Exception e3){
                                    //Log.e("TRANSCRIPT PARSER", "Generic error" + e3.getMessage());
                                    e.printStackTrace();
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
    }

    //Extracts the number of credits
    private static double extractCredits(String creditString){
        double numCredits;

        try{
            creditString = creditString.replaceAll("\\s", "");
            String[] creditArray = creditString.split("-");
            creditArray = creditArray[1].split("credits");
            numCredits = Double.parseDouble(creditArray[0]);
            return numCredits;
        }
        catch (NumberFormatException e){
            GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Credits", null);
//            DialogHelper.showTranscriptBugDialog((Activity)context, "Credit Extractor", e.toString());
            return 99;
        }
        catch(Exception e){
            GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Transcript", "Credits", null);
//            DialogHelper.showTranscriptBugDialog((Activity)context, "Credit Extractor", e.toString());
            return 88;
        }
    }

    /**
     * Parses an HTML String to generate a list of classes
     * @param term The term for these classes
     * @param classHTML The HTML String to parse
     */
    public static void parseClassList(Term term, String classHTML){
        //Get the list of classes already parsed for that year
        List<ClassItem> classItems = App.getClasses();

        //This will be the list of classes to remove at the end (the user has unregistered)
        List<ClassItem> classesToRemove = new ArrayList<ClassItem>();
        //It starts out with all of the classes for this term
        for(ClassItem classItem : classItems){
            if(classItem.getTerm().equals(term)){
                classesToRemove.add(classItem);
            }
        }

        if(classItems == null){
            classItems = new ArrayList<ClassItem>();
        }

        Document doc = Jsoup.parse(classHTML);
        Elements scheduleTable = doc.getElementsByClass("datadisplaytable");
        if (!scheduleTable.isEmpty()) {
            for (int i = 0; i < scheduleTable.size(); i += 2) {
                Element row;
                Element currentElement = scheduleTable.get(i);

                //Course name, code, and section
                row = currentElement.getElementsByTag("caption").first();
                String[] texts = row.text().split(" - ");
                String courseTitle = texts[0].substring(0, texts[0].length() - 1);
                String courseCode = texts[1];
                String section = texts[2];

                //CRN
                row = currentElement.getElementsByTag("tr").get(1);
                String crnString = row.getElementsByTag("td").first().text();
                int crn = -1;
                try{
                    crn = Integer.parseInt(crnString);
                }
                catch (NumberFormatException e){
                    //GA
                    GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Class List", "crn", null);
//                    DialogHelper.showSemesterBugDialog((Activity)context, term.toString(), courseTitle, e.toString());
                    e.printStackTrace();
                }

                //Credits
                row = currentElement.getElementsByTag("tr").get(5);
                String creditString = row.getElementsByTag("td").first().text();
                double credits = -1;
                try{
                    credits = Double.parseDouble(creditString);
                }
                catch (NumberFormatException e){
                    GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Class List", "credits", null);
//                    DialogHelper.showSemesterBugDialog((Activity) context, term.toString(), courseTitle, e.toString());
                    e.printStackTrace();
                }

                //Check if there is any data to parse
                if (i + 1 < scheduleTable.size() && scheduleTable.get(i + 1).attr("summary").equals("This table lists the scheduled meeting times and assigned instructors for this class..")) {
                    Elements scheduledTimesRows = scheduleTable.get(i+1).getElementsByTag("tr");
                    for (int j = 1; j < scheduledTimesRows.size(); j++) {
                        //Time, Days, Location, Section Type, Instructor
                        row = scheduledTimesRows.get(j);
                        Elements cells = row.getElementsByTag("td");
                        String[] times = cells.get(0).text().split(" - ");
                        char[] dayCharacters = cells.get(1).text().toCharArray();
                        String location = cells.get(2).text();
                        String dateRange = cells.get(3).text();
                        String sectionType = cells.get(4).text();
                        String instructor = cells.get(5).text();

                        //Time parsing
                        int startHour, startMinute, endHour, endMinute;
                        try {
                            startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
                            startMinute = Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
                            endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
                            endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
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
                        }
                        //Try/Catch for classes with no assigned times
                        catch (NumberFormatException e) {
                            startHour = 0;
                            //So that the the time will be 0
                            startMinute = 5;
                            endHour = 0;
                            //So that the time will be 0
                            endMinute = 55;
                        }

                        //Day Parsing
                        List<Day> days = new ArrayList<Day>();
                        for (char dayCharacter : dayCharacters) {
                            days.add(Day.getDay(dayCharacter));
                        }

                        //Date Range parsing

                        String startDateString = dateRange.split(" - ")[0];
                        String endDateString = dateRange.split(" - ")[1];
                        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMM dd, yyyy");
                        DateTime startDate = dateFormatter.parseDateTime(startDateString);
                        DateTime endDate = dateFormatter.parseDateTime(endDateString);


                        //Check if the class already exists
                        //There is a classItem for every row in the schedule times table, if there is a row but no corresponding classItem
                        //will add a new one to the next index
                        boolean classExists = false;
                        int k = 0;
                        for (ClassItem classItem : classItems) {
                            int classItemIndex = classItems.indexOf(classItem);
                            int classItemCRN = classItem.getCRN();
                            Term classItemTerm = classItem.getTerm();
                            if (classItem.getCRN() == crn && classItem.getTerm().equals(term)) {
                                k++;
                                classExists = true;
                                if (k == j) {
                                    //If you find an equivalent, just update it
                                    classItem.update(courseCode, courseTitle, section, startHour, startMinute, endHour, endMinute,
                                            days, sectionType, location, instructor, credits, dateRange, startDate, endDate);

                                    //Remove it from the list of class items to remove
                                    classesToRemove.remove(classItem);
                                    break;
                                } else if (classItemIndex == classItems.size() - 1) {
                                    //boundary case, if the current classItem is last item in list but there's still another row to add
                                    //treat as if it is not yet in list (to add to end of list)
                                    classExists = false;
                                }
                            } else if (k > 0 && k == (j - 1)) {
                                String subject = "";
                                try{
                                    subject = courseCode.substring(0, 4);
                                }
                                catch(StringIndexOutOfBoundsException e){
                                    GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Class List", "Course subject Substring", null);
//                                    DialogHelper.showSemesterBugDialog((Activity) context, term.toString(), courseTitle, e.toString());
                                }

                                String code = "";
                                try{
                                    code = courseCode.substring(5, 8);
                                }
                                catch(StringIndexOutOfBoundsException e){
                                    GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Class List", "Course Code Substring", null);
//                                    DialogHelper.showSemesterBugDialog((Activity) context, term.toString(), courseTitle, e.toString());
                                }

                                //a row is not yet included in classItems list
                                classItems.add(classItemIndex, new ClassItem(term, courseCode, subject,
                                        code, courseTitle, crn, section, startHour,
                                        startMinute, endHour, endMinute, days, sectionType, location, instructor, -1,
                                        -1, -1, -1, -1, -1, credits, dateRange, startDate, endDate));
                                k++;
                                break;
                            }
                        }
                        //If not, add a new class item
                        if (!classExists) {
                            String subject = "";
                            try{
                                subject = courseCode.substring(0, 4);
                            }
                            catch(StringIndexOutOfBoundsException e){
                                GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Class List", "Course subject Substring", null);
//                                    DialogHelper.showSemesterBugDialog((Activity) context, term.toString(), courseTitle, e.toString());
                            }

                            String code = "";
                            try{
                                code = courseCode.substring(5, 8);
                            }
                            catch(StringIndexOutOfBoundsException e){
                                GoogleAnalytics.sendEvent(App.getContext(), "Parsing Bug", "Class List", "Course Code Substring", null);
//                                    DialogHelper.showSemesterBugDialog((Activity) context, term.toString(), courseTitle, e.toString());
                            }

                            //Find the concerned course
                            classItems.add(new ClassItem(term, courseCode, subject,
                                    code, courseTitle, crn, section, startHour,
                                    startMinute, endHour, endMinute, days, sectionType, location, instructor, -1,
                                    -1, -1, -1, -1, -1, credits, dateRange, startDate, endDate));
                        }
                    }
                }
                //If there is no data to parse, reset i and continue
                else {
                    i = i - 1;
                }
            }
        }

        //Remove the classes to remove
        classItems.removeAll(classesToRemove);

        //Save it to the instance variable in Application class
        App.setClasses(classItems);
    }

    /**
     * Parses the HTML retrieved from Minerva and returns a list of classes
     * @param term The term for these classes
     * @param classHTML The HTML String to parse
     * @return The list of resulting classes
     */
    public static List<ClassItem> parseClassResults(Term term, String classHTML){
        List<ClassItem> classItems = new ArrayList<ClassItem>();

        Document document = Jsoup.parse(classHTML, "UTF-8");
        //Find rows of HTML by class
        Elements dataRows = document.getElementsByClass("dddefault");

        int rowNumber = 0;
        int rowsSoFar = 0;
        boolean loop = true;

        while (loop) {
            // Create a new course object
            double credits = 99;
            String courseCode = "ERROR";
            String courseSubject = "ERROR";
            String courseNumber = "999";
            String courseTitle = "ERROR";
            String sectionType = "";
            List<Day> days = new ArrayList<Day>();
            int crn = 0;
            String instructor = "";
            String location = "";
            int startHour = 0;
            //So that the time will be 0
            int startMinute = 5;
            int endHour = 0;
            //So that the time will be 0
            int endMinute = 55;
            String dates = "";
            int capacity = 0;
            int seatsAvailable = 0;
            int seatsRemaining = 0;
            int waitlistCapacity = 0;
            int waitlistAvailable = 0;
            int waitlistRemaining = 0;


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
                        // Course code
                        case 2:
                            courseCode = row.text();
                            courseSubject = row.text();
                            break;
                        case 3:
                            courseCode += " " + row.text();
                            courseNumber = row.text();
                            break;
                        // Section type
                        case 5:
                            sectionType = row.text();
                            break;
                        // Number of credits
                        case 6:
                            credits = Double.parseDouble(row.text());
                            break;
                        // Course title
                        case 7:
                            //Remove the extra period at the end of the course title
                            courseTitle = row.text().substring(0, row.text().length() - 1);
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
                                startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
                                startMinute = Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
                                endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
                                endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
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
                            }
                            //Try/Catch for classes with no assigned times
                            catch (NumberFormatException e) {
                                startHour = 0;
                                //So that the time will be 0
                                startMinute = 5;
                                endHour = 0;
                                //So that the time will be 0
                                endMinute = 55;
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
                            dates = row.text();
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
                    e.printStackTrace();
                }
            }
            rowsSoFar = 0;
            if( !courseCode.equals("ERROR")){
                //Create a new course object and add it to list
                classItems.add(new ClassItem(term, courseCode, courseSubject, courseNumber, courseTitle, crn, "", startHour,
                        startMinute, endHour, endMinute, days, sectionType, location, instructor,
                        capacity, seatsAvailable, seatsRemaining, waitlistCapacity, waitlistAvailable, waitlistRemaining,
                        credits, dates));
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

                    if(link.toString().contains(Connection.REGISTRATION_ERROR)){
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
     * Parses an HTML String into a list of eBill Items
     * @param ebillHTML The HTML String
     */
    public static void parseEbill(String ebillHTML){
        List<EbillItem> ebillItems = new ArrayList<EbillItem>();

        //Parse the string to get the relevant info
        Document doc = Jsoup.parse(ebillHTML);
        Element ebillTable = doc.getElementsByClass("datadisplaytable").first();
        Elements rows = ebillTable.getElementsByTag("tr");

        for (int i = 2; i < rows.size(); i+=2) {
            Element row = rows.get(i);
            Elements cells = row.getElementsByTag("td");
            String statementDate = cells.get(0).text();
            String dueDate = cells.get(3).text();
            String amountDue = cells.get(5).text();
            ebillItems.add(new EbillItem(statementDate, dueDate, amountDue));
        }

        App.setEbill(ebillItems);
    }

    /**
     * Parsed an HTML String into the user info
     * @param ebillHTML The HTML String
     */
    public static void parseUserInfo(String ebillHTML){
        //Parse the string to get the relevant info
        Document doc = Jsoup.parse(ebillHTML);
        Element ebillTable = doc.getElementsByClass("datadisplaytable").first();

        //Parse the user info
        Elements userInfo = ebillTable.getElementsByTag("caption");
        String id = userInfo.get(0).text().replace("Statements for ", "");
        String[] userInfoItems = id.split(" - ");

        App.setUserInfo(new UserInfo(userInfoItems[1].trim(), userInfoItems[0].trim()));
    }
}
