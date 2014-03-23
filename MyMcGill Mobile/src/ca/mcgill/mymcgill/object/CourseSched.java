package ca.mcgill.mymcgill.object;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseSched
 * @author Quang
 * 
 */
public class CourseSched implements Serializable{
	private int crn;
	private String courseCode, section;
	private int startH, startM, endH, endM;
	private String room;
    private Day day;
    private String profName;
    private String courseName;
    private String credits;
    private String scheduleType;
	
	private CourseSched(int crn, String courseCode, String section, char day, int startHour, int startMinute, int endHour, int endMinute, String room, String professorName, String courseName, String credits, String scheduleType) {
		this.crn = crn;
		this.courseCode = courseCode;
        this.section = section;
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
		this.profName = professorName;
		this.courseName = courseName;
		this.credits = credits;
		this.scheduleType = scheduleType;
	}
	
	public int getCRN(){
		return crn;
	}
	public String getCourseCode() {
		return courseCode;
	}
    public String getSection(){
        return this.section;
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
	public String getProfessorName() {
		return profName;
	}
	public String getCredits() {
		return credits;
	}
	public String getCourseName() {
		return courseName;
	}
	public String getScheduleType() {
		return scheduleType;
	}

    public static List<CourseSched> parseCourseList(String scheduleString){
        List<CourseSched> schedule = new ArrayList<CourseSched>();

        //Parsing code
        Document doc = Jsoup.parse(scheduleString);
        Elements scheduleTable = doc.getElementsByClass("datadisplaytable");

        String name, data, credits;
        int crn;
        for (int i = 0; i < scheduleTable.size(); i+=2) {
            name = getCourseCodeAndName(scheduleTable.get(i));
            crn = getCRN(scheduleTable.get(i));
            data = getSchedule(scheduleTable.get(i+1));
            credits = getCredit(scheduleTable.get(i));
            schedule.addAll(addCourseSched(name, crn, credits, data));
        }

        return schedule;
    }

    private static String getCourseCodeAndName(Element dataDisplayTable) {
        Element caption = dataDisplayTable.getElementsByTag("caption").first();
        String[] texts = caption.text().split(" - ");
        return (texts[0].substring(0, texts[0].length() - 1) + "," + texts[1] + "," + texts[2]);
    }

    private static int getCRN(Element dataDisplayTable) {
        Element row = dataDisplayTable.getElementsByTag("tr").get(1);
        String crn = row.getElementsByTag("td").first().text();
        return Integer.parseInt(crn);
    }
    private static String getCredit(Element dataDisplayTable) {
        Element row = dataDisplayTable.getElementsByTag("tr").get(5);
        String credit = row.getElementsByTag("td").first().text();
        return credit;
    }

    //return time, day, room, scheduleType, professor
    private static String getSchedule(Element dataDisplayTable) {
        Element row = dataDisplayTable.getElementsByTag("tr").get(1);
        Elements cells = row.getElementsByTag("td");
        return (cells.get(0).text() + "," + cells.get(1).text() + "," + cells.get(2).text() + "," + cells.get(4).text() + "," + cells.get(5).text());
    }


    private static List<CourseSched> addCourseSched(String course, int crn, String credit, String data) {
        String[] dataItems = data.split(",");
        String[] times = dataItems[0].split(" - ");
        char[] days = dataItems[1].toCharArray();
        String room = dataItems[2];
        String courseName = course.split(",")[0];
        String courseCode = course.split(",")[1];
        String section = course.split(",")[2];
        String profName = dataItems[4];
        String scheduleType = dataItems[3];

        int startHour, startMinute, endHour, endMinute;
        try {
            startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
            startMinute = Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
            endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
            endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
            String startPM = times[0].split(" ")[1];
            String endPM = times[1].split(" ")[1];

            //If it's PM, then add 12 hours to the hours for 24 hours format
            //Make sure it isn't noon
            if (startPM.equals("PM") && startHour != 12) {
                startHour += 12;
            }
            if (endPM.equals("PM") && endHour != 12) {
                endHour += 12;
            }
        }
        //Try/Catch for courses with no assigned times
        catch (NumberFormatException e) {
            startHour = 0;
            startMinute = 0;
            endHour = 0;
            endMinute = 0;
        }

        List<CourseSched> courseList = new ArrayList<CourseSched>();

        for (char day : days) {
            courseList.add(new CourseSched(crn, courseCode, section, day, startHour, startMinute, endHour, endMinute, room, profName, courseName, credit, scheduleType));
        }

        return courseList;
    }
}
