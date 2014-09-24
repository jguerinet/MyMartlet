package ca.appvelopers.mcgillmobile.activity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.view.DialogHelper;


public class MyCoursesActivity extends DrawerActivity{

    protected Context mContext = this;
    protected static CookieManager cookieManager;
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

        CookieSyncManager.createInstance(mContext);
        cookieManager = CookieManager.getInstance();
        if(cookieManager.hasCookies())
            cookieManager.removeAllCookie();

        //Get the Webview
        final WebView webView = (WebView)findViewById(R.id.desktop_webview);

        //allows download any file
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                String[] urlSplit = url.split("/");
                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                String fileName = urlSplit[urlSplit.length - 1].concat("." + extension);
                Uri source = Uri.parse(url);
                // Make a new request pointing to the url
                DownloadManager.Request request = new DownloadManager.Request(source);
                // appears the same in Notification bar while downloading
                String cookie = cookieManager.getCookie(url);
                request.addRequestHeader("Cookie", cookie);
                request.setDescription("Description for the DownloadManager Bar");
                request.setTitle(fileName);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                // save the file in the "Downloads" folder of SDCARD
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                // get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        webView.getSettings().setSaveFormData(false);

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


    public static void deleteCookies(){

        if(cookieManager != null && cookieManager.hasCookies())
            cookieManager.removeAllCookie();
    }
}
