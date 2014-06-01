package ca.mcgill.mymcgill.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.object.ClassItem;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.object.Day;
import ca.mcgill.mymcgill.object.Season;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.object.Token;
import ca.mcgill.mymcgill.object.Transcript;

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
        int totalCredits = 0;

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
                }
            }
            //Credits
            if(row.text().startsWith(Token.TOTAL_CREDITS.getString())){
                dataRow = rows.get(index+1);
                try{
                    totalCredits = (int)Double.parseDouble(dataRow.text());
                }
                catch (NumberFormatException e){
                    totalCredits = -1;
                }
            }
            //Semester Information
            if(row.text().startsWith(Token.FALL.getString()) ||
                    row.text().startsWith(Token.WINTER.getString()) ||
                    row.text().startsWith(Token.SUMMER.getString())){

                //Initialize variables
                String scheduleSemester = row.text().trim();
                String[] scheduleSemesterItems = scheduleSemester.split("\\s+");
                //Find the right season and year
                Season season = Season.findSeason(scheduleSemesterItems[0]);
                int year = Integer.valueOf(scheduleSemesterItems[1]);

                String program = "";
                String bachelor = "";
                int programYear = 99;
                int termCredits = 0;
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
                    //Semester Info
                    if(dataRow.text().startsWith(Token.BACHELOR.getString()) ||
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

                        program = degreeDetails[2];
                    }
                    //Term GPA
                    else if(dataRow.text().startsWith(Token.TERM_GPA.getString())){
                        termGPA = Double.parseDouble(rows.get(semesterIndex + 1).text());
                    }
                    //Term Credits
                    else if(dataRow.text().startsWith(Token.TERM_CREDITS.getString())){
                        termCredits = (int)Double.parseDouble(rows.get(semesterIndex + 2).text());
                    }

                    //Extract course information if row contains a course code
                    //Regex looks for a string in the form "ABCD ###"
                    else if(dataRow.text().matches("[A-Za-z]{4} [0-9]{3}.*")){
                        String courseCode = "";
                        //One semester courses are in the form ABCD ###
                        if(dataRow.text().matches("[A-Za-z]{4} [0-9]{3}")){
                            courseCode = dataRow.text();
                        }
                        //Multi semester courses are in the form ABCD ###D#
                        else{
                            //Extract first seven characters from string
                            try{
                                courseCode = dataRow.text().substring(0, 10);
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }

                        String courseTitle = rows.get(semesterIndex + 2).text();

                        //Failed courses are missing the earned credits row
                        int credits = 0;

                        //Check row to see if earned credit exists
                        try{
                            credits = Integer.parseInt(rows.get(semesterIndex + 6).text());
                        }
                        catch(NumberFormatException e){
                            //Course failed -> Earned credit = 0
                        }
                        catch(IndexOutOfBoundsException e){
                            e.printStackTrace();
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
                        }
                        courses.add(new Course(season, year, courseTitle, courseCode, credits,
                                userGrade, averageGrade));
                    }

                    //Extract transfer credit information
                    else if(dataRow.text().startsWith(Token.CREDIT_EXEMPTION.getString())){
                        String courseTitle;
                        String courseCode;
                        String userGrade = "N/A";
                        String averageGrade = "";
                        int credits = 0;

                        //Individual transferred courses not listed
                        if(!rows.get(semesterIndex + 3).text().matches("[A-Za-z]{4}.*")){
                            courseCode = rows.get(semesterIndex + 2).text();

                            //Extract the number of credits granted
                            credits = extractCredits(courseCode);

                            Course course = new Course(season, year, "", courseCode, credits, userGrade,
                                    averageGrade);
                            courses.add(course);
                        }

                        //Individual transferred courses listed
                        else{
                            //Try checking for the number of credits transferred per course
                            try{
                                courseCode = rows.get(semesterIndex + 2).text();
                                courseTitle = rows.get(semesterIndex + 3).text() + " " + rows.get(semesterIndex+4).text();
                                credits = Integer.parseInt(rows.get(semesterIndex + 5).text());

                                Course course = new Course(season, year, courseTitle, courseCode, credits,
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

                                    Course course = new Course(season, year, courseTitle, courseCode, credits,
                                            userGrade, averageGrade);
                                    courses.add(course);

                                }
                                catch(IndexOutOfBoundsException e2){
                                    e.printStackTrace();
                                }
                                catch(Exception e3){
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
                            dataRow.text().startsWith(Token.SUMMER.getString())){
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

                Semester semester = new Semester(season, year, program, bachelor, programYear,
                        termCredits, termGPA, fullTime, satisfactory, courses);

                semesters.add(semester);
            }
            index++;
        }

        App.setTranscript(new Transcript(cgpa, totalCredits, semesters));
    }

    //Extracts the number of credits
    private static int extractCredits(String creditString){
        int numCredits;

        try{
            creditString = creditString.replaceAll("\\s", "");
            String[] creditArray = creditString.split("-");
            creditArray = creditArray[1].split("credits");
            numCredits = Integer.parseInt(creditArray[0]);
            return numCredits;
        }
        catch (NumberFormatException e){
            return 99;
        }
        catch(Exception e){
            return 88;
        }
    }

    /**
     * Parses an HTML String to generate a list of classes
     * @param classHTML The HTML String to parse
     * @return The list of courses
     */
    public static void parseClassList(Season season, int year, String classHTML){
        //Get the list of classes already parsed for that year
        List<ClassItem> classItems = App.getClasses();

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
                int crn = Integer.parseInt(crnString);

                //Credits
                row = currentElement.getElementsByTag("tr").get(5);
                String creditString = row.getElementsByTag("td").first().text();
                int credits = (int) Double.parseDouble(creditString);

                //Check if there is any data to parse
                if (i + 1 < scheduleTable.size() && scheduleTable.get(i + 1).attr("summary").equals("This table lists the scheduled meeting times and assigned instructors for this class..")) {
                    //Time, Days, Location, Section Type, Instructor
                    row = currentElement.getElementsByTag("tr").get(1);
                    Elements cells = row.getElementsByTag("td");
                    String[] times = cells.get(0).text().split(" - ");
                    char[] dayCharacters = cells.get(i).text().toCharArray();
                    String location = cells.get(2).text();
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
                        startMinute = 0;
                        endHour = 0;
                        endMinute = 0;
                    }

                    //Day Parsing
                    List<Day> days = new ArrayList<Day>();
                    for (char dayCharacter : dayCharacters) {
                        days.add(Day.getDay(dayCharacter));
                    }

                    //Check if the class already exists
                    boolean classExists = false;
                    for (ClassItem classItem : classItems) {
                        if (classItem.getCRN() == crn && classItem.getSeason() == season && classItem.getYear() ==
                                year) {
                            classExists = true;
                            //If you find an equivalent, just update it
                            classItem.update(courseCode, courseTitle, section, startHour, startMinute, endHour, endMinute,
                                    days, sectionType, location, instructor, credits);
                            break;
                        }
                    }
                    //It not, add a new class item
                    if (!classExists) {
                        //Find the concerned course
                        classItems.add(new ClassItem(season, year, courseCode, courseTitle, crn, section, startHour,
                                startMinute, endHour, endMinute, days, sectionType, location, instructor, credits, null));
                    }
                }
                //If there is no data to parse, reset i and continue
                else {
                    i = i - 1;
                }
            }
        }

        //Save it to the instance variable in Application class
        App.setClassList(classItems);
    }

    /**
     * Parses the HTML retrieved from Minerva and returns a list of classes
     * @param classHTML The HTML String to parse
     * @return The list of resulting classes
     */
    public static List<ClassItem> parseClassResults(Season season, int year, String classHTML){
        List<ClassItem> classItems = new ArrayList<ClassItem>();

        Document document = Jsoup.parse(classHTML, "UTF-8");
        //Find rows of HTML by class
        Elements dataRows = document.getElementsByClass("dddefault");

        int rowNumber = 0;
        boolean loop = true;

        while (loop) {
            // Create a new course object
            int credits = 99;
            String courseCode = "ERROR";
            String courseTitle = "ERROR";
            String sectionType = "";
            List<Day> days = new ArrayList<Day>();
            int crn = 00000;
            String instructor = "";
            String location = "";
            int startHour = 0;
            int startMinute = 0;
            int endHour = 0;
            int endMinute = 0;
            String dates = "";

            int i = 0;
            while (true) {
                try {
                    // Get the HTML row
                    Element row = dataRows.get(rowNumber);
                    rowNumber++;

                    // End condition: Empty row encountered
                    if (row.toString().contains("&nbsp;") || row.toString().contains("NOTES:")) {
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
                            break;
                        case 3:
                            courseCode += " " + row.text();
                            break;
                        // Section type
                        case 5:
                            sectionType = row.text();
                            break;
                        // Number of credits
                        case 6:
                            credits = (int) Double.parseDouble(row.text());
                            break;
                        // Course title
                        case 7:
                            courseTitle = row.text();
                            break;
                        // Days of the week
                        case 8:
                            String dayString = row.text();
                            //TBA Stuff
                            if(dayString.equals("TBA")){
                                days.add(Day.TBA);
                                i = 10;
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
                            String[] times = row.text().split(" - ");
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
                                startMinute = 0;
                                endHour = 0;
                                endMinute = 0;
                            }
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
            }

            if( !courseCode.equals("ERROR")){
                //Create a new course object and add it to list
                classItems.add(new ClassItem(season, year, courseCode, courseTitle, crn, "", startHour,
                        startMinute, endHour, endMinute, days, sectionType, location, instructor, credits,
                        dates));
            }
        }
        return classItems;
    }
}
