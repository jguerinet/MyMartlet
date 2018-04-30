/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.ui.web;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.ui.DrawerActivity;
import com.guerinet.mymartlet.ui.dialog.DialogHelper;
import com.guerinet.mymartlet.util.dagger.prefs.PrefsModuleKt;
import com.guerinet.mymartlet.util.dagger.prefs.UsernamePref;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.suitcase.util.Utils;
import com.orhanobut.hawk.Hawk;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Shows the desktop page of MyMcGill
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class DesktopActivity extends DrawerActivity {

    @BindView(R.id.web_view)
    WebView webView;

    @Inject
    UsernamePref usernamePref;

    @Override @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        App.Companion.component(this).inject(this);
        getGa().sendScreen("Desktop Site");

        // If the user is not connected to the internet, don't continue
        if (!Utils.isConnected(this)) {
            DialogHelper.error(this, R.string.error_no_internet);
            return;
        }

        // Set up the WebView
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.loadUrl("https://mymcgill.mcgill.ca/portal/page/portal/");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.toLowerCase().contains("login")) {
                    // Only log them in if they're not already
                    view.loadUrl("javascript:(function(){document.getElementById('username')" +
                            ".value='" + usernamePref.full() +
                            "';document.getElementById('password').value='" +
                            Hawk.get(PrefsModuleKt.PASSWORD) + "'; " +
                            "document.getElementsByClassName('mainSubmit').submit.click(); })()");
                }
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Check if we can go back in the WebView
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    @HomepageManager.Homepage
    protected int getCurrentPage() {
        return HomepageManager.Companion.getDESKTOP();
    }
}