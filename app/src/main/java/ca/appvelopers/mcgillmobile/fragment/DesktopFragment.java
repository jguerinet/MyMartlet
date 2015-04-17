package ca.appvelopers.mcgillmobile.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 5:34 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class DesktopFragment extends BaseFragment {
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
        mActivity.setTitle(getString(R.string.title_desktop));

        Analytics.getInstance().sendScreen("Desktop Site");

        if(!Connection.isNetworkAvailable(mActivity)){
            DialogHelper.showNeutralAlertDialog(mActivity, getString(R.string.error),
                    getString(R.string.error_no_internet));
            return view;
        }

        //Get the WebView
        mWebView = (WebView)view.findViewById(R.id.desktop_webview);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.loadUrl("https://mymcgill.mcgill.ca/portal/page/portal/Login");
        mWebView.setWebViewClient(new WebViewClient() {

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //view.loadUrl("javascript:(function(){document.write( '<style class=\"hideStuff\" type=\"text/css\">body {display:none;}</style>')});");
                view.setVisibility(View.INVISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function(){document.getElementById('username').value='" +
                        Load.loadFullUsername(mActivity) + "';" +
                        "document.getElementById('password').value='" +
                        Load.loadPassword(mActivity) + "'; document.LoginForm.submit(); })()");
                view.setVisibility(View.VISIBLE);

                //Hide the loading indicator
                hideLoadingIndicator();
            }
        });

        return view;
    }
}