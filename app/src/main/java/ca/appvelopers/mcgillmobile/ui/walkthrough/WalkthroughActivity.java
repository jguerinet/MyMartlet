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

package ca.appvelopers.mcgillmobile.ui.walkthrough;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Displays the walkthrough the first time the user opens the app
 *  (it can also be seen from the settings or be used to show the email walkthrough)
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class WalkthroughActivity extends BaseActivity {
    /**
     * The ViewPager
     */
    @Bind(R.id.walkthrough_viewpager)
    ViewPager mViewPager;
    /**
     * The ViewPagerIndicator
     */
    @Bind(R.id.indicator)
    CirclePageIndicator mIndicator;
    /**
     * The next button
     */
    @Bind(R.id.walkthrough_next)
    Button mNext;
    /**
     * The back button
     */
    @Bind(R.id.walkthrough_back)
    Button mBack;
    /**
     * The adapter used for the walkthrough
     */
    private WalkthroughAdapter mWalkthroughAdapter;
    /**
     * The current position in the walkthrough
     */
    private int mPosition = 0;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        ButterKnife.bind(this);

        //Check if this is the normal walkthrough or the email one
        boolean email = getIntent().getBooleanExtra(Constants.EMAIL, false);

        Analytics.getInstance().sendScreen(email ? "Email Walkthrough" : "Walkthrough");

        mWalkthroughAdapter = new WalkthroughAdapter(getSupportFragmentManager(), email);
        mViewPager.setAdapter(mWalkthroughAdapter);

        //Indicator
        mIndicator.setViewPager(mViewPager);
        mIndicator.setStrokeColor(Color.WHITE);
        mIndicator.setPageColor(Color.GRAY);
        mIndicator.setFillColor(ContextCompat.getColor(this, R.color.red));
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2){}
            @Override
            public void onPageSelected(int position){
                WalkthroughActivity.this.mPosition = position;
                mBack.setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
                mNext.setText((position == mWalkthroughAdapter.getCount() - 1) ?
                        getString(R.string.start) : getString(R.string.next));
            }
            @Override
            public void onPageScrollStateChanged(int i){}
        });
    }

    @OnClick(R.id.walkthrough_next)
    void next(){
        //We've reached the end of the walkthrough
        if (mPosition == mWalkthroughAdapter.getCount() - 1) {
            finish();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }
    }

    @OnClick(R.id.walkthrough_back)
    void back(){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
    }

    @OnClick(R.id.walkthrough_close)
    void close(){
        Analytics.getInstance().sendEvent("Walkthrough", "Skip", null);
        finish();
    }

    /**
     * Adapter used for the walkthroughs
     */
    class WalkthroughAdapter extends FragmentPagerAdapter {
        private boolean mEmail;

        public WalkthroughAdapter(FragmentManager fm, boolean email){
            super(fm);
            mEmail = email;
        }

        @Override
        public Fragment getItem(int position){
            return WalkthroughFragment.createInstance(position, mEmail);
        }

        @Override
        public int getCount(){
            return 7;
        }
    }
}