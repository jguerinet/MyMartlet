package ca.mcgill.mymcgill.object;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import ca.mcgill.mymcgill.util.Connection;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will contain information pertaining to each semester such as current program,
 * term credits, term GPA, and full time status
 */
public class Semester implements Serializable{
    private static final long serialVersionUID = 1L;

    private Season mSeason;
    private int mYear;
    private String mProgram;
    private String mBachelor;
    private int programYear;
    private int termCredits;
    private double termGPA;
    private boolean fullTime;
    private boolean mSatisfactory;
    private List<Course> courses;


    public Semester(Season season, int year , String program, String bachelor, int programYear, int termCredits, double termGPA,
                        boolean fullTime, boolean satisfactory, List<Course> courses) {
        this.mSeason = season;
        this.mYear = year;
        this.mProgram = program;
        this.mBachelor = bachelor;
        this.termCredits = termCredits;
        this.termGPA = termGPA;
        this.programYear = programYear;
        this.fullTime = fullTime;
        this.mSatisfactory = satisfactory;
        this.courses = courses;
    }

    //Getter for the semester name
    public String getSemesterName(Context context){
        return mSeason.toString(context) + " " + mYear;
    }

    //Getter for the Season
    public Season getSeason(){
        return mSeason;
    }

    //Getter for the year
    public int getYear(){
        return mYear;
    }

    //Getter for program
    public String getProgram() {
        return mProgram;
    }

    //Getter for the bachelor
    public String getBachelor(){
        return mBachelor;
    }

    //Getter for term credits
    public int getTermCredits() {
        return termCredits;
    }

    //Getter for term GPA
    public double getTermGPA() {
        return termGPA;
    }

    //Getter for program year
    public int getProgramYear() {
        return programYear;
    }

    public boolean isFullTime() {
        return fullTime;
    }

    //Getter for the semester's searchedCourses
    public List<Course> getCourses(){
        return courses;
    }

    public String getURL(){
        return Connection.minervaSchedulePrefix + mYear + mSeason.getSeasonNumber();
    }
}
