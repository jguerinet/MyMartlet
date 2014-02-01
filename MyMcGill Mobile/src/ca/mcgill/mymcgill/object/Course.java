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

    //Constructor for the Course object
    public Course(int credits, String courseCode, String courseTitle,
                    String userGrade, String averageGrade){
        this.credits = credits;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.userGrade = userGrade;
        this.averageGrade = averageGrade;
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
