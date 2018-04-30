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

package com.guerinet.mymartlet.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.CourseResult;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.ui.BaseActivity;
import com.guerinet.mymartlet.ui.wishlist.WishlistHelper;
import com.guerinet.mymartlet.util.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Shows the results of the search from the SearchActivity
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SearchResultsActivity extends BaseActivity {
    /**
     * Main view
     */
    @BindView(R.id.mainView)
    LinearLayout main;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_courses);
        ButterKnife.bind(this);
        setUpToolbar(true);
        getGa().sendScreen("Search Results");

        // Get the info from the intent
        Term term = (Term) getIntent().getSerializableExtra(Constants.TERM);
        List<CourseResult> courses =
                (ArrayList<CourseResult>) getIntent().getSerializableExtra(Constants.COURSES);

        // Set the title and the content
        setTitle(term.getString(this));
        new WishlistHelper(this, main, true).update(courses);
    }
}