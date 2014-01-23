package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import ca.mcgill.mymcgill.R;

/**
 * Author: Julien
 * Date: 22/01/14, 9:05 PM
 */
public class DesktopActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);

        //Get the Webview
        WebView webView = (WebView)findViewById(R.id.desktop_webview);

        //TODO Load the right URL here
        webView.loadUrl("https://mcgill.ca");
    }
}