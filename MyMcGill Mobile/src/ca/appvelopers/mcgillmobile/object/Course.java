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

    private Season mSeason;
    private int mYear;
    private String mCourseCode;
    private String mCourseTitle;
    private int mCredits;
    private String mUserGrade;
    private String mAverageGrade;

    public Course(Season season, int year, String courseTitle, String courseCode, int credits,
                    String userGrade, String averageGrade){
        this.mSeason = season;
        this.mYear = year;
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
    public int getCredits(){
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
     * Get the season for this course
     * @return The course season
     */

    public Season getSeason(){
        return mSeason;
    }

    /**
     * Get the year for this course
     * @return The course year
     */

    public int getYear(){
        return mYear;
    }
}
