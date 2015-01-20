package ca.appvelopers.mcgillmobile.fragment;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 4:42 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class MyCoursesFragment extends BaseFragment {
    protected static CookieManager cookieManager;
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = View.inflate(mActivity, R.layout.fragment_web, null);

        lockPortraitMode();

        //Title
        mActivity.setTitle(getString(R.string.title_mycourses));

        GoogleAnalytics.sendScreen(mActivity, "MyCourses");

        if(!Connection.isNetworkAvailable(mActivity)){
            DialogHelper.showNeutralAlertDialog(mActivity, getString(R.string.error),
                    getString(R.string.error_no_internet));
            return view;
        }

        CookieSyncManager.createInstance(mActivity);
        cookieManager = CookieManager.getInstance();
        if(cookieManager.hasCookies())
            cookieManager.removeAllCookie();

        //Get the WebView
        mWebView = (WebView)view.findViewById(R.id.desktop_webview);

        //allows download any file
        mWebView.setDownloadListener(new DownloadListener() {
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
                DownloadManager manager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        mWebView.getSettings().setSaveFormData(false);

        mWebView.loadUrl("https://mycourses2.mcgill.ca/Shibboleth.sso/Login?entityID=https://shibboleth.mcgill.ca/idp/shibboleth&target=https%3A%2F%2Fmycourses2.mcgill.ca%2Fd2l%2FshibbolethSSO%2Flogin.d2l");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function f(){" +
                        "(document.getElementsByName('j_username')[0]).value='" +
                        Load.loadFullUsername(mActivity) + "';" +
                        "(document.getElementsByName('j_password')[0]).value='" +
                        Load.loadPassword(mActivity) + "'; document.forms[0].submit();})()");

                view.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public static void deleteCookies(){
        if(cookieManager != null && cookieManager.hasCookies())
            cookieManager.removeAllCookie();
    }

    public WebView getWebView(){
        return mWebView;
    }
}