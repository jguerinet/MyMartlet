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

package com.guerinet.mymartlet.ui.walkthrough;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.ui.BaseActivity;
import com.guerinet.mymartlet.util.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Displays the walkthrough the first time the user opens the app or through the app settings
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class WalkthroughActivity extends BaseActivity {
    /**
     * The ViewPager
     */
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    /**
     * The ViewPagerIndicator
     */
//    @BindView(R.id.indicator)
//    CirclePageIndicator indicator;
    /**
     * The next button
     */
    @BindView(R.id.next)
    Button next;
    /**
     * The back button
     */
    @BindView(R.id.back)
    Button back;
    /**
     * Adapter used for the walkthrough
     */
    private WalkthroughAdapter adapter;
    /**
     * Current position in the walkthrough
     */
    private int position = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        ButterKnife.bind(this);
        ga.sendScreen("Walkthrough");

        // Load the adapter
        boolean firstOpen = getIntent().getBooleanExtra(Constants.FIRST_OPEN, false);
        adapter = new WalkthroughAdapter(this, firstOpen);
        viewPager.setAdapter(adapter);

        // Indicator
//        indicator.setViewPager(viewPager);
//        indicator.setStrokeColor(Color.WHITE);
//        indicator.setPageColor(Color.GRAY);
//        indicator.setFillColor(ContextCompat.getColor(this, R.color.red));
//        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageScrolled(int i, float v, int i2) {}
//
//            @Override
//            public void onPageSelected(int position) {
//                WalkthroughActivity.this.position = position;
//                // Hide the back button on the first page
//                back.setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
//                // Set the right text on the next button if we are on the last page
//                next.setText((position == adapter.getCount() - 1) ?
//                        R.string.start : R.string.next);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {}
//        });
    }

    @OnClick(R.id.next)
    void next() {
        // We've reached the end of the walkthrough
        if (position == adapter.getCount() - 1) {
            finish();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        }
    }

    @OnClick(R.id.back)
    void back() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
    }

    @OnClick(R.id.close)
    void close() {
        ga.sendEvent("Walkthrough", "Skip");
        finish();
    }
}