package ca.appvelopers.mcgillmobile.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerFragmentActivity;
import ca.appvelopers.mcgillmobile.fragment.AboutFragment;
import ca.appvelopers.mcgillmobile.fragment.HelpFragment;

/**
 * Created by Adnan2
 */
public class AboutActivity extends DrawerFragmentActivity {
    private ViewPager mPager;

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        //Set up the ViewPager
        mPager = (ViewPager)findViewById(R.id.about_viewpager);
        AboutPagerAdapter adapter = new AboutPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {}
            @Override
            public void onPageSelected(int i) {
                //Change the selected tab
                getActionBar().setSelectedNavigationItem(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        //Set up the Tab Listener
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                //Set the right tab
                mPager.setCurrentItem(tab.getPosition());
            }
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
        };

        //Add the tabs
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.title_about))
                .setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.about_help))
                .setTabListener(tabListener));
    }

    class AboutPagerAdapter extends FragmentStatePagerAdapter{
        public AboutPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(i == 0){
                return new AboutFragment();
            }
            return new HelpFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}