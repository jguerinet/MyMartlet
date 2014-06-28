package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will contain information pertaining to each semester such as current program,
 * term credits, term GPA, and full time status
 */
public class Semester implements Serializable{
    private static final long serialVersionUID = 1L;

    private Term mTerm;
    private String mProgram;
    private String mBachelor;
    private int programYear;
    private double termCredits;
    private double termGPA;
    private boolean fullTime;
    private boolean mSatisfactory;
    private List<Course> courses;


    public Semester(Term term, String program, String bachelor, int programYear, double termCredits, double termGPA,
                        boolean fullTime, boolean satisfactory, List<Course> courses) {
        this.mTerm = term;
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
        return mTerm.toString(context);
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
    public double getTermCredits() {
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

    //Getter for the semester's courses
    public List<Course> getCourses(){
        return courses;
    }

    /**
     * Get the semester term
     * @return The semester term
     */
    public Term getTerm(){
        return mTerm;
    }
}
