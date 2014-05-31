package ca.mcgill.mymcgill.object;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.List;

/**
 * CourseSched
 * @author Quang
 * 
 */
public class Class implements Serializable{
    private static final long serialVersionUID = 1L;

    private Course mCourse;
    private int mCRN;
    private String mSection;
    private LocalTime mStartTime, mEndTime, mActualStartTime, mActualEndTime;
    private List<Day> mDays;
    private String mSectionType;
    private String mLocation;
    private String mInstructor;
    private int mCredits;
    private String mDates;

    public Class(Course course, int crn, String section, int startHour, int startMinute, int endHour,
                 int endMinute, List<Day> days, String sectionType, String location, String instructor,
                 int credits, String dates){
        this.mCourse = course;
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
}