package ca.mcgill.mymcgill.tests;

import ca.mcgill.mymcgill.activity.DesktopActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Author: Joshua
 * Date: 29/01/14, 3:01 AM
 */
public class DesktopActivityTest
		extends ActivityInstrumentationTestCase2<DesktopActivity> {

    private DesktopActivity desktopActivityTest;
    
    public DesktopActivityTest() {
        super(DesktopActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        desktopActivityTest = getActivity();
    }
    
    public void testPrecondition() {
    	
    }
}