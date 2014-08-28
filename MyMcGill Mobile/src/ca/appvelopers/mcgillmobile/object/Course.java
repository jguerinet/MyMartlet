package ca.appvelopers.mcgillmobile.object;

import java.io.Serializable;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will contain information pertaining to individual McGill courses that students
 * have taken, such as the grade, credit, and class average
 */
public class Course implements Serializable{
    private static final long serialVersionUID = 1L;

    private Term mTerm;
    private String mCourseCode;
    private String mCourseTitle;
    private double mCredits;
    private String mUserGrade;
    private String mAverageGrade;

    public Course(Term term, String courseTitle, String courseCode, double credits,
                    String userGrade, String averageGrade){
        this.mTerm = term;
        this.mCourseCode = courseCode;
        this.mCourseTitle = courseTitle;
        this.mCredits = credits;
        this.mUserGrade = userGrade;
        this.mAverageGrade = averageGrade;
    }

    /* GETTERS */
    /**
     * Get the course code
     * @return The course code
     */
    public String getCourseCode(){
        return mCourseCode;
    }

    /**
     * Get the course title
     * @return The course title
     */
    public String getCourseTitle(){
        return mCourseTitle;
    }

    /**
     * Get the course credits
     * @return The course credits
     */
    public double getCredits(){
        return mCredits;
    }

    /**
     * Get the grade the user got in this course
     * @return The user's grade
     */
    public String getUserGrade(){
        return mUserGrade;
    }

    /**
     * Get the average grade for this course
     * @return The average grade
     */
    public String getAverageGrade(){
        return mAverageGrade;
    }

    /**
     * Get the term for this course
     * @return The course term
     */
    public Term getTerm(){
        return mTerm;
    }
}
