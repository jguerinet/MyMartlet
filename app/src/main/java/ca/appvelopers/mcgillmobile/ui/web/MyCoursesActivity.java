/*
 * Copyright 2014-2016 Appvelopers
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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.guerinet.utils.Device;
import com.guerinet.utils.Utils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;

/**
 * Displays the user's MyCourses page
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class MyCoursesActivity extends DrawerActivity {
    /**
     * Code used to get the external storage permission for downloads
     */
    private static final int EXTERNAL_STORAGE_PERMISSION = 100;
    /**
     * Main content
     */
    @Bind(R.id.web_view)
    protected WebView mWebView;
    /**
     * {@link UsernamePreference} instance
     */
    @Inject
    protected UsernamePreference usernamePref;
    /**
     * {@link PasswordPreference} instance
     */
    @Inject
    protected PasswordPreference passwordPref;
    /**
     * The {@link ConnectivityManager} instance
     */
    @Inject
    protected ConnectivityManager connectivityManager;

    @Override @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        analytics.sendScreen("MyCourses");

        //No internet: not worth trying to load the view
        if (!Utils.isConnected(connectivityManager)) {
            DialogHelper.error(this, R.string.error_no_internet);
            return;
        }

        //TODO Why ?
        //Clear any existing cookies
        final CookieManager cookieManager = CookieManager.getInstance();
        if (cookieManager.hasCookies()) {
            if (Device.isLollipop()) {
                CookieManager.getInstance().removeAllCookies(null);
            } else{
                //noinspection deprecation
                CookieManager.getInstance().removeAllCookie();
            }
        }

        //Set up any eventual downloads
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                //Check that we have the external storage permission
                if (!Utils.requestPermission(MyCoursesActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, EXTERNAL_STORAGE_PERMISSION)) {
                    return;
                }

                //Set up the file name
                String[] urlSplit = url.split("/");
                String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                String fileName = urlSplit[urlSplit.length - 1].concat("" + extension);

                //Make a new request pointing to the url
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                //Show a notification while downloading
                String cookie = cookieManager.getCookie(url);
                request.addRequestHeader("Cookie", cookie);
                request.setTitle(fileName);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                //Save the file in the "Downloads" folder of SDCARD
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, fileName);

                //Get the download manager and enqueue download
                DownloadManager manager =
                        (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
        });

        //Load the info into the WebView
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
                        usernamePref.full() + "';" +
                        "(document.getElementsByName('j_password')[0]).value='" +
                        passwordPref.get() + "'; document.forms[0].submit();})()");

                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Check if we can go back on the page itself
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION:
                int stringId;
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stringId = R.string.storage_permission_granted;
                }
                else {
                    stringId = R.string.storage_permission_refused;
                }
                Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.MY_COURSES;
    }
}