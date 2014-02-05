package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.Window;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.fragment.DayFragment;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.Day;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;

/**
 * Author: Shabbir
 * Date: 22/01/14, 9:07 PM
 * 
 * This Activity loads the schedule from https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchd
 */
public class ScheduleActivity extends FragmentActivity {
	private List<CourseSched> mCourseList;
    private ViewPager mPager;
    private  FragmentManager mSupportFragmentManager;

    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_schedule);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the first list of courses from the ApplicationClass
        mCourseList = ApplicationClass.getSchedule();;

        //ViewPager stuff
        mSupportFragmentManager = getSupportFragmentManager();
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(6);

        //Only set up the adapter if we are refreshing. If we are downloading a progress dialog
        //will block the view
        boolean refresh = !mCourseList.isEmpty();
        if(refresh){
            loadInfo();
        }

        //Start thread to get schedule
        //If the courses list is not empty, we only need to refresh
        new ScheduleGetter(refresh).execute();
    }

    //Method that returns a list of courses for a given day
    public List<CourseSched> getCoursesForDay(Day day){
        List<CourseSched> courses = new ArrayList<CourseSched>();

        //Go through the list of courses, find which ones have the same day
        for(CourseSched course : mCourseList){
            if(course.getDay() == day){
                courses.add(course);
            }
        }

        return courses;
    }

    private void loadInfo(){
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(mSupportFragmentManager);
        mPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ScheduleGetter extends AsyncTask<Void, Void, Void> {
        private boolean mRefresh;
        private ProgressDialog mProgressDialog;

        public ScheduleGetter(boolean refresh){
            mRefresh = refresh;
        }

        @Override
        protected void onPreExecute(){
            //Only show a ProgressDialog if we are not refreshing the content but
            //downloading it for the first time
            if(!mRefresh){
                mProgressDialog = new ProgressDialog(ScheduleActivity.this);
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
            }
            //If not, just put it in the Action bar
            else{
                setProgressBarIndeterminateVisibility(true);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String scheduleString =  Connection.getInstance().getUrl(Connection.minervaSchedule);

            //Clear the current course list
            mCourseList.clear();

            //Parsing code
            Document doc = Jsoup.parse(scheduleString);
            Elements scheduleTable = doc.getElementsByClass("datadisplaytable");
            String name, data;
            int crn;
            for (int i = 0; i < scheduleTable.size(); i+=2) {
                name = getCourseName(scheduleTable.get(i));
                crn = getCRN(scheduleTable.get(i));
                data = getSchedule(scheduleTable.get(i+1));
                addCourseSched(name, crn, data);
            }

            //Save it to the instance variable in Application class
            ApplicationClass.setSchedule(mCourseList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Reload the adapter
                    loadInfo();
                }
            });

            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            //Dismiss the progress dialog if there was one
            if(!mRefresh){
                mProgressDialog.dismiss();
            }
            else{
                setProgressBarIndeterminateVisibility(false);
            }
        }

        /**
         * This method takes the list of rows in the table and populate the mCourseList
         * @param dataDisplayTable
         */
        private String getCourseName(Element dataDisplayTable) {
            Element caption = dataDisplayTable.getElementsByTag("caption").first();
            String[] texts = caption.text().split(" - ");
            return (texts[1] + "-" + texts[2]);
        }

        private int getCRN(Element dataDisplayTable) {
            Element row = dataDisplayTable.getElementsByTag("tr").get(1);
            String crn = row.getElementsByTag("td").first().text();
            return Integer.parseInt(crn);
        }

        private String getSchedule(Element dataDisplayTable) {
            Element row = dataDisplayTable.getElementsByTag("tr").get(1);
            Elements cells = row.getElementsByTag("td");
            return (cells.get(0).text() + "," + cells.get(1).text() + "," + cells.get(2).text());
        }

        private void addCourseSched(String courseCode, int crn, String data) {
            String[] dataItems = data.split(",");
            String[] times = dataItems[0].split(" - ");
            char[] days = dataItems[1].toCharArray();
            String room = dataItems[2];

            int startHour, startMinute, endHour, endMinute;
            try{
                startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
                startMinute = Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
                endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
                endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
                String startPM = times[0].split(" ")[1];
                String endPM = times[1].split(" ")[1];

                //If it's PM, then add 12 hours to the hours for 24 hours format
                //Make sure it isn't noon
                if(startPM.equals("PM") && startHour != 12){
                    startHour += 12;
                }
                if(endPM.equals("PM") && endHour != 12){
                    endHour += 12;
                }
            }
            //Try/Catch for courses with no assigned times
            catch(NumberFormatException e){
                startHour = 0;
                startMinute = 0;
                endHour = 0;
                endMinute = 0;
            }

            for (char day : days) {
                mCourseList.add(new CourseSched(crn, courseCode, day, startHour, startMinute, endHour, endMinute, room));
            }
        }
    }

    private class SchedulePagerAdapter extends FragmentStatePagerAdapter{
        public SchedulePagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Day currentDay = Day.getDay(i);
            return DayFragment.newInstance(currentDay);
        }

        @Override
        public int getCount() {
            return 7;
        }
    }

}
