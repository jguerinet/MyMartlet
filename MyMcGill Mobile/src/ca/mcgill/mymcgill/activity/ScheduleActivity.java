package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerAdapter;
import ca.mcgill.mymcgill.activity.drawer.DrawerFragmentActivity;
import ca.mcgill.mymcgill.exception.MinervaLoggedOutException;
import ca.mcgill.mymcgill.fragment.DayFragment;
import ca.mcgill.mymcgill.object.ConnectionStatus;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.Day;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Help;
import ca.mcgill.mymcgill.util.Load;

/**
 * @author Nhat-Quang Dao
 * Date: 22/01/14, 9:07 PM
 * 
 * This Activity loads the schedule from https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchd
 */
public class ScheduleActivity extends DrawerFragmentActivity {
	private List<CourseSched> mCourseList;
    private ViewPager mPager;
    private FragmentManager mSupportFragmentManager;

    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_schedule);
        mDrawerAdapter = new DrawerAdapter(this, DrawerAdapter.SCHEDULE_POSITION);
        super.onCreate(savedInstanceState);

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


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_schedule_land);
            showDrawer(false);
            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

            LinearLayout timetableContainer = (LinearLayout) findViewById(R.id.timetable_container);
            fillTimetable(inflater, timetableContainer);

            LinearLayout scheduleContainer = (LinearLayout)findViewById(R.id.schedule_container);
            for(int i = 0; i < 7; i ++){
                LinearLayout coursesLayout = new LinearLayout(this);
                coursesLayout.setOrientation(LinearLayout.VERTICAL);
                coursesLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.cell_landscape_width),
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                fillSchedule(inflater, coursesLayout, Day.getDay(i));
                scheduleContainer.addView(coursesLayout);

                //Line
                View line = new View(this);
                line.setBackgroundColor(getResources().getColor(R.color.black));
                line.setLayoutParams(new ViewGroup.LayoutParams(getResources().getDimensionPixelSize(R.dimen.line),
                        ViewGroup.LayoutParams.MATCH_PARENT));
                scheduleContainer.addView(line);
            }


        }
        else{
            setContentView(R.layout.activity_schedule);
            showDrawer(true);
            mPager = (ViewPager)findViewById(R.id.pager);
            mPager.setOffscreenPageLimit(6);
            loadInfo();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void loadInfo(){
        SchedulePagerAdapter adapter = new SchedulePagerAdapter(mSupportFragmentManager);
        mPager.setAdapter(adapter);

        //Open it to the right day
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.MONDAY:
                mPager.setCurrentItem(0);
                break;
            case Calendar.TUESDAY:
                mPager.setCurrentItem(1);
                break;
            case Calendar.WEDNESDAY:
                mPager.setCurrentItem(2);
                break;
            case Calendar.THURSDAY:
                mPager.setCurrentItem(3);
                break;
            case Calendar.FRIDAY:
                mPager.setCurrentItem(4);
                break;
            case Calendar.SATURDAY:
                mPager.setCurrentItem(5);
                break;
            case Calendar.SUNDAY:
                mPager.setCurrentItem(6);
                break;
        }
    }

    private void fillTimetable(LayoutInflater inflater, LinearLayout timetableContainer){
        //Empty view for the days
        //Day name
        View dayView = inflater.inflate(R.layout.activity_day_name, null);

        //Black line
        View dayViewLine = dayView.findViewById(R.id.day_line);
        dayViewLine.setVisibility(View.VISIBLE);

        timetableContainer.addView(dayView);

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Start inflating a timetable cell
            View timetableCell = inflater.inflate(R.layout.fragment_day_timetable_cell, null);

            //Quick check
            assert(timetableCell != null);

            //Put the correct time
            TextView time = (TextView)timetableCell.findViewById(R.id.cell_time);
            time.setText(Help.getShortTimeString(this, hour));

            //Add it to the right container
            timetableContainer.addView(timetableCell);
        }
    }

    //Method that fills the schedule based on given data
    private void fillSchedule(LayoutInflater inflater,
                              LinearLayout scheduleContainer, Day currentDay){
        //This will be used of an end time of a course when it is added to the schedule container
        int currentCourseEndTime = 0;

        List<CourseSched> mCourses = getCoursesForDay(currentDay);

        //Day name
        View dayView = inflater.inflate(R.layout.activity_day_name, null);
        TextView dayViewTitle = (TextView)dayView.findViewById(R.id.day_name);
        dayViewTitle.setText(currentDay.getDayString(this));

        scheduleContainer.addView(dayView);

        //Cycle through the hours
        for(int hour = 8; hour < 22; hour++){
            //Cycle through the half hours
            for(int min = 0; min < 31; min+= 30){
                //Initialize the current course to null
                CourseSched currentCourse = null;

                //Calculate time in minutes
                int timeInMinutes = 60*hour + min;

                //if currentCourseEndTime = 0 (no course is being added) or it is equal to
                //the current time in min (end of a course being added) we need to add a new view
                if(currentCourseEndTime == 0 || currentCourseEndTime == timeInMinutes){
                    //Reset currentCourseEndTime to 0
                    currentCourseEndTime = 0;

                    //Check if there is a course at this time
                    for(CourseSched course : mCourses){
                        //If there is, set the current course to that time, and calculate the
                        //ending time of this course
                        if(course.getStartTimeInMinutes() == timeInMinutes){
                            currentCourse = course;
                            currentCourseEndTime = course.getEndTimeInMinutes();
                            break;
                        }
                    }

                    View scheduleCell;

                    //There is a course at this time
                    if(currentCourse != null){
                        //Inflate the right view
                        scheduleCell = inflater.inflate(R.layout.fragment_day_cell, null);

                        //Quick check
                        assert(scheduleCell != null);

                        //Set up all of the info
                        TextView courseName = (TextView)scheduleCell.findViewById(R.id.course_code);
                        courseName.setText(currentCourse.getCourseCode());

                        TextView courseType = (TextView)scheduleCell.findViewById(R.id.course_type);
                        courseType.setText(currentCourse.getScheduleType());

                        TextView  courseTime = (TextView)scheduleCell.findViewById(R.id.course_time);
                        //Get the beginning time
                        String beginningTime = Help.getLongTimeString(this, hour, min);
                        //Get the end time
                        String endTime = Help.getLongTimeString(this, currentCourse.getEndHour(), currentCourse.getEndMinute());

                        courseTime.setText(getResources().getString(R.string.course_time, beginningTime, endTime));

                        TextView courseLocation = (TextView)scheduleCell.findViewById(R.id.course_location);
                        courseLocation.setText(currentCourse.getRoom());

                        //Find out how long this course is in terms of blocks of 30 min
                        int length = ((currentCourse.getEndHour() - currentCourse.getStartHour()) * 60 +
                                (currentCourse.getEndMinute() - currentCourse.getStartMinute())) / 30;

                        //Set the height of the view depending on this height
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                (int) this.getResources().getDimension(R.dimen.cell_30min_height) * length);
                        scheduleCell.setLayoutParams(lp);

                        //OnClick: CourseActivity (for a detailed description of the course)
                        scheduleCell.setClickable(false);
                    }
                    else{
                        //Inflate the empty view
                        scheduleCell = inflater.inflate(R.layout.fragment_day_cell_empty, null);

                        //Quick check
                        assert(scheduleCell != null);
                    }

                    //Add the given view to the schedule container
                    scheduleContainer.addView(scheduleCell);
                }
            }
        }
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
            String scheduleString = "";
            Context context = ScheduleActivity.this; 
            
			try {
				scheduleString = Connection.getInstance().getUrl(Connection.minervaSchedule);
			} catch (MinervaLoggedOutException e) {
				e.printStackTrace();
				ConnectionStatus connectionResult = Connection.getInstance().connectToMinerva(context,Load.loadUsername(context),Load.loadPassword(context));
                if(connectionResult == ConnectionStatus.CONNECTION_OK){
                	
                	//TRY again
                	try {
						scheduleString = Connection.getInstance().getUrl(Connection.minervaSchedule);
					} catch (Exception e1) {
						// TODO display error message
						e1.printStackTrace();
						return null;
					} 
                }
                else{
                    //TODO: display error Message
                	return null;
                }
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			}

            //Clear the current course list
            mCourseList.clear();

            //Parsing code
            Document doc = Jsoup.parse(scheduleString);
            Elements scheduleTable = doc.getElementsByClass("datadisplaytable");
            String name, data, credits;
            int crn;
            for (int i = 0; i < scheduleTable.size(); i+=2) {
                name = getCourseCodeAndName(scheduleTable.get(i));
                crn = getCRN(scheduleTable.get(i));
                data = getSchedule(scheduleTable.get(i+1));
                credits = getCredit(scheduleTable.get(i));
                addCourseSched(name, crn, credits, data);
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
            setProgressBarIndeterminateVisibility(false);
        }

        /**
         * This method takes the list of rows in the table and populate the mCourseList
         * @param dataDisplayTable
         */
        private String getCourseCodeAndName(Element dataDisplayTable) {
            Element caption = dataDisplayTable.getElementsByTag("caption").first();
            String[] texts = caption.text().split(" - ");
            return (texts[0].substring(0, texts[0].length() - 1) + "," + texts[1] + "," + texts[2]);
        }

        private int getCRN(Element dataDisplayTable) {
            Element row = dataDisplayTable.getElementsByTag("tr").get(1);
            String crn = row.getElementsByTag("td").first().text();
            return Integer.parseInt(crn);
        }
        private String getCredit(Element dataDisplayTable) {
        	Element row = dataDisplayTable.getElementsByTag("tr").get(5);
            String credit = row.getElementsByTag("td").first().text();
            return credit;
        }
        
        //return time, day, room, scheduleType, professor 
        private String getSchedule(Element dataDisplayTable) {
            Element row = dataDisplayTable.getElementsByTag("tr").get(1);
            Elements cells = row.getElementsByTag("td");
            return (cells.get(0).text() + "," + cells.get(1).text() + "," + cells.get(2).text() + "," + cells.get(4).text() + "," + cells.get(5).text());
        }
        

        private void addCourseSched(String course, int crn, String credit, String data) {
            String[] dataItems = data.split(",");
            String[] times = dataItems[0].split(" - ");
            char[] days = dataItems[1].toCharArray();
            String room = dataItems[2];
            String courseName = course.split(",")[0];
            String courseCode = course.split(",")[1];
            String section = course.split(",")[2];
            String profName = dataItems[4];
            String scheduleType = dataItems[3];

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
                mCourseList.add(new CourseSched(crn, courseCode, section, day, startHour, startMinute, endHour, endMinute, room, profName, courseName, credit, scheduleType));
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
