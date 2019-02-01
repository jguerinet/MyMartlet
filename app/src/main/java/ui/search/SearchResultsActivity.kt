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

package com.guerinet.mymartlet.ui.search

import android.os.Bundle
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.CourseResult
import com.guerinet.mymartlet.model.Term
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.ui.wishlist.WishlistHelper
import com.guerinet.mymartlet.util.Constants
import kotlinx.android.synthetic.main.view_courses.*
import java.util.*

/**
 * Shows the results of the search from the SearchActivity
 * @author Julien Guerinet
 * @since 1.0.0
 */
class SearchResultsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_courses)
        setUpToolbar()

        // Get the info from the intent
        val term = intent.getSerializableExtra(Constants.TERM) as? Term ?: error("Missing term")
        @Suppress("UNCHECKED_CAST")
        val courses = intent.getSerializableExtra(Constants.COURSES)
                as? ArrayList<CourseResult> ?: error("Missing course results")

        // Set the title and the content
        title = term.getString(this)
        WishlistHelper(this, mainView, true).update(courses)
    }
}