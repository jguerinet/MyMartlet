/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.ui.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.storage.Load;

/**
 * Displays the user's MyCourses page
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class MyCoursesFragment extends BaseFragment {
    /**
     * Code used to get the external storage permission for downloads
     */
    private static final int EXTERNAL_STORAGE_PERMISSION = 100;
    /**
     * The main view
     */
    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        lockPortraitMode();
        Analytics.getInstance().sendScreen("MyCourses");
        mActivity.setTitle(R.string.title_mycourses);

        if(!Help.isConnected()){
            DialogHelper.showNeutralDialog(mActivity, getString(R.string.error),
                    getString(R.string.error_no_internet));
            return view;
        }

        //Clear any existing cookies
        final CookieManager cookieManager = CookieManager.getInstance();
        if(cookieManager.hasCookies()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                CookieManager.getInstance().removeAllCookies(null);
            }
            else{
                //noinspection deprecation
                CookieManager.getInstance().removeAllCookie();
            }
        }

        //Get the WebView
        mWebView = (WebView)view.findViewById(R.id.webview);

        //allows download any file
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                //Check that we have the external storage permission
                if(!Help.checkPermission(MyCoursesFragment.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, EXTERNAL_STORAGE_PERMISSION)) {
                    return;
                }

                String[] urlSplit = url.split("/");
                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                String fileName = urlSplit[urlSplit.length - 1].concat("" + extension);
                Uri source = Uri.parse(url);
                // Make a new request pointing to the url
                DownloadManager.Request request = new DownloadManager.Request(source);
                // Appears the same in Notification bar while downloading
                String cookie = cookieManager.getCookie(url);
                request.addRequestHeader("Cookie", cookie);
                request.setTitle(fileName);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                // save the file in the "Downloads" folder of SDCARD
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        fileName);
                // get download service and enqueue file
                DownloadManager manager =
                        (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; " +
                "LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) " +
                "Version/4.0 Mobile Safari/534.30");
        mWebView.getSettings().setSaveFormData(false);
        mWebView.loadUrl("https://mycourses2.mcgill.ca/Shibboleth.sso/Login?entityID=" +
                "https://shibboleth.mcgill.ca/idp/shibboleth&target=https%3A%2F%2Fmycourses2" +
                ".mcgill.ca%2Fd2l%2FshibbolethSSO%2Flogin.d2l");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //Preload the user's information
                view.loadUrl("javascript:(function f(){" +
                        "(document.getElementsByName('j_username')[0]).value='" +
                        Load.fullUsername() + "';" +
                        "(document.getElementsByName('j_password')[0]).value='" +
                        Load.password() + "'; document.forms[0].submit();})()");

                view.setVisibility(View.VISIBLE);

                //Hide the loading indicator
                hideLoadingIndicator();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION: {
                int stringId;
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stringId = R.string.storage_permission_granted;
                }
                else {
                    stringId = R.string.storage_permission_refused;
                }
                Toast.makeText(mActivity, stringId, Toast.LENGTH_SHORT).show();
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * @return The MyCourses WebView
     */
    public WebView getWebView(){
        return mWebView;
    }
}