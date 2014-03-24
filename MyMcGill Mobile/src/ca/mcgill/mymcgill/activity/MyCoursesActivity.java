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

public class MyCoursesActivity extends DrawerActivity{
	
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_desktop);
        super.onCreate(savedInstanceState);
        
        //Get the Webview
        final WebView webView = (WebView)findViewById(R.id.desktop_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

        webView.loadUrl("https://mycourses2.mcgill.ca/Shibboleth.sso/Login?entityID=https://shibboleth.mcgill.ca/idp/shibboleth&target=https%3A%2F%2Fmycourses2.mcgill.ca%2Fd2l%2FshibbolethSSO%2Flogin.d2l");
        webView.setWebViewClient(new WebViewClient() {
        	
        	@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon){
        		view.setVisibility(View.INVISIBLE);
        	}
        	
            @Override
			public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function f(){" +
                    "(document.getElementsByName('j_username')[0]).value='" +
                    Load.loadFullUsername(MyCoursesActivity.this) + "';" +
                    "(document.getElementsByName('j_password')[0]).value='" +
                    Load.loadPassword(MyCoursesActivity.this) + "'; document.forms[0].submit();})()");
                
                view.setVisibility(View.VISIBLE);
            }
        });
    }
}
