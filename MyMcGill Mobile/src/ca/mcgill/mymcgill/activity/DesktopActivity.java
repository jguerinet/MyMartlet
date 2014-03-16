package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.util.Load;

/**
 * Author: Julien
 * Date: 22/01/14, 9:05 PM
 */
public class DesktopActivity extends DrawerActivity{
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_desktop);
        super.onCreate(savedInstanceState);

        //Get the Webview
        final WebView webView = (WebView)findViewById(R.id.desktop_webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("https://mymcgill.mcgill.ca/portal/page/portal/Login");
        webView.setWebViewClient(new WebViewClient() {
        	
        	public void onPageStarted(WebView view, String url, Bitmap favicon){
        		//view.loadUrl("javascript:(function(){document.write( '<style class=\"hideStuff\" type=\"text/css\">body {display:none;}</style>')});");
        		view.setVisibility(View.INVISIBLE);
        	}
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function(){document.getElementById('username').value='" +
                        Load.loadFullUsername(DesktopActivity.this) + "';" +
                        "document.getElementById('password').value='" +
                        Load.loadPassword(DesktopActivity.this) + "'; document.LoginForm.submit(); })()");
                view.setVisibility(View.VISIBLE);
            }
        });

    }
}