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

package com.guerinet.mymartlet.ui.wishlist;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.CourseResult;
import com.guerinet.mymartlet.model.CourseResult_Table;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.ui.DrawerActivity;
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper;
import com.guerinet.mymartlet.util.Help;
import com.guerinet.mymartlet.util.dagger.prefs.RegisterTermPreference;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.suitcase.dialog.DialogUtils;
import com.guerinet.suitcase.util.Utils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Displays the user's wishlist
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class WishlistActivity extends DrawerActivity {
    /**
     * {@link RegisterTermPreference} instance
     */
    @Inject
    RegisterTermPreference registerTermPref;
    /**
     * The current currentTerm, null if none possible (no semesters to register for)
     */
    @Nullable
    private Term term;
    /**
     * {@link WishlistHelper} instance
     */
    private WishlistHelper wishlistHelper;
    /**
     * Keeps track of the number of concurrent update calls that are currently being executed
     */
    private int updateCount;
    /**
     * UI handler
     */
    private Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        ButterKnife.bind(this);
        App.Companion.component(this).inject(this);
        analytics.sendScreen("Wishlist");

        // Set up the view
        wishlistHelper = new WishlistHelper(this, mainView, false);

        // Load the first registration currentTerm if there is one
        if (!registerTermPref.getTerms().isEmpty()) {
            term = registerTermPref.getTerms().get(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!registerTermPref.getTerms().isEmpty()) {
            getMenuInflater().inflate(R.menu.refresh, menu);

            // Allow user to change the semester if there is more than 1 semester
            if (registerTermPref.getTerms().size() > 1) {
                getMenuInflater().inflate(R.menu.change_semester, menu);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_semester:
                DialogUtils.singleList(this, R.string.title_change_semester,
                        new TermDialogHelper(this, term, true) {
                            @Override
                            public void onTermSelected(Term newTerm) {
                                term = newTerm;
                                update();
                            }
                        });
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @HomepageManager.Homepage
    @Override
    protected int getCurrentPage() {
        return HomepageManager.WISHLIST;
    }

    /**
     * Updates the view
     */
    private void update() {
        if (term != null) {
            // Set the title if there is a currentTerm
            setTitle(term.getString(this));
        }
        // Reload the adapter
        wishlistHelper.update(term);
    }

    /**
     * Refreshes the course info on the current wishlist
     */
    private void refresh() {
        showToolbarProgress(true);

        // Go through the user's wishlist
        SQLite.select()
                .from(CourseResult.class)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    if (tResult == null) {
                        tResult = new ArrayList<>();
                    }
                    List<CourseHolder> holders = new ArrayList<>();
                    for (CourseResult course : tResult) {
                        CourseHolder holder = new CourseHolder(course);
                        if (!holders.contains(holder)) {
                            holders.add(holder);
                        }
                    }
                    mainHandler.post(() -> performUpdateCalls(holders));
                })
                .execute();

    }

    /**
     * Makes the necessary calls to update the list of given courses
     *
     * @param courses List of {@link CourseHolder}s to update, which represent the wishlist
     */
    private void performUpdateCalls(List<CourseHolder> courses) {
        if (courses.isEmpty()) {
            // If there are no courses, don't continue
            showToolbarProgress(false);
            return;
        }

        // Set the update count to the number of courses
        updateCount = courses.size();
        for (CourseHolder course : courses) {
            // Get the course registration URL
            String code[] = course.code.split(" ");
            if (code.length < 2) {
                Utils.toast(this, getString(R.string.error_cannot_update, course.code));
                finalizeUpdate();
                continue;
            }

            String subject = code[0];
            String number = code[1];

            mcGillService.search(course.term, subject, number, "", 0, 0, 0, 0, "a", 0, 0, "a",
                    new ArrayList<>()).enqueue(new retrofit2.Callback<List<CourseResult>>() {
                @Override
                public void onResponse(Call<List<CourseResult>> call,
                        Response<List<CourseResult>> response) {
                    // Go through the received courses, check if they are on the user's wishlist
                    for (CourseResult course : response.body()) {
                        boolean exists = SQLite.selectCountOf()
                                .from(CourseResult.class)
                                .where(CourseResult_Table.term.eq(course.getTerm()))
                                .and(CourseResult_Table.crn.eq(course.getCRN()))
                                .hasData();
                        if (exists) {
                            course.save();
                        }
                    }
                    finalizeUpdate();
                }

                @Override
                public void onFailure(Call<List<CourseResult>> call, Throwable t) {
                    Timber.e(t, "Error updating wishlist");
                    Help.handleException(WishlistActivity.this, t);
                    finalizeUpdate();
                }
            });
        }
    }

    /**
     * Finalizes an update call and determine whether UI action is necessary depending on where
     *  we are in the update process
     */
    private void finalizeUpdate() {
        // Decrement the update count;
        updateCount --;

        // If there are no more updates to wait for, hide the progress bar and reload the adapter
        if (updateCount == 0) {
            update();
            showToolbarProgress(false);
        }
    }

    /**
     * {@link CourseResult} holder to update the user's wishlist
     */
    private class CourseHolder {
        /**
         * Course currentTerm
         */
        private final Term term;
        /**
         * Course code
         */
        private final String code;

        /**
         * Default Constructor
         *
         * @param course {@link CourseResult} instance to hold the information for
         */
        private CourseHolder(CourseResult course) {
            term = course.getTerm();
            code = course.getCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CourseHolder)) {
                return false;
            }
            CourseHolder holder = (CourseHolder) obj;
            return holder.term.equals(term) && holder.code.equals(code);
        }
    }
}