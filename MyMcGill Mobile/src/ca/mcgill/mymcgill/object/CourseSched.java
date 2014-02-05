package ca.mcgill.mymcgill.object;

import java.io.Serializable;

/**
 * CourseSched
 * @author Quang
 * 
 */
public class CourseSched implements Serializable{
	private int crn;
	private String courseCode; //format: ECSE 428-001 (department coursenumber-section)
	private int startH, startM, endH, endM;
	private String room;
    private Day day;
	
	public CourseSched(String a) {
		//TODO: parse algorithm
	}
	
	public CourseSched(int crn, String courseCode, char day, int startHour, int startMinute, int endHour, int endMinute, String room) {
		this.crn = crn;
		this.courseCode = courseCode;
        this.day = Day.getDay(day);
		this.startH = startHour;
        //Remove 5 minutes to the start to get round numbers
		this.startM = (startMinute - 5) % 60;
		this.endH = endHour;
        //Add 5 minutes to the end to get round numbers
		this.endM = (endMinute + 5) % 60;
        //Make sure it didn't loop around. If so, we need to increment the hour
        if(this.endM == 0){
            this.endH ++;
        }
		this.room = room;
	}
	
	public int getCRN(){
		return crn;
	}
	public String getCourseCode() {
		return courseCode;
	}
	public Day getDay(){
        return day;
    }
    public int getStartHour() {
		return startH;
	}
	public int getStartMinute() {
		return startM;
	}
	public int getEndHour() {
		return endH;
	}
	public int getEndMinute() {
		return endM;
	}
    public int getStartTimeInMinutes(){
        return 60*startH + startM;
    }
    public int getEndTimeInMinutes(){
        return 60*endH + endM;
    }
	public String getRoom()
    {
		return room;
	}

}
