/*
 * Copyright 2014-2016 Julien Guerinet
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
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.guerinet.utils.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;

/**
 * Shows the desktop page of MyMcGill
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class DesktopActivity extends DrawerActivity {
    /**
     * The WebView
     */
    @BindView(R.id.web_view)
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

    @Override @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        analytics.sendScreen("Desktop Site");

        //If the user is not connected to the internet, don't continue
        if (!Utils.isConnected(this)) {
            DialogHelper.error(this, R.string.error_no_internet);
            return;
        }

        //Set up the WebView
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);

        mWebView.loadUrl("https://mymcgill.mcgill.ca/portal/page/portal/Login");
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:(function(){document.getElementById('username').value='" +
                        usernamePref.full() + "';document.getElementById('password').value='" +
                        passwordPref.get() + "'; document.LoginForm.submit(); })()");
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Check if we can go back in the WebView
        if(mWebView.canGoBack()){
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.DESKTOP;
    }
}