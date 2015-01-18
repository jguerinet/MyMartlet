package ca.appvelopers.mcgillmobile.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import ca.appvelopers.mcgillmobile.activity.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.dialog.ChangeSemesterDialog;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.util.downloader.ClassDownloader;
import ca.appvelopers.mcgillmobile.util.downloader.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.view.ScheduleViewBuilder;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-12 10:39 PM
 * Copyright (c) 2014 Appvelopers Inc. All rights reserved.
 */

public class ScheduleFragment extends BaseFragment {
    private List<ClassItem> mClassList;
    private Term mTerm;
    private ScheduleViewBuilder mScheduleViewBuilder;
    private DateTime mDate;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Get the semester
        mTerm = App.getDefaultTerm();

        mClassList = new ArrayList<ClassItem>();

        //Set up the ScheduleViewBuilder
        mScheduleViewBuilder = new ScheduleViewBuilder(this);

        //Load the right view
        View view = loadView(getResources().getConfiguration().orientation);

        //Check if this is the first time the user is using the app
        if(Load.isFirstOpen(mActivity)){
            //Show him the walkthrough if it is
            startActivity(new Intent(mActivity, WalkthroughActivity.class));
            //Save the fact that the walkthrough has been seen at least once
            Save.saveFirstOpen(mActivity);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh_change_semester, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Opens the context menu
            case R.id.action_change_semester:
                new ChangeSemesterDialog(mActivity, false, mTerm, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //Get the chosen term
                        Term term = ((ChangeSemesterDialog)dialog).getTerm();
                        if(term != null){
                            mTerm = term;

                            //If we aren't in test mode, reload the classes
                            if(!Test.LOCAL_SCHEDULE){
                                executeClassDownloader();
                            }

                            //Download the Transcript (if ever the user has new semesters on their transcript)
                            new TranscriptDownloader(mActivity, false) {
                                @Override
                                protected void onPreExecute() {}
                                @Override
                                protected void onPostExecute(Boolean loadInfo) {}
                            }.execute();
                        }
                    }
                }).show();
                return true;
            case R.id.action_refresh:
                //Start thread to retrieve schedule
                executeClassDownloader();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public View loadView(int orientation){
        loadInfo();

        View view;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            view = mScheduleViewBuilder.renderLandscapeView(mDate);
        }
        else{
            //Load the right view
            view = View.inflate(mActivity, R.layout.fragment_schedule, null);

            //Set up the ViewPager
            //Open it to the right day (offset of 500002 to get the right day)
            int firstDayIndex = 500002 + mDate.getDayOfWeek();
            ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
            SchedulePagerAdapter adapter = new SchedulePagerAdapter(mActivity.getSupportFragmentManager(), firstDayIndex);
            pager.setAdapter(adapter);
            pager.setCurrentItem(firstDayIndex);
        }

        return view;
    }

    private void loadInfo(){
        //Title
        mActivity.setTitle(mTerm.toString(mActivity));

        //Clear the current course list, add the courses that are for this semester
        mClassList.clear();
        for(ClassItem classItem : App.getClasses()){
            if(classItem.getTerm().equals(mTerm)){
                mClassList.add(classItem);
            }
        }

        //Check if we are in the current semester
        mDate = DateTime.now();
        if(!mTerm.equals(Term.getCurrentTerm())){
            //If not, find the starting date of this semester instead of using today
            for(ClassItem classItem : mClassList){
                if(classItem.getStartDate().isBefore(mDate)){
                    mDate = classItem.getStartDate();
                }
            }
        }
    }

    //Downloads the list of classes for the given term
    private void executeClassDownloader(){
        new ClassDownloader(mActivity, mTerm){
            @Override
            protected void onPreExecute(){
                //Show the user we are refreshing
                mActivity.showToolbarSpinner(true);
            }

            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(Boolean loadInfo) {
                if(loadInfo){
                    //Reload the adapter
                    loadView(getResources().getConfiguration().orientation);
                }

                mActivity.showToolbarSpinner(false);
            }
        }.execute();
    }

    //Method that returns a list of courses for a given day
    public List<ClassItem> getClassesForDate(Day day, DateTime date){
        List<ClassItem> courses = new ArrayList<ClassItem>();

        //Go through the list of courses, find which ones have the same day and are for the given date
        for(ClassItem course : mClassList){
            if(course.isForDate(date) && course.getDays().contains(day)){
                courses.add(course);
            }
        }
        return courses;
    }

    private class SchedulePagerAdapter extends FragmentStatePagerAdapter {
        private int mFirstDayIndex;

        public SchedulePagerAdapter(FragmentManager fm, int firstDayIndex){
            super(fm);
            this.mFirstDayIndex = firstDayIndex;
        }

        @Override
        public Fragment getItem(int i) {
            Day currentDay = Day.getDay(i%7);
            DateTime date = mDate.plusDays(i - mFirstDayIndex);
            return DayFragment.newInstance(currentDay, date);
        }

        @Override
        public int getCount() {
            return 1000000;
        }
    }
}