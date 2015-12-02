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

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.storage.Load;

/**
 * Shows the desktop page of MyMcGill
 * @author Julien Guerinet
 * @version 2.0.1
 * @since 2.0
 */
public class DesktopFragment extends BaseFragment {
    /**
     * The WebView
     */
    private WebView mWebView;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        lockPortraitMode();
        Analytics.getInstance().sendScreen("Desktop Site");
        mActivity.setTitle(R.string.title_desktop);

        if(!Help.isConnected()){
            DialogHelper.showNeutralDialog(mActivity, getString(R.string.error),
                    getString(R.string.error_no_internet));
            return view;
        }

        //Get the WebView
        mWebView = (WebView)view.findViewById(R.id.webview);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.loadUrl("https://mymcgill.mcgill.ca/portal/page/portal/Login");
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                //view.loadUrl("javascript:(function(){document.write( '<style
                // class=\"hideStuff\" type=\"text/css\">body {display:none;}</style>')});");
                view.setVisibility(View.INVISIBLE);
            }

            public void onPageFinished(WebView view, String url){
                view.loadUrl("javascript:(function(){document.getElementById('username').value='" +
                        Load.fullUsername() + "';" +
                        "document.getElementById('password').value='" +
                        Load.password() + "'; document.LoginForm.submit(); })()");
                view.setVisibility(View.VISIBLE);

                //Hide the loading indicator
                hideLoadingIndicator();
            }
        });

        return view;
    }

    /**
     * @return The Desktop WebView
     */
    public WebView getWebView(){
        return mWebView;
    }
}