package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import org.joda.time.DateTime;
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
    private String mCourseSubject;
    private String mCourseNumber;
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
    private double mCredits;
    private String mDates;
    private DateTime mStartDateRange;
    private DateTime mEndDateRange;

    /**
     * Constructor
     * @param term
     * @param courseCode
     * @param courseSubject
     * @param courseNumber
     * @param courseTitle
     * @param crn
     * @param section
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     * @param days
     * @param sectionType
     * @param location
     * @param instructor
     * @param capacity
     * @param seatsAvailable
     * @param seatsRemaining
     * @param waitlistCapacity
     * @param waitlistAvailable
     * @param waitlistRemaining
     * @param credits
     * @param startDateRange
     * @param endDateRange
     */
    public ClassItem(Term term, String courseCode, String courseSubject, String courseNumber, String courseTitle, int crn,
                     String section, int startHour, int startMinute, int endHour, int endMinute,
                     List<Day> days, String sectionType, String location, String instructor, int capacity,
                     int seatsAvailable, int seatsRemaining, int waitlistCapacity, int waitlistAvailable,
                     int waitlistRemaining, double credits, String dates, DateTime startDateRange, DateTime endDateRange){
        this.mTerm = term;
        this.mCourseCode = courseCode;
        this.mCourseSubject = courseSubject;
        this.mCourseNumber = courseNumber;
        this.mCourseTitle = courseTitle;
        this.mCRN = crn;
        this.mSection = section;
        this.mActualStartTime = new LocalTime(startHour, startMinute);
        //Remove 5 minutes to the start to get round numbers
        int newStartMin = (startMinute - 5) % 60;
        if(newStartMin < 0){
            newStartMin = startMinute;
        }
        this.mStartTime = new LocalTime(startHour, newStartMin);this.mActualEndTime = new LocalTime(endHour, endMinute);
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
        this.mStartDateRange = startDateRange;
        this.mEndDateRange = endDateRange;
    }

    /**
     * Constructor without date range
     * @param term
     * @param courseCode
     * @param courseSubject
     * @param courseNumber
     * @param courseTitle
     * @param crn
     * @param section
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     * @param days
     * @param sectionType
     * @param location
     * @param instructor
     * @param capacity
     * @param seatsAvailable
     * @param seatsRemaining
     * @param waitlistCapacity
     * @param waitlistAvailable
     * @param waitlistRemaining
     * @param credits
     * @param dates
     */
    public ClassItem(Term term, String courseCode, String courseSubject, String courseNumber, String courseTitle, int crn,
                     String section, int startHour, int startMinute, int endHour, int endMinute,
                     List<Day> days, String sectionType, String location, String instructor, int capacity,
                     int seatsAvailable, int seatsRemaining, int waitlistCapacity, int waitlistAvailable,
                     int waitlistRemaining, double credits, String dates){
        this.mTerm = term;
        this.mCourseCode = courseCode;
        this.mCourseSubject = courseSubject;
        this.mCourseNumber = courseNumber;
        this.mCourseTitle = courseTitle;
        this.mCRN = crn;
        this.mSection = section;
        this.mActualStartTime = new LocalTime(startHour, startMinute);
        //Remove 5 minutes to the start to get round numbers
        int newStartMin = (startMinute - 5) % 60;
        if(newStartMin < 0){
            newStartMin = startMinute;
        }
        this.mStartTime = new LocalTime(startHour, newStartMin);this.mActualEndTime = new LocalTime(endHour, endMinute);
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
        this.mStartDateRange = null;
        this.mEndDateRange = null;
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
     * Get the course subject
     * @return The course subject
     */
    public String getCourseSubject(){
        return mCourseSubject;
    }

    /**
     * Get the course number
     * @return The course number
     */
    public String getCourseNumber(){
        return mCourseNumber;
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
    public double getCredits(){
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

    /**
     * Checks that the course is for the given date
     * @param date The given date
     * @return True if it is, false otherwise
     */
    public boolean isForDate(DateTime date){
        return !date.isBefore(mStartDateRange) || !date.isAfter(mEndDateRange);
    }

    /**
     * Set the start time of the course (rounded off to the nearest half hour)
     */
    public void setStartTime(LocalTime time){
        this.mStartTime = time;
    }

    /**
     * Set the end time of the course (rounded off the the nearest half hour)
     */
    public void setEndTime(LocalTime time){
        this.mEndTime = time;
    }

    /**
     * Set the days this course is on
     */
    public void setDays(List<Day> days){
        this.mDays = days;
    }

    /**
     * Set the course's location
     */
    public void setLocation(String location){
        this.mLocation = location;
    }

    /**
     * Set the instructor for this course
     */
    public void setInstructor(String instructor){
        this.mInstructor = instructor;
    }

    /**
     * Set the dates this course is on
     */
    public void setDates(String dates){
        this.mDates = dates;
    }

    /**
     * Set the number of spots remaining
     */
    public void setSeatsRemaining(int seatsRemaining){
        this.mSeatsRemaining = seatsRemaining;
    }

    /**
     * Set the number of waitlist spots remaining
     */
    public void setWaitlistRemaining(int waitlistRemaining){
        this.mWaitlistRemaining = waitlistRemaining;
    }

    /**
     * Update the start date of the date range
     * @param startDateRange
     */
    public void setStartDateRange(DateTime startDateRange) {
        this.mStartDateRange = startDateRange;
    }

    /**
     * Update the end date of the date range
     * @param endDateRange
     */
    public void setEndDateRange(DateTime endDateRange) {
        this.mEndDateRange = endDateRange;
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
     * @param startDateRange start date of date range
     * @param endDateRange end date of date range
     */
    public void update(String courseCode, String courseTitle, String section, int startHour, int startMinute,
                       int endHour, int endMinute, List<Day> days, String sectionType, String location, String instructor,
                       double credits, String dates, DateTime startDateRange, DateTime endDateRange){
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
        this.mDates = dates;
        this.mStartDateRange = startDateRange;
        this.mEndDateRange = endDateRange;
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