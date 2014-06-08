package ca.appvelopers.mcgillmobile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.HomePage;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.DialogHelper;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Load;

public class MyCoursesActivity extends DrawerActivity{
    private boolean mDoubleBackToExit;
	
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_desktop);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "MyCourses");

        if(!Connection.isNetworkAvailable(this)){
            DialogHelper.showNeutralAlertDialog(this, this.getResources().getString(R.string.error),
                    this.getResources().getString(R.string.error_no_internet));
            return;
        }

        //Get the Webview
        final WebView webView = (WebView)findViewById(R.id.desktop_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

        webView.loadUrl("https://mycourses2.mcgill.ca/Shibboleth.sso/Login?entityID=https://shibboleth.mcgill.ca/idp/shibboleth&target=https%3A%2F%2Fmycourses2.mcgill.ca%2Fd2l%2FshibbolethSSO%2Flogin.d2l");
        webView.setWebViewClient(new WebViewClient() {
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

    @Override
    public void onBackPressed(){
        if(App.getHomePage() != HomePage.MY_COURSES){
            startActivity(new Intent(MyCoursesActivity.this, App.getHomePage().getHomePageClass()));
            super.onBackPressed();
        }
        else{
            if (mDoubleBackToExit) {
                super.onBackPressed();
                return;
            }
            this.mDoubleBackToExit = true;
            Toast.makeText(this, R.string.back_toaster_message, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDoubleBackToExit=false;
                }
            }, 2000);
        }
    }
}
