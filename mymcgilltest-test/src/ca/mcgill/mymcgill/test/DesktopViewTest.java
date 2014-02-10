package ca.mcgill.mymcgill.test;

import org.junit.Test;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.DesktopActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Ryan Singzon on 09/02/14.
 */
public class DesktopViewTest extends ActivityInstrumentationTestCase2<DesktopActivity> {

    private DesktopActivity mDesktopActivity;
    private String testString = "test";

    public DesktopViewTest(){
        super(DesktopActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        setActivityInitialTouchMode(false);
        mDesktopActivity = getActivity();
    }


    @Test
    public void testTrue() {
        assertNotNull(testString);
    }

    
    @Test
    public void testWebViewExists(){
        WebView webview = (WebView) mDesktopActivity.findViewById(R.id.desktop_webview);
        assertNotNull("Webview does not exist", webview);
    }


    @Test
    public void testValidTextAppears(){
    	
    }

 /*
    @Test
    public void testIncorrectLogin(){

    }
    */
}
