package ca.mcgill.mymcgill.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.TextView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerFragmentActivity;
import ca.mcgill.mymcgill.fragment.AboutFragment;
import ca.mcgill.mymcgill.fragment.HelpFragment;

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

        mPager = (ViewPager)findViewById(R.id.about_viewpager);
        AboutPagerAdapter adapter = new AboutPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {}
            @Override
            public void onPageSelected(int i) {
                getActionBar().setSelectedNavigationItem(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });

        //Tab Listener
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mPager.setCurrentItem(tab.getPosition());
            }
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
        };

        // add tabs

        TextView teamName = new TextView(this);
        teamName.setText(getResources().getString(R.string.about_team_name));
        teamName.setTextColor(Color.BLACK);
        teamName.setPadding(0,20,0,0); // need to make padding relative

        TextView help = new TextView(this);
        help.setText(getResources().getString(R.string.about_help));
        help.setTextColor(Color.BLACK);
        help.setGravity(Gravity.CENTER);
        help.setPadding(0,20,0,0); // need to make padding relative


        ActionBar.Tab teamNameTab = actionBar.newTab().setCustomView(teamName).setTabListener(tabListener);
        ActionBar.Tab helpTab = actionBar.newTab().setCustomView(help).setTabListener(tabListener);

        actionBar.addTab(teamNameTab);
        actionBar.addTab(helpTab);
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