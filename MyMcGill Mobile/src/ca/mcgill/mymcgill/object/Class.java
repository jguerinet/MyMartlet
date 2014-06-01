package ca.mcgill.mymcgill.object;

import android.content.Context;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

import ca.mcgill.mymcgill.util.Help;

/**
 * CourseSched
 * @author Quang
 * 
 */
public class Class implements Serializable{
    private static final long serialVersionUID = 1L;

    private Season mSeason;
    private int mYear;
    private String mCourseCode;
    private String mCourseTitle;
    private int mCRN;
    private String mSection;
    private LocalTime mStartTime, mEndTime, mActualStartTime, mActualEndTime;
    private List<Day> mDays;
    private String mSectionType;
    private String mLocation;
    private String mInstructor;
    private int mCredits;
    private String mDates;

    public Class(Season season, int year, String courseCode, String courseTitle, int crn,
                 String section, int startHour, int startMinute, int endHour, int endMinute,
                 List<Day> days, String sectionType, String location, String instructor, int credits,
                 String dates){
        this.mSeason = season;
        this.mYear = year;
        this.mCourseCode = courseCode;
        this.mCourseTitle = courseTitle;
        this.mCRN = crn;
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
    public String getCourseTitle() {
        return mCourseTitle;
    }

    /**
     * Get the course CRN
     * @return The course CRN
     */
    public int getCRN(){
        return mCRN;
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


    /* HELPER METHODS */

    /**
     * Get the String representing the class time
     * @return The class time in String format
     */
    public String getTimeString(Context context){
        return Help.getLongTimeString(context, mActualStartTime.getHourOfDay(), mActualStartTime.getMinuteOfHour()) +
                " - " + Help.getLongTimeString(context, mActualEndTime.getHourOfDay(), mActualEndTime.getMinuteOfHour());
    }

    /**
     * Checks to see if two classes are equal
     * @param object The course to check
     * @return True if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object object){
        if(!(object instanceof Class)){
            return false;
        }
        Class aClass = (Class)object;

        //Check if they have the same season, year, and CRN
        return this.mCRN == aClass.mCRN && this.mYear == aClass.mYear &&
                this.mSeason == aClass.mSeason;
    }
}