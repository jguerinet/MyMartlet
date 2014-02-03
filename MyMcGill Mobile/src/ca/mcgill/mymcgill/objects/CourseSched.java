package ca.mcgill.mymcgill.objects;

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
		this.startM = startMinute;
		this.endH = endHour;
		this.endM = endMinute;
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
	public String getRoom() {
		return room;
	}

}
