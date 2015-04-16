package ca.appvelopers.mcgillmobile.activity.walkthrough;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;

/**
 * Author : Julien
 * Date :  2014-05-26 8:54 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class WalkthroughActivity extends BaseActivity {
    private ViewPager mViewPager;
    private WalkthroughAdapter mWalkthroughAdapter;

    static int position;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(ca.appvelopers.mcgillmobile.R.layout.activity_walkthrough);

        //Check if this is the normal walkthrough or the email one
        boolean email = getIntent().getBooleanExtra(Constants.EMAIL, false);

        GoogleAnalytics.sendScreen(this, email ? "Email Walkthrough" : "Walkthrough");

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
                GoogleAnalytics.sendEvent(WalkthroughActivity.this, "Walkthrough", "Skip", null, null);
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