/*
 * Copyright 2014-2017 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.ui.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistHelper;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Shows the results of the search from the SearchActivity
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SearchResultsActivity extends BaseActivity {
    /**
     * Main view
     */
    @BindView(R.id.main)
    LinearLayout main;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_courses);
        ButterKnife.bind(this);
        setUpToolbar(true);
        analytics.sendScreen("Search Results");

        // Get the info from the intent
        Term term = (Term) getIntent().getSerializableExtra(Constants.TERM);
        List<CourseResult> courses =
                (ArrayList<CourseResult>) getIntent().getSerializableExtra(Constants.COURSES);

        // Set the title and the content
        setTitle(term.getString(this));
        new WishlistHelper(this, main, true).update(courses);
    }
}