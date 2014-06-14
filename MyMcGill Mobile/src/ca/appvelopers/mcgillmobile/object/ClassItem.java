package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * CourseClass
 * @author Quang
 * 
 */
public class ClassItem implements Serializable{
    private static final long serialVersionUID = 1L;

    private Term mTerm;
    private String mCourseCode;
    private String mCourseTitle;
    private int mCRN;
    private String mSection;
    private LocalTime mStartTime, mEndTime, mActualStartTime, mActualEndTime;
    private List<Day> mDays;
    private String mSectionType;
    private String mLocation;
    private String mInstructor;
    private int mCapacity;
    private int mSeatsAvailable;
    private int mSeatsRemaining;
    private int mWaitlistCapacity;
    private int mWaitlistAvailable;
    private int mWaitlistRemaining;
    private int mCredits;
    private String mDates;

    public ClassItem(Term term, String courseCode, String courseTitle, int crn,
                     String section, int startHour, int startMinute, int endHour, int endMinute,
                     List<Day> days, String sectionType, String location, String instructor, int capacity,
                     int seatsAvailable, int seatsRemaining, int waitlistCapacity, int waitlistAvailable,
                     int waitlistRemaining, int credits, String dates){
        this.mTerm = term;
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
        this.mCapacity = capacity;
        this.mSeatsAvailable = seatsAvailable;
        this.mSeatsRemaining = seatsRemaining;
        this.mWaitlistCapacity = waitlistCapacity;
        this.mWaitlistAvailable = waitlistAvailable;
        this.mWaitlistRemaining = waitlistRemaining;
        this.mCredits = credits;
        this.mDates = dates;
    }

	/* GETTERS */

    /**
     * Get the course term
     * @return The course term
     */
    public Term getTerm(){
        return mTerm;
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
     * Get the number of spots remaining
     * @return The number of spots remaining
     */
    public int getSeatsRemaining(){
        return mSeatsRemaining;
    }

    /**
     * Get the number of waitlist spots remaining
     * @return The number of waitlist spots remaining
     */
    public int getWaitlistRemaining(){
        return mWaitlistRemaining;
    }


    /* HELPER METHODS */
    /**
     * Update the ClassItem
     * @param courseCode The new course code
     * @param courseTitle The new course title
     * @param section The new course section
     * @param startHour The new start hour
     * @param startMinute The new start minute
     * @param endHour The new end hour
     * @param endMinute The new end minute
     * @param days The new days
     * @param sectionType The new section type
     * @param location The new location
     * @param instructor The new instructor
     * @param credits The new credits
     */
    public void update(String courseCode, String courseTitle, String section, int startHour, int startMinute,
                       int endHour, int endMinute, List<Day> days, String sectionType, String location, String instructor,
                       int credits){
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

    /**
     * Get the String representing the class time
     * @return The class time in String format
     */
    public String getTimeString(Context context){
        //No time associated, therefore no time string
        if(mStartTime.getHourOfDay() == 0 && mStartTime.getMinuteOfHour() == 0){
            return "";
        }
        return context.getResources().getString(R.string.course_time,
                Help.getLongTimeString(context, mActualStartTime.getHourOfDay(), mActualStartTime.getMinuteOfHour()),
                Help.getLongTimeString(context, mActualEndTime.getHourOfDay(), mActualEndTime.getMinuteOfHour()));
    }

    /**
     * Checks to see if two classes are equal
     * @param object The course to check
     * @return True if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object object){
        if(!(object instanceof ClassItem)){
            return false;
        }
        ClassItem classItem = (ClassItem)object;

        //Check if they have the same season, year, and CRN
        return this.mCRN == classItem.mCRN && this.mTerm.equals(classItem.getTerm());
    }
}