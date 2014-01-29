package ca.mcgill.mymcgill.tests;

import ca.mcgill.mymcgill.activity.SplashActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Author: Joshua
 * Date: 29/01/14, 3:01 AM
 */
public class SplashActivityTest
		extends ActivityInstrumentationTestCase2<SplashActivity> {

    private SplashActivity splashActivityTest;
    
    public SplashActivityTest() {
        super(SplashActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        splashActivityTest = getActivity();
    }
    
    public void testPrecondition() {
    	
    }
}