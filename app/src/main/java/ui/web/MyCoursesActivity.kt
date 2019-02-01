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

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.webkit.*
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.suitcase.util.Utils
import com.guerinet.suitcase.util.extensions.hasPermission
import com.guerinet.suitcase.util.extensions.isConnected
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_web.*
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject

/**
 * Displays the user's MyCourses page
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 1.0.0
 */
class MyCoursesActivity : DrawerActivity() {

    private val usernamePref by inject<UsernamePref>()

    override val currentPage = HomepageManager.HomePage.MY_COURSES

    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        // Check internet first
        if (!isConnected) {
            errorDialog(R.string.error_no_internet)
            return
        }

        // TODO Why ?
        // Clear any existing cookies
        val cookieManager = CookieManager.getInstance()
        if (cookieManager.hasCookies()) {
            CookieManager.getInstance().removeAllCookies(null)
        }

        // Set up any eventual downloads
        webView.setDownloadListener(DownloadListener { url, _, _, mimeType, _ ->
            // Check that we have the external storage permission
            if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            EXTERNAL_STORAGE_PERMISSION)) {
                return@DownloadListener
            }

            // Set up the file name
            val urlSplit = url.split("/")
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            val fileName = urlSplit[urlSplit.size - 1] + (extension)

            // Make a new request pointing to the url
            val request = DownloadManager.Request(url.toUri())

            // Show a notification while downloading
            val cookie = cookieManager.getCookie(url)
            request.apply {
                addRequestHeader("Cookie", cookie)
                setTitle(fileName)
                allowScanningByMediaScanner()
                setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

                // Save the file in the "Downloads" folder of SDCARD
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }

            // Get the download manager and enqueue download
            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        })

        // Load the info into the WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.userAgentString = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; " +
                "LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) " +
                "Version/4.0 Mobile Safari/534.30"
        @Suppress("DEPRECATION")
        webView.settings.saveFormData = false
        webView.loadUrl("https://mycourses2.mcgill.ca/Shibboleth.sso/Login?entityID=" +
                "https://shibboleth.mcgill.ca/idp/shibboleth&target=https%3A%2F%2Fmycourses2" +
                ".mcgill.ca%2Fd2l%2FshibbolethSSO%2Flogin.d2l")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Preload the user's information
                view.loadUrl("javascript:(function f(){" +
                        "(document.getElementsByName('j_username')[0]).value='" +
                        usernamePref.full + "';" +
                        "(document.getElementsByName('j_password')[0]).value='" +
                        Hawk.get<String>(Prefs.PASSWORD) +
                        "'; document.getElementsByName('_eventId_proceed')[0].click();})()")
                view.isVisible = true
            }
        }
    }

    override fun onBackPressed() {
        // Check if we can go back on the page itself
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        when (requestCode) {
            EXTERNAL_STORAGE_PERMISSION -> {
                val stringId = if (Utils.isPermissionGranted(grantResults)) {
                    R.string.storage_permission_granted
                } else {
                    R.string.storage_permission_refused
                }
                toast(stringId)
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {

        /**
         * Code used to get the external storage permission for downloads
         */
        private const val EXTERNAL_STORAGE_PERMISSION = 100
    }
}