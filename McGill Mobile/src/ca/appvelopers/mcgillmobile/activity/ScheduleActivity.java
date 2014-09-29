package ca.appvelopers.mcgillmobile.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.DrawerFragmentActivity;
import ca.appvelopers.mcgillmobile.activity.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.fragment.DayFragment;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Day;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.util.downloader.ClassDownloader;
import ca.appvelopers.mcgillmobile.util.downloader.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.view.ScheduleViewBuilder;

/**
 * @author Nhat-Quang Dao
 * Date: 22/01/14, 9:07 PM
 * 
 * This Activity loads the schedule from https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchd
 */
public class ScheduleActivity extends DrawerFragmentActivity {
    private static final int CHANGE_SEMESTER_CODE = 100;

    private List<ClassItem> mClassList;
    private Term mTerm;
    private ScheduleViewBuilder mScheduleViewBuilder;
    private DateTime mDate;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_schedule);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Schedule");

        //Get the semester
        mTerm = App.getDefaultTerm();

        mClassList = new ArrayList<ClassItem>();

        //Set up the ScheduleViewBuilder
        mScheduleViewBuilder = new ScheduleViewBuilder(this);

        //Load the right view
        loadView(getResources().getConfiguration().orientation);

        //Check if this is the first time the user is using the app
        if(Load.isFirstOpen(this)){
            //Show him the walkthrough if it is
            startActivity(new Intent(this, WalkthroughActivity.class));
            //Save the fact that the walkthrough has been seen at least once
            Save.saveFirstOpen(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        //Reload the menu
        invalidateOptionsMenu();

        //Reload the view
        loadView(newConfig.orientation);
    }

    private void loadView(int orientation){
        loadInfo();

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            mScheduleViewBuilder.renderLandscapeView(mDate);
        }
        else{
            //Load the right view
            setContentView(R.layout.activity_schedule);

            //Set up the ViewPager
            //Open it to the right day (offset of 500002 to get the right day
            int firstDayIndex = 500002 + mDate.getDayOfWeek();
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            SchedulePagerAdapter adapter = new SchedulePagerAdapter(getSupportFragmentManager(), firstDayIndex);
            pager.setAdapter(adapter);
            pager.setCurrentItem(firstDayIndex);
        }

        //Reload the drawer
        loadDrawer();
    }

    private void loadInfo(){
        //Title
        setTitle(mTerm.toString(this));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.refresh_change_semester, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Opens the context menu    
            case R.id.action_change_semester:
            	Intent intent = new Intent(this, ChangeSemesterActivity.class);
                intent.putExtra(Constants.REGISTER_TERMS, false);
                intent.putExtra(Constants.TERM, mTerm);
                startActivityForResult(intent, CHANGE_SEMESTER_CODE);
            	return true;
            case R.id.action_refresh:
                //Start thread to retrieve schedule
                executeClassDownloader();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        //Only show the menu in portrait mode
        return getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CHANGE_SEMESTER_CODE){
            if(resultCode == RESULT_OK){
                //Get the chosen term
                mTerm = (Term)data.getSerializableExtra(Constants.TERM);
                if(!Test.LOCAL_SCHEDULE){
                    executeClassDownloader();
                }

                //Download the Transcript (if ever the user has new semesters on their transcript)
                new TranscriptDownloader(this, false) {
                    @Override
                    protected void onPreExecute() {}

                    @Override
                    protected void onPostExecute(Boolean loadInfo) {}
                }.execute();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    //Downloads the list of classes for the given term
    private void executeClassDownloader(){
        new ClassDownloader(this, mTerm){
            @Override
            protected void onPreExecute(){
                //Show the user we are refreshing
                setProgressBarIndeterminateVisibility(true);
            }

            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(Boolean loadInfo) {
                if(loadInfo){
                    //Reload the adapter
                    loadView(getResources().getConfiguration().orientation);
                }

                setProgressBarIndeterminateVisibility(false);
            }
        }.execute();
    }

    private class SchedulePagerAdapter extends FragmentStatePagerAdapter{
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
