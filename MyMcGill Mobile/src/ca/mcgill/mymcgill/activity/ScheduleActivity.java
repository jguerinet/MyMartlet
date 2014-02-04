package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.fragment.DayFragment;
import ca.mcgill.mymcgill.objects.CourseSched;
import ca.mcgill.mymcgill.objects.Day;
import ca.mcgill.mymcgill.util.Connection;

/**
 * Author: Shabbir
 * Date: 22/01/14, 9:07 PM
 * 
 * This Activity loads the schedule from https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchd
 */
public class ScheduleActivity extends FragmentActivity {
    protected ScheduleActivity scheduleInstance = this;
	List<CourseSched> courseList = new ArrayList<CourseSched>();
    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        //Start trhead to get schedule
        new ScheduleGetter().execute();
        
        //get the schedule file in string format
        String fileContent = readFromFile("cousched.htm");
        
        Document doc = Jsoup.parse(fileContent);
        Elements scheduleTable = doc.getElementsByClass("datadisplaytable");
        String name, crn, data;
        for (int i = 0; i < scheduleTable.size(); i+=2) {
        	name = getCourseName(scheduleTable.get(i));
        	crn = getCRN(scheduleTable.get(i));
        	data = getSchedule(scheduleTable.get(i+1));
        	System.out.println(buildAttributeString(name, crn, data));
        	addCourseSched(buildAttributeString(name, crn, data));
        }

        //ViewPager stuff

        SchedulePagerAdapter adapter = new SchedulePagerAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(6);        
    }

    //Method that returns a list of courses for a given day
    public List<CourseSched> getCoursesForDay(Day day){
        List<CourseSched> courses = new ArrayList<CourseSched>();

        //Go through the list of courses, find which ones have the same day
        for(CourseSched course : courseList){
            if(course.getDay() == day){
                courses.add(course);
            }
        }

        return courses;
    }
    
    private String getWeek(Element form){
    	return form.text();
    }
    
    /**
     * This method takes the list of rows in the table and populate the courseList
     * @param dataDisplayTable
     */
    private String getCourseName(Element dataDisplayTable) {
		Element caption = dataDisplayTable.getElementsByTag("caption").first();
		String[] texts = caption.text().split(" - ");
		return (texts[1] + "-" + texts[2]);
	}
	private String getCRN(Element dataDisplayTable) {
		Element row = dataDisplayTable.getElementsByTag("tr").get(1);
		String crn = row.getElementsByTag("td").first().text();
		return crn;
	}
	private String getSchedule(Element dataDisplayTable) {
		Element row = dataDisplayTable.getElementsByTag("tr").get(1);
		Elements cells = row.getElementsByTag("td");
		String dataString = cells.get(0).text() + "," + cells.get(1).text() + "," + cells.get(2).text();
		return dataString;
	}
	private String buildAttributeString(String name, String crn, String data) {
		String attributes = name + "," + crn + "," + data;
		return attributes;
	}
	private void addCourseSched(String attributeString) {
		String[] attributes = attributeString.split(",");
		String[] times = attributes[2].split(" - ");
		String courseCode = attributes[0];
		int crn = Integer.parseInt(attributes[1]);
		String room = attributes[4];
		char[] days = attributes[3].toCharArray();
		int startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
        int startMinute = Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
        int endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
        int endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
        for (int i = 0; i < days.length; i++) {
    		CourseSched schedule = new CourseSched(crn, courseCode, days[i], startHour, startMinute, endHour, endMinute, room);
    		System.out.println(schedule.getDay());
    		courseList.add(schedule);
        }
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
    
    @SuppressWarnings("finally")
	private String readFromFile(String filename) {
    	
    	//create return string
        String ret = "";

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.cousched);;

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.toString());
        } catch (IOException e) {
            System.out.println("Can not read file: " + e.toString());
        }
        catch(Exception e){
        	System.out.println("Exception: " + e.toString());
        }
        finally{
        	
        	//always return something
        	return ret;
        }

    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private class ScheduleGetter extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            //TODO: REPLACE CONTENT VIEW WITH CIRCLE THINGY TO SHOW WE ARE LOADING
        }

        @Override
        protected String doInBackground(String... params) {

            return Connection.getInstance().getUrl(Connection.minervaSchedule);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Show the Up button in the action bar.
            setupActionBar();
            TextView textView = new TextView(scheduleInstance);
            String htmlAsAString = result;
            textView.setText(Html.fromHtml(htmlAsAString));
            textView.setMovementMethod(new ScrollingMovementMethod());
            setContentView(textView);

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
