package ca.mcgill.mymcgill.object;

import java.io.Serializable;
import ca.mcgill.mymcgill.object.Season;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will contain information pertaining to individual McGill searchedCourses that students
 * have taken, such as the grade, credit, and class average
 */
public class Course implements Serializable{
    private static final long serialVersionUID = 1L;

    private int credits;
    private String courseCode;
    private String courseTitle;
    private String userGrade;
    private String averageGrade;
    private String sectionType;
    private String days;
    private int crn;
    private String instructor;
    private String location;
    private String time;
    private String dates;
    private String semester;
    private int capacity;
    private int seatsAvailable;
    private int seatsRemaining;
    private int waitlistCapacity;
    private int waitlistAvailable;
    private int waitlistRemaining;
    private Season mSeason;

    //Constructor for the Course object
    public Course(String courseTitle, String courseCode, int credits,
                    String userGrade, String averageGrade){
        this.credits = credits;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.userGrade = userGrade;
        this.averageGrade = averageGrade;
    }

    //Constructor for course wishlist
    public Course(int credits, String courseCode, String courseTitle, String sectionType,
                  String days, int crn, String instructor, String location, String time,
                  String dates, int capacity, int seatsAvailable, int seatsRemaining,
                  int waitlistCapacity, int waitlistAvailable, int waitlistRemaining,
                  Season mSeason) {
        this.credits = credits;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.sectionType = sectionType;
        this.days = days;
        this.crn = crn;
        this.instructor = instructor;
        this.location = location;
        this.time = time;
        this.dates = dates;
    }

    //Getter for credits
    public int getCredits(){
        return credits;
    }

    //Getter for course code
    public String getCourseCode(){
        return courseCode;
    }

    //Getter for course title
    public String getCourseTitle(){
        return courseTitle;
    }

    //Getter for user grade
    public String getUserGrade(){
        return userGrade;
    }

    //Getter for average grade
    public String getAverageGrade(){
        return averageGrade;
    }

    public int getCrn(){
        return crn;
    }

    /**
     * Get the days this course is on
     * @return The course days
     */
    public String getDays(){
        return days;
    }

    /**
     * Get the time this course is at
     * @return The course time
     */
    public String getTime(){
        return time;
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
        return this.crn == ((Course)course).crn;
    }

}
