/*
 * Copyright 2014-2019 Julien Guerinet
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

package com.guerinet.mymartlet.ui.web

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.suitcase.util.extensions.isConnected
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_web.*
import org.koin.android.ext.android.inject

/**
 * Shows the desktop page of MyMcGill
 * @author Julien Guerinet
 * @since 1.0.0
 */
class DesktopActivity : DrawerActivity() {

    private val usernamePref by inject<UsernamePref>()

    override val currentPage = HomepageManager.HomePage.DESKTOP

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        // If the user is not connected to the internet, don't continue
        if (!isConnected) {
            errorDialog(R.string.error_no_internet)
            return
        }

        // Set up the WebView
        webView.apply {
            settings.apply {
                useWideViewPort = true
                javaScriptEnabled = true
                builtInZoomControls = true
                displayZoomControls = false
            }

            loadUrl("https://mymcgill.mcgill.ca/portal/page/portal/")
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    if (url.toLowerCase().contains("login")) {
                        // Only log them in if they're not already
                        view.loadUrl("javascript:(function(){document.getElementById('username')" +
                                ".value=${usernamePref.full}';document.getElementById('password')" +
                                ".value='${Hawk.get<String>(Prefs.PASSWORD)}'; " + "document." +
                                "getElementsByClassName('mainSubmit').submit.click(); })()")
                    }
                    view.isVisible = true
                }
            }
        }
    }

    override fun onBackPressed() {
        // Check if we can go back in the WebView
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }
}