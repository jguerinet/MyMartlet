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

package com.guerinet.mymartlet.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import com.afollestad.materialdialogs.DialogAction
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.android.material.navigation.NavigationView
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.suitcase.dialog.alertDialog
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import kotlinx.android.synthetic.main.activity_ebill.*
import kotlinx.android.synthetic.main.drawer.*
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL

/**
 * Base class for all of the activities with the main navigation drawer
 * @author Julien Guerinet
 * @since 1.0.0
 */
abstract class DrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected val homePageManager by inject<HomepageManager>()

    private val drawerToggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
    }

    private val drawerLayout by lazy { find<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawerLayout) }

    /**
     * Callback manager used for Facebook
     */
    private val facebookCallbackManager: CallbackManager = CallbackManager.Factory.create()

    /**
     * Page in the drawer that this activity represents
     */
    protected abstract val currentPage: HomepageManager.HomePage

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Set up the toolbar
        setUpToolbar(false)

        // Set up the drawer
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerLayout.isFocusableInTouchMode = false
        drawer.setNavigationItemSelectedListener(this)
        drawerToggle.syncState()

        // Check the current item
        drawer.menu.findItem(currentPage.menuId).isChecked = true

        // Fade in the main view
        mainView.alpha = 0f
        mainView.animate().alpha(1f).duration = 250
    }

    override fun onBackPressed() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Open the menu if it's not open
            drawerLayout.openDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.facebook -> {
                shareOnFacebook()
                true
            }
            R.id.twitter -> {
                shareOnTwitter()
                true
            }
            R.id.logout -> {
                logout()
                true
            }
            else -> {
                val homepage = homePageManager.getHomePage(item.itemId)

                // If it's the same as the current homepage, close the drawer and don't continue
                if (homepage == currentPage) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }

                // Try to get one of the activities to open
                switchDrawerActivity(Intent(this, homepage.activity::class.java))

                // Close the drawer
                drawerLayout.closeDrawer(GravityCompat.START)

                true
            }
        }
    }

    /**
     * Switches to a new drawer activity using the [intent]
     */
    protected fun switchDrawerActivity(intent: Intent) {
        // If it's not the currently opened activity, launch it after a short delay
        //  so that the user sees the drawer closing
        handler.postDelayed({
            startActivity(intent)
            finish()
        }, 250)

        // Fade out the main view
        mainView.animate().alpha(0f).duration = 150
    }

    /* HELPERS */

    /**
     * Logs the user out
     */
    private fun logout() {
        alertDialog(R.string.warning, R.string.logout_dialog_message) { _, which ->
            if (which == DialogAction.POSITIVE) {
                ga.sendEvent("Logout", "Clicked")
                clearManager.clearUserInfo()
                // Go back to SplashActivity
                startActivity<SplashActivity>()
                finish()
            }
        }
    }

    /**
     * Shares the app on Facebook
     */
    private fun shareOnFacebook() {
        ga.sendEvent("facebook", "attempt_post")

        // Set up all of the info
        // TODO Update Facebook Usage
        val content = ShareLinkContent.Builder()
                .setContentTitle(getString(R.string.social_facebook_title, "Android"))
                .setContentDescription(getString(R.string.social_facebook_description_android))
                .setContentUrl(getString(R.string.social_link_android).toUri())
                .setImageUrl(getString(R.string.social_facebook_image).toUri())
                .build()

        // Show the dialog
        val dialog = ShareDialog(this)
        dialog.registerCallback(facebookCallbackManager, object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result) {
                if (result.postId != null) {
                    // Let the user know they posted successfully
                    toast(R.string.social_post_success)
                    ga.sendEvent("facebook", "successful_post")
                } else {
                    Timber.i("Facebook post cancelled")
                }
            }

            override fun onCancel() {
                Timber.i("Facebook post cancelled")
            }

            override fun onError(e: FacebookException) {
                Timber.e(e, "Error posting to Facebook")
                toast(R.string.social_post_failure)
                ga.sendEvent("facebook", "failed_post")
            }
        })
        dialog.show(content)
    }

    /**
     * Shares the app on Twitter
     */
    private fun shareOnTwitter() {
        try {
            TweetComposer.Builder(this)
                    .text(getString(R.string.social_twitter_message_android, "Android"))
                    .url(URL(getString(R.string.social_link_android)))
                    .show()
        } catch (e: MalformedURLException) {
            Timber.e(e, "Twitter URL malformed")
        }
    }
}
