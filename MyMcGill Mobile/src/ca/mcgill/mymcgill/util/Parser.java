package ca.mcgill.mymcgill.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.object.Day;
import ca.mcgill.mymcgill.object.Season;

/**
 * Author : Julien
 * Date :  2014-05-31 2:35 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class Parser {
    /**
     * Parses an HTML String to generate a list of courses
     * @param courseHTML The HTML String to parse
     * @return The list of courses
     */
    public static List<Course> parseCourseList(Season season, int year, String courseHTML){
        List<Course> courses = new ArrayList<Course>();

        Document doc = Jsoup.parse(courseHTML);
        Elements scheduleTable = doc.getElementsByClass("datadisplaytable");
        if (scheduleTable.isEmpty()) {
            return courses;
        }
        for (int i = 0; i < scheduleTable.size(); i+=2) {
            Element row;
            Element currentElement = scheduleTable.get(i);

            //Course name, code, and section
            row = currentElement.getElementsByTag("caption").first();
            String[] texts = row.text().split(" - ");
            String courseTitle = texts[0].substring(0, texts[0].length() - 1);
            String courseCode = texts[1];
            String section = texts[2];

            //CRN
            row = currentElement.getElementsByTag("tr").get(1);
            String crnString = row.getElementsByTag("td").first().text();
            int crn = Integer.parseInt(crnString);

            //Credits
            row = currentElement.getElementsByTag("tr").get(5);
            String creditString = row.getElementsByTag("td").first().text();
            int credits = Integer.parseInt(creditString) ;

            //Check if there is any data to parse
            if (i+1 < scheduleTable.size() && scheduleTable.get(i+1).attr("summary").equals("This table lists the scheduled meeting times and assigned instructors for this class..")) {
                //Time, Days, Location, Section Type, Instructor
                row = currentElement.getElementsByTag("tr").get(1);
                Elements cells = row.getElementsByTag("td");
                String[] times = cells.get(0).text().split(" - ");
                char[] dayCharacters = cells.get(i).text().toCharArray();
                String location = cells.get(2).text();
                String sectionType = cells.get(4).text();
                String instructor = cells.get(5).text();

                //Time parsing
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
                //Try/Catch for searchedCourses with no assigned times
                catch (NumberFormatException e) {
                    startHour = 0;
                    startMinute = 0;
                    endHour = 0;
                    endMinute = 0;
                }

                //Day Parsing
                List<Day> days = new ArrayList<Day>();
                for(char dayCharacter : dayCharacters){
                    days.add(Day.getDay(dayCharacter));
                }

                courses.add(new Course(season, year, crn, courseCode, courseTitle, section, startHour,
                        startMinute, endHour, endMinute, days, sectionType, location, instructor, credits));
            }
            //If there is no data to parse, reset i and continue
            else {
                i = i - 1;
            }
        }

        return courses;
    }
}
