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

package ca.appvelopers.mcgillmobile.fragment;

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

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.dialog.ChangeSemesterDialog;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.thread.ClassDownloader;
import ca.appvelopers.mcgillmobile.thread.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.view.ScheduleViewBuilder;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class ScheduleFragment extends BaseFragment {
    private List<ClassItem> mClassList;
    private Term mTerm;
    private ScheduleViewBuilder mScheduleViewBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //If there is no term set, use the default one
        if(mTerm == null){
            mTerm = App.getDefaultTerm();
        }

        mClassList = new ArrayList<ClassItem>();

        //Set up the ScheduleViewBuilder
        mScheduleViewBuilder = new ScheduleViewBuilder(this, getStartingDate());

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
            // Opens the context menu
            case R.id.action_change_semester:
                final ChangeSemesterDialog dialog = new ChangeSemesterDialog(mActivity,
                        false, mTerm);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //Get the chosen term
                        Term term = dialog.getTerm();
                        if (term != null) {
                            mTerm = term;

                            //Restart the schedule view builder with the right date
                            mScheduleViewBuilder = new ScheduleViewBuilder(ScheduleFragment.this,
                                    getStartingDate());

                            //If we aren't in test mode, reload the classes
                            if (!Test.LOCAL_SCHEDULE) {
                                executeClassDownloader();
                            }

                            //Download the Transcript
                            //  (if ever the user has new semesters on their transcript)
                            final TranscriptDownloader downloader =
                                    new TranscriptDownloader(mActivity, false);
                            downloader.start();

                            //Wait for the downloader to finish
                            synchronized(downloader){
                                try{
                                    downloader.wait();
                                } catch(InterruptedException e){}
                            }
                        }
                    }
                });
                dialog.show();

                return true;
            case R.id.action_refresh:
                //Start thread to retrieve schedule
                executeClassDownloader();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public View loadView(int orientation){
        //Title
        mActivity.setTitle(mTerm.toString(mActivity));

        //Return the view
        return mScheduleViewBuilder.renderView(orientation);
    }

    /**
     * Get the starting date based on the term and get the concerned classes
     *
     * @return The starting date
     */
    private DateTime getStartingDate(){
        fillClassList();

        //Date is by default set to today
        DateTime date = DateTime.now();
        //Check if we are in the current semester
        if(!mTerm.equals(Term.getCurrentTerm())){
            //If not, find the starting date of this semester instead of using today
            for(ClassItem classItem : mClassList){
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
    private void fillClassList(){
        //Clear the current course list, add the courses that are for this semester
        mClassList.clear();
        for(ClassItem classItem : App.getClasses()){
            if(classItem.getTerm().equals(mTerm)){
                mClassList.add(classItem);
            }
        }
    }

    //Downloads the list of classes for the given term
    private void executeClassDownloader(){
        //Show the user we are refreshing
        mActivity.showToolbarProgress(true);

        final ClassDownloader downloader = new ClassDownloader(mActivity, mTerm);
        downloader.start();

        //Wait for the downloader to finish
        synchronized(downloader){
            try{
                downloader.wait();
            } catch(InterruptedException e){}
        }

        mActivity.showToolbarProgress(false);

        if(downloader.success()){
            updateView(mActivity.getResources().getConfiguration().orientation);
        }
    }

    //Method that returns a list of courses for a given day
    public List<ClassItem> getClassesForDate(Day day, DateTime date){
        List<ClassItem> courses = new ArrayList<ClassItem>();

        //Make sure the class list is initialized
        if(mClassList == null){
            mClassList = new ArrayList<ClassItem>();
            fillClassList();
        }

        //Go through the list of courses, find which ones have the same day and are for the given date
        for(ClassItem course : mClassList){
            if(course.isForDate(date) && course.getDays().contains(day)){
                courses.add(course);
            }
        }
        return courses;
    }

    public void updateView(int orientation){
        //Get the root view
        ViewGroup rootView = (ViewGroup) getView();

        // Remove all the existing views from the root view.
        rootView.removeAllViews();

        //Reload a new view
        rootView.addView(loadView(orientation));
    }
}