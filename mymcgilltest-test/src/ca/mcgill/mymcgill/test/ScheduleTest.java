package ca.mcgill.mymcgill.test;

import java.util.List;

import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.Day;

public class ScheduleTest extends ActivityInstrumentationTestCase2<ScheduleActivity> {
	public ScheduleActivity schedActivityClass;
	public CourseSched course;
	public List<CourseSched> courses;
		
	@SuppressWarnings("deprecation")
	public ScheduleTest() {
		super("ca.mcgill.mymcgill.activity", ScheduleActivity.class);
	}
		
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		schedActivityClass = this.getActivity();
	}
		
	@Test
	public void testGetCoursesForDay(){
		courses = schedActivityClass.getCoursesForDay(Day.TUESDAY);
		assertNotNull(courses);
	}
	
	@Test
	public void testCourseSched() throws Exception {
		course = new CourseSched(738, "COMP 421", "001", 'T', 11, 30, 13, 0, "Rutherford Physics Building 112", "Bettina Kemme", "Database Systems", "3.000", "Lecture");
		assertNotNull(course);
	}
		
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
