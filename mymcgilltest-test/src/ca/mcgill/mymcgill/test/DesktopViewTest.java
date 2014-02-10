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
    private WebView webView;
    
    public DesktopViewTest(){
        super(DesktopActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception{
        super.setUp();
        setActivityInitialTouchMode(false);
        mDesktopActivity = getActivity();
        webView = (WebView) mDesktopActivity.findViewById(R.id.desktop_webview);
    }
    
    @Test
    public void testWebViewExists(){
        assertNotNull("Webview does not exist", webView);
    }
}
