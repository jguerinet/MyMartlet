package ca.mcgill.mymcgill.tests;

import ca.mcgill.mymcgill.activity.ScheduleActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Author: Joshua
 * Date: 29/01/14, 3:01 AM
 */
public class ScheduleActivityTest
		extends ActivityInstrumentationTestCase2<ScheduleActivity> {

    private ScheduleActivity scheduleActivityTest;
    
    public ScheduleActivityTest() {
        super(ScheduleActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        scheduleActivityTest = getActivity();
    }
    
    public void testPrecondition() {
    	
    }
}