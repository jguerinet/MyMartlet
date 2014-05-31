package ca.mcgill.mymcgill.object;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will contain information pertaining to individual McGill searchedCourses that students
 * have taken, such as the grade, credit, and class average
 */
public class Course implements Serializable{
    private static final long serialVersionUID = 1L;

    private int mCRN;
    private String mCourseCode;
    private String mCourseTitle;
    private LocalTime mStartTime, mEndTime, mActualStartTime, mActualEndTime;
    private List<Day> mDays;
    private String mSectionType;
    private String mLocation;
    private String mInstructor;
    private int mCredits;
    private String mDates;

    private String mUserGrade;
    private String mAverageGrade;

    //Constructor for the Course object
    public Course(String courseTitle, String courseCode, int credits,
                    String userGrade, String averageGrade){
        this.mCredits = credits;
        this.mCourseCode = courseCode;
        this.mCourseTitle = courseTitle;
        this.mUserGrade = userGrade;
        this.mAverageGrade = averageGrade;
    }

    //Constructor for course wishlist
    public Course(int credits, String courseCode, String courseTitle, String sectionType, String days, int crn, String instructor, String location, String time, String dates) {
        this.mCredits = credits;
        this.mCourseCode = courseCode;
        this.mCourseTitle = courseTitle;
        this.mSectionType = sectionType;
        this.mDays = days;
        this.mCRN = crn;
        this.mInstructor = instructor;
        this.mLocation = location;
        this.time = time;
        this.mDates = dates;
    }

    /* GETTERS */
    /**
     * Get the course CRN
     * @return The course CRN
     */
    public int getCRN(){
        return mCRN;
    }

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
     * Get the start time of the course (rounded off to the nearest half hour)
     * @return The course start time
     */
    public LocalTime getStartTime(){
        return mStartTime;
    }

    /**
     * Get the end time of the course (rounded off the the nearest half hour)
     * @return The course end time
     */
    public LocalTime getEndTime(){
        return mEndTime;
    }

    /**
     * Get the actual start time of the course given by Minerva
     * @return The actual course start time
     */
    public LocalTime getActualStartTime(){
        return mActualStartTime;
    }

    /**
     * Get the actual end time of the course given by Minerva
     * @return The actual course end time
     */
    public LocalTime getActualEndTime(){
        return mActualEndTime;
    }

    /**
     * Get the days this course is on
     * @return The course days
     */
    public List<Day> getDays(){
        return mDays;
    }

    /**
     * Get the course section type
     * @return The course section type
     */
    public String getSectionType(){
        return mSectionType;
    }

    /**
     * Get the course's location
     * @return The course's location
     */
    public String getLocation(){
        return mLocation;
    }

    /**
     * Get the instructor for this course
     * @return Return the course's instructor
     */
    public String getInstructor(){
        return mInstructor;
    }

    /**
     * Get the course credits
     * @return The course credits
     */
    public int getCredits(){
        return mCredits;
    }

    /**
     * Get the dates this course is on
     * @return The course dates
     */
    public String getDates(){
        return mDates;
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

    /* SETTER METHODS */

    /**
     * Set the user grade for this course
     * @param userGrade The user's grade
     */
    public void setUserGrade(String userGrade){
        mUserGrade = userGrade;
    }

    /**
     * Set the average grade for this course
     * @param averageGrade The course's average grade
     */
    public void setAverageGrade(String averageGrade){
        mAverageGrade = averageGrade;
    }

    /* HELPER METHODS */
    /**
     * Checks to see if two courses are equal
     * @param course The course to check
     * @return True if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object course){
        if(!(course instanceof Course)){
            return false;
        }
        return this.mCRN == ((Course)course).mCRN;
    }

}
