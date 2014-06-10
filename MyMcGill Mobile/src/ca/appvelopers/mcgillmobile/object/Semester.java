package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.util.Connection;

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
    private int termCredits;
    private double termGPA;
    private boolean fullTime;
    private boolean mSatisfactory;
    private List<Course> courses;


    public Semester(Term term, String program, String bachelor, int programYear, int termCredits, double termGPA,
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

    //Getter for the semester's courses
    public List<Course> getCourses(){
        return courses;
    }

    public String getURL(){
        return Connection.minervaSchedulePrefix + mTerm.getYear() + mTerm.getSeason().getSeasonNumber();
    }

    /**
     * Get the semester term
     * @return The semester term
     */
    public Term getTerm(){
        return mTerm;
    }

    /**
     * Check if the current semester is after the given semester
     * @param semester The semester to compare
     * @return True if the current semester is after, false, otherwise
     */
    public boolean isAfter(Semester semester){
        return mTerm.isAfter(semester.getTerm());
    }

    public static Semester getSemester(Term term){
        for(Semester semester : App.getTranscript().getSemesters()){
            if(semester.getTerm().equals(term)){
                return semester;
            }
        }
        return null;
    }
}
