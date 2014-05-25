package ca.mcgill.mymcgill.object;

import java.io.Serializable;

/**
 * Created by Ryan Singzon on 30/01/14.
 *
 * This class will contain information pertaining to individual McGill courses that students
 * have taken, such as the grade, credit, and class average
 */
public class Course implements Serializable{

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
    public Course(int credits, String courseCode, String courseTitle, String sectionType, String days, int crn, String instructor, String location, String time, String dates) {
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

}
