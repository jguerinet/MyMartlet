package ca.mcgill.mymcgill.object;

import junit.framework.TestCase;
import org.junit.Test;

import ca.mcgill.mymcgill.activity.DesktopActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by Ryan Singzon on 09/02/14.
 */
public class DesktopViewTest extends ActivityInstrumentationTestCase2<DesktopActivity> {

    private DesktopActivity mDesktopActivity;
    private String testString = "test";
    private String otherTestString = "test";

    public DesktopViewTest(){
        super(DesktopActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
     //  setActivityInitialTouchMode(false);
        mDesktopActivity = getActivity();
    }


    @Test
    public void testTrue() {
        assertEquals(testString, otherTestString);
    }

    /*
    @Test
    public void testViewDesktopSite(){
        DesktopActivity desktop = new DesktopActivity();
        assertEquals("Testing", true, true);

    }


    @Test
    public void testValidTextAppears(){

    }

    @Test
    public void testIncorrectLogin(){

    }
    */
}
