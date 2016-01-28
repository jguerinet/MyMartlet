/*
 * Copyright 2014-2016 Appvelopers
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

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.guerinet.utils.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.TranscriptCourse;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.search.SearchResultsActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Displays the user's wishlist
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class WishlistActivity extends DrawerActivity {
    /**
     * The empty view
     */
    @Bind(R.id.courses_empty)
    protected TextView mEmptyView;
    /**
     * The wishlist
     */
    @Bind(android.R.id.list)
    protected RecyclerView mList;
    /**
     * The ListView adapter
     */
    private WishlistSearchCourseAdapter mAdapter;
    /**
     * The list of classes to display
     */
    private List<Course> mCourses;
    /**
     * The current term
     */
    private Term mTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        ButterKnife.bind(this);
        Analytics.get().sendScreen("Wishlist");

        //Check if there are any terms to register for
        if (App.getRegisterTerms().isEmpty()) {
            //Hide all of the main content, show explanatory text, and return the view
            mEmptyView.setText(R.string.registration_no_semesters);
            mEmptyView.setVisibility(View.VISIBLE);
            mList.setVisibility(View.GONE);
            return;
        }

        mList.setLayoutManager(new LinearLayoutManager(this));

        //Load the first registration term
        mTerm = App.getRegisterTerms().get(0);

        //Load the wishlist
        mCourses = App.getWishlist();

        //Update the wishlist
        updateWishlist();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!App.getRegisterTerms().isEmpty()) {
            getMenuInflater().inflate(R.menu.refresh, menu);
            Util.setTint(menu.findItem(R.id.action_refresh).getIcon(), Color.WHITE);

            //Allow user to change the semester if there is more than 1 semester
            if (App.getRegisterTerms().size() > 1) {
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
                DialogHelper.changeSemester(this, mTerm, true,
                        new DialogHelper.TermCallback() {
                            @Override
                            public void onTermSelected(Term term) {
                                mTerm = term;
                                update();
                            }
                        });
                return true;
            case R.id.action_refresh:
                updateWishlist();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected @Homepage.Type int getCurrentPage() {
        return Homepage.WISHLIST;
    }

    @OnClick(R.id.course_register)
    protected void register() {
        SearchResultsActivity.register(this, mTerm, mAdapter.getCheckedCourses());
        //Reload the adapter
        update();
    }

    @OnClick(R.id.course_wishlist)
    protected void removeFromWishlist() {
        SearchResultsActivity.addToWishlist(this, mAdapter.getCheckedCourses(), false);
        //Reload the adapter
        update();
    }

    /**
     * Updates the view
     */
    private void update() {
        //Only load the info if there is info to load
        if (!App.getRegisterTerms().isEmpty()) {
            //Set the title
            setTitle(mTerm.toString());

            //Reload the adapter
            mAdapter = new WishlistSearchCourseAdapter(this, mTerm, mCourses);
            mList.setAdapter(mAdapter);

            //If there are no classes, show the empty view
            mList.setVisibility(mAdapter.isEmpty() ? View.GONE : View.VISIBLE);
            mEmptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Updates the information of the courses on the current wishlist
     */
    private void updateWishlist() {
        new AsyncTask<Void, Void, Void>() {
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
                        mTranscriptCourses.add(new TranscriptCourse(course.getTerm(),
                                course.getCode(), course.getTitle(), course.getCredits(), "N/A",
                                "N/A"));
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
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
                    String url = new Connection.SearchURLBuilder(course.getTerm(), subject)
                            .courseNumber(number)
                            .build();

                    String html = new DownloaderThread(WishlistActivity.this, url)
                            .execute();

                    if (html != null) {
                        //TODO: Figure out a way to parse only some course sections instead of re-parsing all course sections for a given Course
                        //This parses all ClassItems for a given course
                        List<Course> updatedCourses =
                                Parser.parseClassResults(course.getTerm(), html);

                        //Update the course object with an updated class size
                        for (Course updatedClass : updatedCourses) {
                            for (Course wishlistClass : mCourses) {
                                if (wishlistClass.equals(updatedClass)) {
                                    int i = mCourses.indexOf(wishlistClass);
                                    mCourses.remove(wishlistClass);
                                    mCourses.add(i, updatedClass);
                                }
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                //Set the new wishlist
                App.setWishlist(mCourses);
                //Reload the adapter
                update();
                showToolbarProgress(false);
            }
        }.execute();
    }
}