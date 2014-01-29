package ca.mcgill.mymcgill.tests;

import ca.mcgill.mymcgill.activity.LoginActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Author: Joshua
 * Date: 29/01/14, 3:01 AM
 */
public class LoginActivityTest
		extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity loginActivityTest;
    
    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loginActivityTest = getActivity();
    }
    
    public void testPrecondition() {
    	
    }
}