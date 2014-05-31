package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private String mDates;

    private String mUserGrade;
    private String mAverageGrade;

    public Course(Season season, int year, int crn, String courseCode, String courseTitle, String section,
                  int startHour, int startMinute, int endHour, int endMinute, List<Day> days,
                  String sectionType, String location, String instructor, int credits) {
        this.mSeason = season;
        this.mYear = year;
        this.mCRN = crn;
        this.mCourseCode = courseCode;
        this.mCourseTitle = courseTitle;
        this.mSection = section;

        this.mDays = days;
        this.mSectionType = sectionType;
        this.mLocation = location;
        this.mInstructor = instructor;
        this.mCredits = credits;
    }
    public Course(Season season, int year, String courseTitle, String courseCode, int credits,
                    String userGrade, String averageGrade){
        this.mCredits = credits;
        this.mCourseCode = courseCode;
        this.mCourseTitle = courseTitle;
        this.mUserGrade = userGrade;
        this.mAverageGrade = averageGrade;

        this.mSeason = season;
        this.mYear = year;
        this.mCRN = -1;
        this.mCourseCode = courseCode;
        this.mCourseTitle = courseTitle;
        this.mSection = "";
        this.mActualStartTime = null;
        this.mStartTime = null;
        this.mActualEndTime = null;
        this.mEndTime = null;
        this.mDays = new ArrayList<Day>();
        this.mSectionType = "";
        this.mLocation = "";
        this.mInstructor = "";
        this.mCredits = credits;
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

    /* HELPER METHODS */
    /**
     * Checks to see if two courses are equal
     * @param object The course to check
     * @return True if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object object){
        if(!(object instanceof Course)){
            return false;
        }
        Course course = (Course)object;

        //Check if they have the same season, year, and CRN
        return this.mCRN == course.mCRN && this.mYear == course.mYear &&
                this.mSeason == course.mSeason;
    }

}
