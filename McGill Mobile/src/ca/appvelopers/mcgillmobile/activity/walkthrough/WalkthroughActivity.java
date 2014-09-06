package ca.appvelopers.mcgillmobile.activity.walkthrough;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseFragmentActivity;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Author : Julien
 * Date :  2014-05-26 8:54 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class WalkthroughActivity extends BaseFragmentActivity {
    private ViewPager mViewPager;
    private WalkthroughAdapter mWalkthroughAdapter;
    private boolean mEmail;

    static int position;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_walkthrough);

        overridePendingTransition(R.anim.in_from_top, R.anim.stay);

        //Check if this is the normal walkthrough or the email one
        mEmail = getIntent().getBooleanExtra(Constants.EMAIL, false);

        GoogleAnalytics.sendScreen(this, mEmail ? "Email Walkthrough" : "Walkthrough");

        //Get the screen height
        int displayHeight = Help.getDisplayHeight(getWindowManager().getDefaultDisplay());
        //Set the height to be 2/3 of the screen
        LinearLayout layout = (LinearLayout)findViewById(R.id.walkthrough_container);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.height = (3 * displayHeight) / 4;
        layout.setLayoutParams(params);

        mViewPager = (ViewPager) findViewById(R.id.walkthrough_viewpager);
        mWalkthroughAdapter = new WalkthroughAdapter(getSupportFragmentManager(), mEmail);
        mViewPager.setAdapter(mWalkthroughAdapter);

        //Next
        final TextView next = (TextView) findViewById(R.id.walkthrough_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We've reached the end of the walkthrough
                if (position == mWalkthroughAdapter.getCount() - 1) {
                    finish();
                    overridePendingTransition(0, R.anim.out_to_top);
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }

            }
        });

        //Back
        final TextView back = (TextView) findViewById(R.id.walkthrough_back);
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