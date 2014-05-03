package ca.mcgill.mymcgill.object;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will retrieve and parse transcript data from Minerva and hold all the information
 * that is shown on the transcript
 *
 */
public class Transcript implements Serializable{

    private double mCgpa;
    private int mTotalCredits;
    private List<Semester> semesters = new ArrayList<Semester>();

    //Constructor for the Transcript object
    public Transcript(String rawTranscript){
        parseTranscript(rawTranscript);
    }

    //Create an array of semesters and set CGPA and total credits
    private void parseTranscript(String transcriptString){
        Document transcript = Jsoup.parse(transcriptString);

        //Extract program, scholarships, total credits, and CGPA
        Elements rows = transcript.getElementsByClass("fieldmediumtext");

        /**
         * Main loop:
         * This will iterate through every row of the transcript data and check for various tokens
         * Once a match is found, the value in the appropriate row will be saved to a variable
         */

        // row iterates through all of the rows in the transcript searching for tokens
        // dataRow finds the rows containing the data once a token is found
        Element dataRow;

        int index = 0;
        for (Element row : rows){

            try{
                //Check the text at the start of the row
                //If it matches one of the tokens, take the corresponding data out
                //of one of the following rows, depending on the HTML layout
                if(row.text().startsWith(Token.CUM_GPA.getString())){
                    dataRow = rows.get(index+1);
                    mCgpa = Double.parseDouble(dataRow.text());
                }
                if(row.text().startsWith(Token.TOTAL_CREDITS.getString())){
                    dataRow = rows.get(index+1);
                    mTotalCredits = (int)Double.parseDouble(dataRow.text());
                }

                //Extract semester information
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

                        //Extract semester information
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

                            if(degreeDetails[1].contains("0")){
                                programYear = 0;
                            }
                            if(degreeDetails[1].contains("1")){
                                programYear = 1;
                            }
                            if(degreeDetails[1].contains("2")){
                                programYear = 2;
                            }
                            if(degreeDetails[1].contains("3")){
                                programYear = 3;
                            }
                            if(degreeDetails[1].contains("4")){
                                programYear = 4;
                            }
                            if(degreeDetails[1].contains("5")){
                                programYear = 5;
                            }

                            program = degreeDetails[2];
                        }

                        //End of semester information, extract term GPA and credits
                        else if(dataRow.text().startsWith(Token.TERM_GPA.getString())){
                            termGPA = Double.parseDouble(rows.get(semesterIndex + 1).text());
                        }

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

                            }

                            //Obtain user's grade
                            String userGrade = rows.get(semesterIndex+4).text();

                            //Check for deferred classes
                            if(userGrade.equals("L")){
                                userGrade = rows.get(semesterIndex+13).text();
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

                            Course course = new Course(courseTitle, courseCode, credits,
                                    userGrade, averageGrade);

                            courses.add(course);
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
                                credits = extractCredits(new String(courseCode));

                                Course course = new Course("", courseCode, credits, userGrade, averageGrade);
                                courses.add(course);
                            }

                            //Individual transferred courses listed
                            else{

                                //Try checking for the number of credits transferred per course
                                try{
                                    courseCode = rows.get(semesterIndex + 2).text();
                                    courseTitle = rows.get(semesterIndex + 3).text() + " " + rows.get(semesterIndex+4).text();
                                    credits = Integer.parseInt(rows.get(semesterIndex + 5).text());

                                    Course course = new Course(courseTitle, courseCode, credits, userGrade, averageGrade);
                                    courses.add(course);
                                }

                                //Number of credits per course not listed
                                catch(NumberFormatException e){
                                    try{
                                        courseCode = rows.get(semesterIndex + 2).text();
                                        courseTitle = "";

                                        credits = extractCredits(new String(courseCode));

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

                                        Course course = new Course(courseTitle, courseCode, credits, userGrade, averageGrade);
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
            }

            catch(NumberFormatException e){
                mTotalCredits = 100000;
                mCgpa = 5;
            }
            index++;
        }
    }

    private int extractCredits(String creditString){
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

    //Getter for CGPA
    public double getCgpa(){
        return mCgpa;
    }

    //Getter for totalCredits
    public int getTotalCredits(){
        return mTotalCredits;
    }

    //Getter for semesters
    public List<Semester> getSemesters(){

        //Return semesters in reverse chronological order
        ArrayList<Semester> reversedSemesters = new ArrayList<Semester>(semesters);
        Collections.reverse(reversedSemesters);
        return reversedSemesters;
    }
}
