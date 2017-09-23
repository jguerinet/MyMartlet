/*
 * Copyright 2014-2017 Julien Guerinet
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

package com.guerinet.mymartlet.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.suitcase.dialog.DialogUtils;
import com.guerinet.suitcase.util.Utils;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Base class for all of the activities with the main navigation drawer
 * @author Julien Guerinet
 * @since 2.1.0
 */
public abstract class DrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Drawer layout
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    /**
     * Navigation view
     */
    @BindView(R.id.drawer)
    NavigationView drawer;
    /**
     * Main content view to fade out and in on page change
     */
    @BindView(R.id.main)
    protected View mainView;
    /**
     * {@link HomepageManager} instance
     */
    @Inject
    protected HomepageManager homepageManager;
    /**
     * Toggle for the drawer inside the action bar
     */
    private ActionBarDrawerToggle drawerToggle;
    /**
     * Callback manager used for Facebook
     */
    private CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.Companion.component(this).inject(this);

        // Set up the Facebook callback manager
        facebookCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Set up the toolbar
        setUpToolbar(false);

        // Set up the drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerLayout.setFocusableInTouchMode(false);
        drawer.setNavigationItemSelectedListener(this);
        drawerToggle.syncState();

        // Check the current item
        drawer.getMenu().findItem(homepageManager.getMenuId(getCurrentPage())).setChecked(true);

        // Fade in the main view
        mainView.setAlpha(0);
        mainView.animate().alpha(1).setDuration(250);
    }

    @Override
    public void onBackPressed() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // Open the menu if it's not open
            drawerLayout.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.facebook:
                shareOnFacebook();
                return true;
            case R.id.twitter:
                shareOnTwitter();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                int homepage = homepageManager.getHomepage(item.getItemId());

                // If it's the same as the current homepage, close the drawer and don't continue
                if (homepage == getCurrentPage()) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }

                // Try to get one of the activities to open
                final Class activity = homepageManager.getActivity(homepage);

                switchDrawerActivity(new Intent(this, activity));

                // Close the drawer
                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
        }
    }

    protected void switchDrawerActivity(Intent intent) {
        // If it's not the currently opened activity, launch it after a short delay
        //  so that the user sees the drawer closing
        handler.postDelayed(() -> {
            startActivity(intent);
            finish();
        }, 250);

        // Fade out the main view
        mainView.animate().alpha(0).setDuration(150);
    }

    /* ABSTRACT */

    /**
     * @return The page in the drawer that this activity represents
     */
    @HomepageManager.Homepage
    protected abstract int getCurrentPage();

    /* HELPERS */

    /**
     * Logs the user out
     */
    private void logout() {
        DialogUtils.alert(this, R.string.warning, R.string.logout_dialog_message,
                (dialog, which) -> {
                    if (which == DialogAction.POSITIVE) {
                        analytics.sendEvent("Logout", "Clicked");
                        clearManager.all();
                        // Go back to SplashActivity
                        startActivity(new Intent(this, SplashActivity.class));
                        finish();
                    }
                    dialog.dismiss();
                });
    }

    /**
     * Shares the app on Facebook
     */
    private void shareOnFacebook() {
        analytics.sendEvent("facebook", "attempt_post");

        // Set up all of the info
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(getString(R.string.social_facebook_title, "Android"))
                .setContentDescription(getString(R.string.social_facebook_description_android))
                .setContentUrl(Uri.parse(getString(R.string.social_link_android)))
                .setImageUrl(Uri.parse(getString(R.string.social_facebook_image)))
                .build();

        // Show the dialog
        ShareDialog dialog = new ShareDialog(this);
        dialog.registerCallback(facebookCallbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                if (result.getPostId() != null) {
                    // Let the user know they posted successfully
                    Utils.toast(DrawerActivity.this, R.string.social_post_success);
                    analytics.sendEvent("facebook", "successful_post");
                } else {
                    Timber.i("Facebook post cancelled");
                }
            }

            @Override
            public void onCancel() {
                Timber.i("Facebook post cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Timber.e(e, "Error posting to Facebook");
                Utils.toast(DrawerActivity.this, R.string.social_post_failure);
                analytics.sendEvent("facebook", "failed_post");
            }
        });
        dialog.show(content);
    }

    /**
     * Shares the app on Twitter
     */
    private void shareOnTwitter() {
        try {
            new TweetComposer.Builder(this)
                    .text(getString(R.string.social_twitter_message_android, "Android"))
                    .url(new URL(getString(R.string.social_link_android)))
                    .show();
        } catch (MalformedURLException e) {
            Timber.e(e, "Twitter URL malformed");
        }
    }
}
