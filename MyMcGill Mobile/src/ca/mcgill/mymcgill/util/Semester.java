package ca.mcgill.mymcgill.util;

import java.util.ArrayList;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will contain information pertaining to each semester such as current program,
 * term credits, term GPA, and full time status
 */
public class Semester {

    private String program;
    private int programYear;
    private int termCredits;
    private int termGPA;
    private boolean fullTime;
    private String courseString;

    ArrayList<Course> courses;


    public Semester(String program, int programYear, int termCredits, int termGPA,
                        boolean fullTime, String courseString) {
        this.program = program;
        this.termCredits = termCredits;
        this.termGPA = termGPA;
        this.programYear = programYear;
        this.fullTime = fullTime;
        this.courseString = courseString;

        parseCourses(courseString);
    }


    //Takes as input a string of course information and converts it to Course objects
    private void parseCourses(String courseString){

        //TODO: Create parsing code here

        courses = new ArrayList<Course>();
    }

    //Getter for program
    public String getProgram() {
        return program;
    }

    //Getter for term credits
    public int getTermCredits() {
        return termCredits;
    }

    //Getter for term GPA
    public int getTermGPA() {
        return termGPA;
    }

    //Getter for program year
    public int getProgramYear() {
        return programYear;
    }

    public boolean isFullTime() {
        return fullTime;
    }
}
