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

    private Season mSeason;
    private int mYear;
    private int mCRN;
    private String mCourseCode;
    private String mCourseTitle;
    private String mSection;
    private LocalTime mStartTime, mEndTime, mActualStartTime, mActualEndTime;
    private List<Day> mDays;
    private String mSectionType;
    private String mLocation;
    private String mInstructor;
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
        this.mActualStartTime = new LocalTime(startHour, startMinute);
        //Remove 5 minutes to the start to get round numbers
        this.mStartTime = new LocalTime(startHour, (startMinute - 5) % 60);
        this.mActualEndTime = new LocalTime(endHour, endMinute);
        //Add 5 minutes to the end to get round numbers, increment the hour if the minutes get set to 0s
        int endM = (endMinute + 5) % 60;
        int endH = endHour;
        if(endM == 0){
            endH ++;
        }
        this.mEndTime = new LocalTime(endH, endM);
        this.mDays = days;
        this.mSectionType = sectionType;
        this.mLocation = location;
        this.mInstructor = instructor;
        this.mCredits = credits;
    }
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
     * Get the Section the user is in
     * @return The course section
     */
    public String getSection(){
        return mSection;
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
