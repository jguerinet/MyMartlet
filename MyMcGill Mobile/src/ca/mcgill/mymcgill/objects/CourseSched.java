package ca.mcgill.mymcgill.objects;

/**
 * CourseSched
 * @author Quang
 * 
 */
public class CourseSched {
	private int crn;
	private String courseCode; //format: ECSE 428-001 (department coursenumber-section)
	private int startH, startM, endH, endM;
	private String room;
    private Day day;
	
	public CourseSched(String a) {
		//TODO: parse algorithm
	}
	
	public CourseSched(int crn, String courseCode, int day, int startHour, int startMinute, int endHour, int endMinute, String room) {
		this.crn = crn;
		this.courseCode = courseCode;
        switch (day) {
        	case 0: 
        		this.day = Day.MONDAY;
        		break;
        	case 1: 
        		this.day = Day.TUESDAY;
        		break;
        	case 2: 
        		this.day = Day.WEDNESDAY;
        		break;
        	case 3: 
        		this.day = Day.THURSDAY;
        		break;
        	case 4: 
        		this.day = Day.FRIDAY;
        		break;
        	case 5: 
        		this.day = Day.SATURDAY;
        		break;
        	case 6: 
        		this.day = Day.SUNDAY;
        		break;
        }
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
