package ca.mcgill.mymcgill.object;

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
    private static final long serialVersionUID = 1L;

    private double mCGPA;
    private int mTotalCredits;
    private List<Semester> mSemesters;

    //Constructor for the Transcript object
    public Transcript(double cgpa, int totalCredits, List<Semester> semesters){
        this.mCGPA = cgpa;
        this.mTotalCredits = totalCredits;
        this.mSemesters = semesters;
    }

    //Getter for CGPA
    public double getCgpa(){
        return mCGPA;
    }

    //Getter for totalCredits
    public int getTotalCredits(){
        return mTotalCredits;
    }

    //Getter for semesters
    public List<Semester> getSemesters(){

        //Return semesters in reverse chronological order
        ArrayList<Semester> reversedSemesters = new ArrayList<Semester>(mSemesters);
        Collections.reverse(reversedSemesters);
        return reversedSemesters;
    }
}
