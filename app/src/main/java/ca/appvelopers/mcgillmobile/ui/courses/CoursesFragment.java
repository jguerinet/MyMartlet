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

package ca.appvelopers.mcgillmobile.ui.courses;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.DialogHelper;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Shows the user all of the courses the user has taken or is currently registered in
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @since 1.0.0
 */
public class CoursesFragment extends BaseFragment {
    /**
     * The ListView for the courses
     */
    @Bind(android.R.id.list)
    RecyclerView mListView;
    /**
     * The button to unregister from a course
     */
    @Bind(R.id.course_register)
    TextView mUnregisterButton;
    /**
     * The empty list view
     */
    @Bind(R.id.courses_empty)
    TextView mEmptyView;
    /**
     * The ListView adapter
     */
    private CoursesAdapter mAdapter;
    /**
     * The current term shown
     */
    private Term mTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        ButterKnife.bind(this, view);
        lockPortraitMode();
        Analytics.get().sendScreen("View Courses");

        mTerm = App.getDefaultTerm();

        mListView.setLayoutManager(new LinearLayoutManager(mActivity));

        //Remove this button
        view.findViewById(R.id.course_wishlist).setVisibility(View.GONE);

        //Done loading the view
        hideLoadingIndicator();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        update();
    }

    /**
     * Updates all of the info in the view
     */
    private void update(){
        //Set the title
        mActivity.setTitle(mTerm.toString());

        //User can unregister if the current term is in the list of terms to register for
        boolean canUnregister = App.getRegisterTerms().contains(mTerm);

        //Change the text and the visibility if we are in the list of currently registered courses
        if(canUnregister){
            mUnregisterButton.setVisibility(View.VISIBLE);
            mUnregisterButton.setText(getString(R.string.courses_unregister));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mUnregisterButton.setLayoutParams(params);
        }
        else{
            mUnregisterButton.setVisibility(View.GONE);
        }

        //Get the list of courses for this term
        List<Course> courses = new ArrayList<>();
        for(Course course : App.getCourses()){
            if(course.getTerm().equals(mTerm)){
                courses.add(course);
            }
        }

        //Set up the list
        mAdapter = new CoursesAdapter(courses, canUnregister);
        mListView.setAdapter(mAdapter);

        //Show the empty view if needed
        mListView.setVisibility(courses.isEmpty() ? View.GONE : View.VISIBLE);
        mEmptyView.setVisibility(courses.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh_change_semester, menu);
        Help.setTint(menu.findItem(R.id.action_refresh).getIcon(), android.R.color.white);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Change Semester
        if(item.getItemId() == R.id.action_change_semester){
            DialogHelper.showChangeSemesterDialog(mActivity, mTerm, false,
                    new DialogHelper.TermCallback() {
                        @Override
                        public void onTermSelected(Term term){
                            mTerm = term;
                            update();
                            refreshCourses();
                        }
                    });
            return true;
        }
        //Refresh
        else if(item.getItemId() == R.id.action_refresh){
            refreshCourses();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refreshes the list of courses for the given term and the user's transcript
     */
    private void refreshCourses(){
        //Show the user we are refreshing
        mActivity.showToolbarProgress(true);

        //Download the courses for this term
        new DownloaderThread(mActivity, Connection.getScheduleURL(mTerm))
                            .execute(new DownloaderThread.Callback() {
                        @Override
                        public void onDownloadFinished(String result){
                            //Parse the courses if there are any
                            if(result != null){
                                Parser.parseCourses(mTerm, result);

                                //Download the Transcript
                                //  (if ever the user has new semesters on their transcript)
                                new DownloaderThread(null, Connection.TRANSCRIPT_URL)
                                        .execute(new DownloaderThread.Callback() {
                                            @Override
                                            public void onDownloadFinished(String result){
                                                //Parse the transcript if possible
                                                if(result != null){
                                                    Parser.parseTranscript(result);
                                                }

                                                mActivity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run(){
                                                        //Update the view
                                                        update();

                                                        //Done refreshing
                                                        mActivity.showToolbarProgress(false);
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                });
    }



    /**
     * Tries to unregister from the given courses
     */
    @OnClick(R.id.course_register)
    public void unregister(){
        //Get checked courses from adapter
        final List<Course> courses = mAdapter.getCheckedCourses();

        //Too many courses
        if (courses.size() > 10) {
            Toast.makeText(mActivity, getString(R.string.courses_too_many_courses),
                    Toast.LENGTH_SHORT).show();
        }
        //No courses
        else if (courses.isEmpty()) {
            Toast.makeText(mActivity, getString(R.string.courses_none_selected),
                    Toast.LENGTH_SHORT).show();
        }
        else if (courses.size() > 0) {
            //Ask for confirmation before unregistering
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.unregister_dialog_title)
                    .setMessage(R.string.unregister_dialog_message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Show the user we are loading
                                    mActivity.showToolbarProgress(true);

                                    //Run the registration thread
                                    new DownloaderThread(mActivity,
                                            Connection.getRegistrationURL(mTerm, courses, true))
                                            .execute(new DownloaderThread.Callback() {
                                                @Override
                                                public void onDownloadFinished(String result){
                                                    if(result != null){
                                                        String error =
                                                                Parser.parseRegistrationErrors(
                                                                        result, courses);

                                                        //If there are no errors,
                                                        //  show the success message
                                                        if(error == null){
                                                            Toast.makeText(mActivity,
                                                                    R.string.unregistration_success,
                                                                    Toast.LENGTH_LONG).show();
                                                        }
                                                        //If not, show the error message
                                                        else{
                                                            DialogHelper.showNeutralDialog(mActivity,
                                                                    getString(R.string.
                                                                            unregistration_error),
                                                                    error);
                                                        }

                                                        //Refresh the courses
                                                        refreshCourses();
                                                    }
                                                }
                                            });
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        }
    }
}