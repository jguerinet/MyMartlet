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

package ca.appvelopers.mcgillmobile.ui.wishlist;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.guerinet.utils.dialog.DialogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.transcript.TranscriptCourse;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.list.TermDialogHelper;
import ca.appvelopers.mcgillmobile.ui.search.SearchResultsActivity;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.RegisterTermPreference;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
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
     * {@link TranscriptManager} instance
     */
    @Inject
    RegisterTermPreference registerTermPref;
    /**
     * The current term, null if none possible (no semesters to register for)
     */
    @Nullable
    private Term term;
    /**
     * {@link WishlistHelper} instance
     */
    WishlistHelper wishlistHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        analytics.sendScreen("Wishlist");

        // Set up the view
        wishlistHelper = new WishlistHelper(this, mainView, false);

        // Load the first registration term if there is one
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
                DialogUtils.list(this, R.string.title_change_semester,
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
            // Set the title if there is a term
            setTitle(term.getString(this));
        }
        // Reload the adapter
        wishlistHelper.update(term, App.getWishlist());
    }

    /**
     * Refreshes the course info on the current wishlist
     */
    private void refresh() {
        final List<CourseResult> mCourses = App.getWishlist();
        new AsyncTask<Void, Void, IOException>() {
            private List<TranscriptCourse> mTranscriptCourses;

            @Override
            protected void onPreExecute() {
                showToolbarProgress(true);

                //Sort Courses into TranscriptCourses
                mTranscriptCourses = new ArrayList<>();
                for (Course course : mCourses) {
                    boolean courseExists = false;
                    //Check if course exists in list
                    for (TranscriptCourse addedCourse : mTranscriptCourses) {
                        if (addedCourse.getCourseCode().equals(course.getCode())) {
                            courseExists = true;
                        }
                    }
                    //Add course if it has not already been added
                    if (!courseExists) {
                        mTranscriptCourses.add(new TranscriptCourse(-1, course.getTerm(),
                                course.getCode(), course.getTitle(), course.getCredits(), "N/A",
                                "N/A"));
                    }
                }
            }

            @Override
            protected IOException doInBackground(Void... params) {
                //For each course, obtain its Minerva registration page
                for (TranscriptCourse course : mTranscriptCourses) {
                    //Get the course registration URL
                    String code[] = course.getCourseCode().split(" ");
                    if (code.length < 2) {
                        //TODO: Get a String for this
                        Toast.makeText(WishlistActivity.this, "Cannot update " +
                                        course.getCourseCode(), Toast.LENGTH_SHORT).show();
                        continue;
                    }

                    String subject = code[0];
                    String number = code[1];

                    try {
                        Response<List<CourseResult>> results = mcGillService.search(
                                course.getTerm(), subject, number, "", 0, 0, 0, 0, "a", 0, 0, "a",
                                new ArrayList<>()).execute();

                        // TODO Fix fact that this can update courses concurrently
                        //Update the course object with an updated class size
                        for (CourseResult updatedClass : results.body()) {
                            for (CourseResult wishlistClass : mCourses) {
                                if (wishlistClass.equals(updatedClass)) {
                                    int i = mCourses.indexOf(wishlistClass);
                                    mCourses.remove(wishlistClass);
                                    mCourses.add(i, updatedClass);
                                }
                            }
                        }

                    } catch (IOException e) {
                        Timber.e(e, "Error updating wishlist");
                        return e;
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(IOException result) {
                //Set the new wishlist
                App.setWishlist(mCourses);
                //Reload the adapter
                update();
                showToolbarProgress(false);
                Help.handleException(WishlistActivity.this, result);
            }
        }.execute();
    }
}