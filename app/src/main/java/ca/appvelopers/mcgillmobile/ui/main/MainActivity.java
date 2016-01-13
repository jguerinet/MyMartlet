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

package ca.appvelopers.mcgillmobile.ui.main;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;

import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.courses.CoursesFragment;
import ca.appvelopers.mcgillmobile.ui.ebill.EbillFragment;
import ca.appvelopers.mcgillmobile.ui.map.MapFragment;
import ca.appvelopers.mcgillmobile.ui.schedule.ScheduleFragment;
import ca.appvelopers.mcgillmobile.ui.search.CourseSearchFragment;
import ca.appvelopers.mcgillmobile.ui.settings.SettingsFragment;
import ca.appvelopers.mcgillmobile.ui.web.DesktopFragment;
import ca.appvelopers.mcgillmobile.ui.web.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistFragment;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * The MainActivity the contains the side drawer and the main views for the fragments
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class MainActivity extends DrawerActivity {
    /**
     * The currently selected drawer item
     */
    private Homepage mCurrentItem;
    /**
     * The schedule view
     */
    private ScheduleFragment mScheduleFragment;
    /**
     * The MyCourses view
     */
    private MyCoursesActivity mMyCoursesFragment;
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

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbar(false);

        //Get the page from the intent. If not, use the home page
        mCurrentItem = (Homepage)getIntent().getSerializableExtra(Constants.HOMEPAGE);
        if(mCurrentItem == null){
            mCurrentItem = App.getHomepage();
        }

        //Create the fragments
        mScheduleFragment = new ScheduleFragment();
        mMyCoursesFragment = new MyCoursesActivity();
        mCoursesFragment = new CoursesFragment();
        mCourseSearchFragment = new CourseSearchFragment();
        mWishlistFragment = new WishlistFragment();
        mEbillFragment = new EbillFragment();
        mMapFragment = new MapFragment();
        mDesktopFragment = new DesktopFragment();
        mSettingsFragment = new SettingsFragment();

        //Load the initial checked item and fragment
        setFragment();
    }

    @Override
    public void onBackPressed(){
        if(mCurrentItem == Homepage.DESKTOP
                && mDesktopFragment.getWebView().canGoBack()){
            mDesktopFragment.getWebView().goBack();
            return;
        }
        //Open the menu if it is not open
        if(!mDrawerLayout.isDrawerOpen(mDrawer)){
            mDrawerLayout.openDrawer(mDrawer);
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
    public boolean onPrepareOptionsMenu (Menu menu) {
        //Only show the menu in portrait mode for the schedule
        if(mCurrentItem == Homepage.SCHEDULE){
            return getResources().getConfiguration().orientation !=
                    Configuration.ORIENTATION_LANDSCAPE;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Reload the menu and the view if this is the schedule
        if(mCurrentItem == Homepage.SCHEDULE){
            //Reload the menu
            invalidateOptionsMenu();

            //Reload the view
            mScheduleFragment.updateView(newConfig.orientation);
        }
    }

    /* HELPERS */

    /**
     * Changes the fragment in the main container
     */
    private void setFragment(){
        BaseFragment fragment = null;
        switch(mCurrentItem) {
            case SCHEDULE:
                fragment = mScheduleFragment;
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
}