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

package ca.appvelopers.mcgillmobile.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.ui.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.SplashActivity;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.courses.CoursesFragment;
import ca.appvelopers.mcgillmobile.ui.ebill.EbillFragment;
import ca.appvelopers.mcgillmobile.ui.map.MapFragment;
import ca.appvelopers.mcgillmobile.ui.schedule.ScheduleFragment;
import ca.appvelopers.mcgillmobile.ui.search.CourseSearchFragment;
import ca.appvelopers.mcgillmobile.ui.settings.SettingsFragment;
import ca.appvelopers.mcgillmobile.ui.transcript.TranscriptFragment;
import ca.appvelopers.mcgillmobile.ui.web.DesktopFragment;
import ca.appvelopers.mcgillmobile.ui.web.MyCoursesFragment;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Clear;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * The MainActivity the contains the side drawer and the main views for the fragments
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    /**
     * Progress bar shown when the user is switching fragments
     */
    @Bind(R.id.fragment_switcher)
    LinearLayout mFragmentSwitcherProgress;
    /**
     * The drawer layout
     */
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    /**
     * The ListView inside the drawer
     */
    @Bind(R.id.drawer_list)
    ListView mDrawerList;
    /**
     * The toggle for the drawer inside the action bar
     */
    private ActionBarDrawerToggle mDrawerToggle;
    /**
     * The adapter for the drawer ListView
     */
    private DrawerAdapter mAdapter;
    /**
     * The currently selected drawer item
     */
    private DrawerItem mCurrentDrawerItem;
    /**
     * True if we need to change the currently showed fragment, false otherwise
     */
    private boolean mChangeFragment = false;
    /**
     * The schedule view
     */
    private ScheduleFragment mScheduleFragment;
    /**
     * The transcript view
     */
    private TranscriptFragment mTranscriptFragment;
    /**
     * The MyCourses view
     */
    private MyCoursesFragment mMyCoursesFragment;
    /**
     * The courses view
     */
    private CoursesFragment mCoursesFragment;
    /**
     * The course search view
     */
    private CourseSearchFragment mCourseSearchFragment;
    /**
     * The wishlist view
     */
    private WishlistFragment mWishlistFragment;
    /**
     * The eBill view
     */
    private EbillFragment mEbillFragment;
    /**
     * The map view
     */
    private MapFragment mMapFragment;
    /**
     * The desktop site view
     */
    private DesktopFragment mDesktopFragment;
    /**
     * The settings view
     */
    private SettingsFragment mSettingsFragment;
    /**
     * Callback manager used for Facebook
     */
    private CallbackManager mCallbackManager;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Get the page from the intent. If not, use the home page
        mCurrentDrawerItem = (DrawerItem)getIntent().getSerializableExtra(Constants.HOMEPAGE);
        if(mCurrentDrawerItem == null){
            mCurrentDrawerItem = App.getHomePage();
        }

        //Create the fragments
        mScheduleFragment = new ScheduleFragment();
        mTranscriptFragment = new TranscriptFragment();
        mMyCoursesFragment = new MyCoursesFragment();
        mCoursesFragment = new CoursesFragment();
        mCourseSearchFragment = new CourseSearchFragment();
        mWishlistFragment = new WishlistFragment();
        mEbillFragment = new EbillFragment();
        mMapFragment = new MapFragment();
        mDesktopFragment = new DesktopFragment();
        mSettingsFragment = new SettingsFragment();

        //Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //Set up the Facebook callback manager
        mCallbackManager = CallbackManager.Factory.create();

        //Set up the toolbar
        Toolbar toolbar = setUpToolbar(false);

        //Set up the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0){
            @Override
            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                //Change fragments if needed
                if(mChangeFragment){
                    setFragment();
                }
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setFocusableInTouchMode(false);

        //Set up the drawer list
        mAdapter = new DrawerAdapter();
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //Get the concerned page and save it as the new drawer item
                DrawerItem drawerItem = mAdapter.getItem(position);

                //Share on Facebook
                if(drawerItem == DrawerItem.FACEBOOK){
                    shareOnFacebook();
                }
                //Share on Twitter
                else if(drawerItem == DrawerItem.TWITTER){
                    shareOnTwitter();
                }
                else if(drawerItem == DrawerItem.LOGOUT){
                    logout();
                }
                else{
                    mChangeFragment = mCurrentDrawerItem != drawerItem;

                    //If not, set the current drawer item if needed and close the drawer
                    if(mChangeFragment){
                        showFragmentSwitcherProgress(true);
                        mCurrentDrawerItem = drawerItem;
                    }

                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                checkItem();
            }
        });

        //Load the initial checked item and fragment
        checkItem();
        setFragment();

        //Show the BugDialog if there is one
        String parserBug = getIntent().getStringExtra(Constants.BUG);
        if(parserBug != null){
            DialogHelper.showBugDialog(this, parserBug.equals(Constants.TRANSCRIPT),
                    getIntent().getStringExtra(Constants.TERM));
        }
    }

    @Override
    public void onBackPressed(){
        //If we are on a web page, check if we can go back in the web page itself
        if(mCurrentDrawerItem == DrawerItem.MY_COURSES
                && mMyCoursesFragment.getWebView().canGoBack()){
            mMyCoursesFragment.getWebView().goBack();
            return;
        }
        else if(mCurrentDrawerItem == DrawerItem.DESKTOP
                && mDesktopFragment.getWebView().canGoBack()){
            mDesktopFragment.getWebView().goBack();
            return;
        }
        //Open the menu if it is not open
        if(!mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.openDrawer(mDrawerList);
        }
        //If it is open, ask the user if he wants to exit
        else{
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.drawer_exit))
                    .setPositiveButton(getString(android.R.string.yes),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(getString(android.R.string.no), null)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        //Only show the menu in portrait mode for the schedule
        if(mCurrentDrawerItem == DrawerItem.SCHEDULE){
            return getResources().getConfiguration().orientation !=
                    Configuration.ORIENTATION_LANDSCAPE;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

        //Reload the menu and the view if this is the schedule
        if(mCurrentDrawerItem == DrawerItem.SCHEDULE){
            //Reload the menu
            invalidateOptionsMenu();

            //Reload the view
            mScheduleFragment.updateView(newConfig.orientation);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //For Facebook sharing
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /* HELPERS */

    /**
     * Sets the current drawer item to checked
     */
    private void checkItem(){
        for(int i = 1; i < mAdapter.getCount(); i++){
            mDrawerList.setItemChecked(i, mAdapter.getItem(i) == mCurrentDrawerItem);
        }
    }

    /**
     * Changes the fragment in the main container
     */
    private void setFragment(){
        BaseFragment fragment = null;
        switch(mCurrentDrawerItem) {
            case SCHEDULE:
                fragment = mScheduleFragment;
                break;
            case TRANSCRIPT:
                fragment = mTranscriptFragment;
                break;
            case MY_COURSES:
                fragment = mMyCoursesFragment;
                break;
            case COURSES:
                fragment = mCoursesFragment;
                break;
            case SEARCH_COURSES:
                fragment = mCourseSearchFragment;
                break;
            case WISHLIST:
                fragment = mWishlistFragment;
                break;
            case EBILL:
                fragment = mEbillFragment;
                break;
            case MAP:
                fragment = mMapFragment;
                break;
            case DESKTOP:
                fragment = mDesktopFragment;
                break;
            case SETTINGS:
                fragment = mSettingsFragment;
                break;
            default:
                break;
        }

        //If there is a fragment, insert it by replacing any existing fragment
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Shows or hides the progress bar in the main view while switching fragments
     *
     * @param visible True if it should be visible, false otherwise
     */
    public void showFragmentSwitcherProgress(boolean visible){
        mFragmentSwitcherProgress.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Logs the user out
     */
    private void logout(){
        //Confirm with the user
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.logout_dialog_title))
                .setMessage(getString(R.string.logout_dialog_message))
                .setPositiveButton(getString(R.string.logout_dialog_positive),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Analytics.getInstance().sendEvent("Logout", "Clicked", null);
                                Clear.clearAllInfo(MainActivity.this);
                                //Go back to SplashActivity
                                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                            }

                        })
                .setNegativeButton(getString(R.string.logout_dialog_negative), null)
                .create()
                .show();
    }

    /**
     * Shares the app on Facebook
     */
    private void shareOnFacebook(){
        Analytics.getInstance().sendEvent("facebook", "attempt_post", null);

        //Set up all of the info
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(getString(R.string.social_facebook_title, "Android"))
                .setContentDescription(getString(R.string.social_facebook_description_android))
                .setContentUrl(Uri.parse(getString(R.string.social_link_android)))
                .setImageUrl(Uri.parse(getString(R.string.social_facebook_image)))
                .build();

        //Show the dialog
        ShareDialog dialog = new ShareDialog(MainActivity.this);
        dialog.registerCallback(mCallbackManager, new FacebookCallback<Sharer.Result>(){
            @Override
            public void onSuccess(Sharer.Result result){
                if(result.getPostId() != null){
                    //Let the user know he posted successfully
                    Toast.makeText(MainActivity.this, getString(R.string.social_post_success),
                            Toast.LENGTH_SHORT).show();
                    Analytics.getInstance().sendEvent("facebook", "successful_post", null);
                }
                else{
                    Log.d(TAG, "Facebook post cancelled");
                }
            }

            @Override
            public void onCancel(){
                Log.d(TAG, "Facebook post cancelled");
            }

            @Override
            public void onError(FacebookException e){
                Toast.makeText(MainActivity.this, getString(R.string.social_post_failure),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Analytics.getInstance().sendEvent("facebook", "failed_post", null);
            }
        });
        dialog.show(content);
    }

    /**
     * Shares the app on Twitter
     */
    private void shareOnTwitter(){
        try{
            //Set up the Tweet Composer
            TweetComposer.Builder builder = new TweetComposer.Builder(this)
                    .text(getString(R.string.social_twitter_message_android, "Android"))
                    .url(new URL(getString(R.string.social_link_android)));

            //Show the TweetComposer
            builder.show();
        } catch(MalformedURLException e){
            Log.e(TAG, "Twitter URL malformed");
        }
    }
}