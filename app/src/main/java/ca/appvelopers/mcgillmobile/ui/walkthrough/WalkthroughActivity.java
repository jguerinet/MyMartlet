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
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.main.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;

public class WalkthroughActivity extends BaseActivity {
    private ViewPager mViewPager;
    private WalkthroughAdapter mWalkthroughAdapter;

    static int position;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(ca.appvelopers.mcgillmobile.R.layout.activity_walkthrough);

        //Check if this is the normal walkthrough or the email one
        boolean email = getIntent().getBooleanExtra(Constants.EMAIL, false);

        Analytics.getInstance().sendScreen(email ? "Email Walkthrough" : "Walkthrough");

        mViewPager = (ViewPager) findViewById(R.id.walkthrough_viewpager);
        mWalkthroughAdapter = new WalkthroughAdapter(getSupportFragmentManager(), email);
        mViewPager.setAdapter(mWalkthroughAdapter);

        //Set the position to 0 initially
        position = 0;

        //Next
        final Button next = (Button) findViewById(R.id.walkthrough_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We've reached the end of the walkthrough
                if (position == mWalkthroughAdapter.getCount() - 1) {
                    finish();
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }

            }
        });

        //Close
        Button close = (Button) findViewById(R.id.walkthrough_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics.getInstance().sendEvent("Walkthrough", "Skip", null);
                finish();
            }
        });

        //Back
        final Button back = (Button) findViewById(R.id.walkthrough_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
            }
        });

        //Indicator
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.walkthrough_pageindicator);
        indicator.setViewPager(mViewPager);
        indicator.setStrokeColor(Color.WHITE);
        indicator.setPageColor(Color.GRAY);
        indicator.setFillColor(getResources().getColor(R.color.red));
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int i, float v, int i2){}
            @Override
            public void onPageSelected(int position){
                WalkthroughActivity.position = position;
                back.setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
                next.setText((position == mWalkthroughAdapter.getCount() - 1) ?
                        getResources().getString(R.string.start) : getResources().getString(R.string.next));
            }
            @Override
            public void onPageScrollStateChanged(int i){}
        });
    }
}