/*
 * Copyright 2014-2019 Julien Guerinet
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

package com.guerinet.mymartlet.ui.walkthrough

import android.os.Bundle
import androidx.core.view.isInvisible
import androidx.viewpager.widget.ViewPager
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.util.Constants
import kotlinx.android.synthetic.main.activity_walkthrough.*

/**
 * Displays the walkthrough the first time the user opens the app or through the app settings
 * @author Julien Guerinet
 * @since 1.0.0
 */
class WalkthroughActivity : BaseActivity() {

    private val adapter: WalkthroughAdapter by lazy {
        val isFirstOpen = intent.getBooleanExtra(Constants.FIRST_OPEN, false)
        WalkthroughAdapter(isFirstOpen)
    }

    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkthrough)
        ga.sendScreen("Walkthrough")

        // Load the adapter
        viewPager.adapter = adapter

        indicator.setViewPager(viewPager)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
                // Hide the back button on the first page
                back.isInvisible = position == 0
                // Set the right text on the next button if we are on the last page
                next.setText(if (position == adapter.count - 1) R.string.start else R.string.next)
            }
        })

        next.setOnClickListener {
            // We've reached the end of the walkthrough
            if (currentPosition == adapter.count - 1) {
                finish()
            } else {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }

        back.setOnClickListener {
            viewPager.setCurrentItem(viewPager.currentItem - 1, true)
        }

        close.setOnClickListener {
            ga.sendEvent("Walkthrough", "Skip")
            finish()
        }
    }
}