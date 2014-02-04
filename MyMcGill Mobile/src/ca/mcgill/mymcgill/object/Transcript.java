package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import ca.mcgill.mymcgill.objects.Token;

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
    private String mScholarships;
    private String mRawTranscript;
    private Document transcript;
    private List<Semester> semesters = new ArrayList<Semester>();

    //Constructor for the Transcript object
    public Transcript(String rawTranscript){

        this.mRawTranscript = rawTranscript;
        parseTranscript(rawTranscript);
    }

    //Create an array of semesters and set CGPA and total credits
    private void parseTranscript(String transcriptString){

        transcript = Jsoup.parse(transcriptString);

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
                if(row.text().startsWith(Token.CREDITS_REQUIRED.getString())){
                    //TODO: FILL THIS IN
                }

                //Extract semester information
                if(row.text().startsWith(Token.FALL.getString()) ||
                        row.text().startsWith(Token.WINTER.getString()) ||
                        row.text().startsWith(Token.SUMMER.getString())){

                    //Initialize variables
                    String semesterName = row.text();
                    String program = "";
                    String bachelor = "";
                    int programYear = 99;
                    int termCredits = 0;
                    double termGPA = 99;
                    boolean fullTime = false;
                    boolean satisfactory = false;
                    List<Course> courses = new ArrayList<Course>();

                    //Search rows until academic standing is found or the end of the transcript is reached
                    int semesterIndex = index +1;
                    dataRow = rows.get(semesterIndex);

                    while(true){

                        //Extract semester information
                        if(dataRow.text().startsWith(Token.BACHELOR.getString())){
                            //TODO: EXTRACT INDIVIDUAL WORDS
                            program = dataRow.text();
                            bachelor = "";
                            programYear = 50;
                        }

                        //End of semester information, extract term GPA and credits
                        else if(dataRow.text().startsWith(Token.TERM_GPA.getString())){
                            termGPA = Double.parseDouble(rows.get(semesterIndex + 1).text());
                        }

                        else if(dataRow.text().startsWith(Token.TERM_CREDITS.getString())){
                            termCredits = (int)Double.parseDouble(rows.get(semesterIndex + 1).text());
                        }

                        else if(dataRow.text().startsWith(Token.STANDING.getString())){
                            //TODO: EXTRACT SATISFACTORY/UNSATISFACTORY
                            break;
                        }

                        //Extract course information if row contains a course code
                        else if(dataRow.text().matches("[A-Za-z]{4} [0-9]{3}")){

                            String courseCode = dataRow.text();
                            String courseTitle = rows.get(semesterIndex + 2).text();
                            int credits = Integer.parseInt(rows.get(semesterIndex + 3).text());
                            String userGrade = rows.get(semesterIndex+4).text();

                            //If average grades haven't been released on minerva, index will be null
                            String averageGrade;
                            try{
                                averageGrade = rows.get(semesterIndex+7).text();
                            }
                            catch(IndexOutOfBoundsException e){
                                averageGrade = "";
                            }


                            Course course = new Course(courseTitle, courseCode, credits,
                                    userGrade, averageGrade);

                            courses.add(course);
                        }

                        semesterIndex++;

                        //Reached the end of the transcript
                        try{
                            dataRow = rows.get(semesterIndex);
                        }
                        catch(IndexOutOfBoundsException e){
                            break;
                        }
                    }

                    Semester semester = new Semester(semesterName, program, bachelor, programYear,
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
        return semesters;
    }

    public String getmScholarships(){
        return mScholarships;
    }

    public String getmRawTranscript(){
        return mRawTranscript;
    }



}
