/*
 * Copyright 2014-2015 Appvelopers
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

package ca.appvelopers.mcgillmobile.ui.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.ui.ChangeSemesterDialog;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;

/**
 * Represents the user's schedule
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class ScheduleFragment extends BaseFragment {
    /**
     * The list of courses
     */
    private List<Course> mCourses;
    /**
     * The current term
     */
    private Term mTerm;
    /**
     * The ScheduleViewBuilder
     */
    private ScheduleViewBuilder mViewBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mTerm = App.getDefaultTerm();
        mCourses = new ArrayList<>();

        //Fragment has a menu
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        //Set up the ScheduleViewBuilder
        mViewBuilder = new ScheduleViewBuilder(this, getStartingDate());

        //Load the right view
        View view = loadView(getResources().getConfiguration().orientation);

        //Check if this is the first time the user is using the app
        if(Load.isFirstOpen(mActivity)){
            //Show him the walkthrough if it is
            startActivity(new Intent(mActivity, WalkthroughActivity.class));
            //Save the fact that the walkthrough has been seen at least once
            Save.saveFirstOpen(mActivity);
        }

        //Hide the loading indicator
        hideLoadingIndicator();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        //Only load the menu in portrait mode
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            inflater.inflate(R.menu.refresh_change_semester, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_semester:
                final ChangeSemesterDialog dialog = new ChangeSemesterDialog(mActivity, mTerm,
                        false);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //Get the chosen term
                        Term term = dialog.getTerm();
                        if (term != null) {
                            mTerm = term;

                            //Restart the schedule view builder with the right date
                            mViewBuilder = new ScheduleViewBuilder(ScheduleFragment.this,
                                    getStartingDate());

                            //Refresh the content
                            boolean success = refreshCourses(mTerm);
                            //Update the view if this was a successful refresh
                            if(success){
                                updateView(mActivity.getResources().getConfiguration().orientation);
                            }
                        }
                    }
                });
                dialog.show();

                return true;
            case R.id.action_refresh:
                //Refresh the content
                boolean success = refreshCourses(mTerm);
                //Update the view if this was a successful refresh
                if(success){
                    updateView(mActivity.getResources().getConfiguration().orientation);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Reloads the view
     *
     * @param orientation The current orientation
     * @return The view to load
     */
    private View loadView(int orientation){
        //Title
        mActivity.setTitle(mTerm.toString());

        //Return the view
        return mViewBuilder.renderView(orientation);
    }

    /**
     * Gets the starting date based on the term and get the concerned classes
     *
     * @return The starting date
     */
    private LocalDate getStartingDate(){
        fillCourses();

        //Date is by default set to today
        LocalDate date = LocalDate.now();
        //Check if we are in the current semester
        if(!mTerm.equals(Term.getCurrentTerm())){
            //If not, find the starting date of this semester instead of using today
            for(Course classItem : mCourses){
                if(classItem.getStartDate().isBefore(date)){
                    date = classItem.getStartDate();
                }
            }
        }

        return date;
    }

    /**
     * Fills the class list with the current term's classes
     */
    private void fillCourses(){
        //Clear the current course list, add the courses that are for this semester
        mCourses.clear();
        for(Course classItem : App.getClasses()){
            if(classItem.getTerm().equals(mTerm)){
                mCourses.add(classItem);
            }
        }
    }

    /**
     * Returns the list of courses for a given day and date
     *
     * @param date The date
     * @return The list of courses
     */
    public List<Course> getCourses(LocalDate date){
        List<Course> courses = new ArrayList<>();

        //Go through the list of courses, find which ones have the same day and
        //  are for the given date
        for(Course course : mCourses){
            if(course.isForDate(date)){
                courses.add(course);
            }
        }
        return courses;
    }

    /**
     * Updates the view
     *
     * @param orientation The current orientation
     */
    public void updateView(int orientation){
        //Get the root view
        ViewGroup rootView = (ViewGroup) getView();

        if(rootView != null){
            // Remove all the existing views from the root view.
            rootView.removeAllViews();

            //Reload a new view
            rootView.addView(loadView(orientation));
        }
    }
}