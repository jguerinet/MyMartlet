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
	
		
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
