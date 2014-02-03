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
	
	public CourseSched(int crn, String courseCode, char day, int startHour, int startMinute, int endHour, int endMinute, String room) {
		this.crn = crn;
		this.courseCode = courseCode;
        switch (day) {
        	case 'M': 
        		this.day = Day.MONDAY;
        		break;
        	case 'T': 
        		this.day = Day.TUESDAY;
        		break;
        	case 'W': 
        		this.day = Day.WEDNESDAY;
        		break;
        	case 'R': 
        		this.day = Day.THURSDAY;
        		break;
        	case 'F': 
        		this.day = Day.FRIDAY;
        		break;
        	case 'S': 
        		this.day = Day.SATURDAY;
        		break;
        	case 'N': 
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
