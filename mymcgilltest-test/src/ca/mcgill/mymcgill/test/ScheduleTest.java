package ca.mcgill.mymcgill.test;

import java.util.List;

import org.junit.Test;

import android.test.ActivityInstrumentationTestCase2;
import ca.mcgill.mymcgill.activity.ScheduleActivity;
import ca.mcgill.mymcgill.activity.LoginActivity;
//import widget.
import android.widget.TextView;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.Day;

public class ScheduleTest extends ActivityInstrumentationTestCase2<ScheduleActivity> {
	public ScheduleActivity schedActivityClass;
	public TextView usernameView;
	public TextView passwordView;
		
	@SuppressWarnings("deprecation")
	public ScheduleTest() {
		super("ca.mcgill.mymcgill.activity", ScheduleActivity.class);
	}
		
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		schedActivityClass = this.getActivity();
		
		String username = "";
		String password = "";
		
		usernameView.setText(username);
		passwordView.setText(password);
	}
		
	@Test
	public void testGetCoursesForDay(){
		List<CourseSched> courses = schedActivityClass.getCoursesForDay(Day.TUESDAY);
		CourseSched course = new CourseSched(738, "COMP 421", "001", 'T', 11, 30, 13, 0, "Rutherford Physics Building 112", "Bettina Kemme", "Database Systems", "3.000", "Lecture");
		System.out.println(courses.get(0));
		System.out.println(course);
		assertEquals(courses.get(0), course);
		assertTrue(true);
	}
		
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
