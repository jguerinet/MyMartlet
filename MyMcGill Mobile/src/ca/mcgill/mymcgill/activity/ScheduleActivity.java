package ca.mcgill.mymcgill.activity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;
import ca.mcgill.mymcgill.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ca.mcgill.mymcgill.objects.CourseSched;
/**
 * Author: Shabbir
 * Date: 22/01/14, 9:07 PM
 * 
 * This Activity loads the schedule from https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchd
 */
public class ScheduleActivity extends Activity {
	ArrayList<CourseSched> courseList = new ArrayList<CourseSched>();
    @SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        
     // Make sure we're running on Honeycomb or higher to use ActionBar APIs to return home
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        //get the schedule file in string format
        String fileContent = readFromFile("minsched.html");
        
        Document doc = Jsoup.parse(fileContent);
        Element scheduleTable = doc.getElementsByClass("datadisplaytable").first();
        Elements weekCell = doc.getElementsByClass("fieldlargetext");
        Elements scheduleRows = scheduleTable.getElementsByTag("tr");
        getWeek(weekCell.first());
        getSchedule(scheduleRows);
        
        
        
        
        
    }
    
    private String getWeek(Element form){
    	return form.text();
    }
    
    /**
     * This method takes the list of rows in the table and populate the courseList
     * @param rows
     */
    private void getSchedule(Elements rows){
    	for (int i = 0; i < rows.size(); i++) {
        	Element row = rows.get(i);
        	Elements cells = row.getElementsByTag("td");
        	if (cells.size() < 7) {
        		continue;
        	}
        	for (int day = 0; day < cells.size(); day++) {
        		Element cell = cells.get(day);
        		Elements courseCells = cell.getElementsByClass("ddlabel");
        		if (courseCells.size() < 1) {
            		continue;
            	}
        		Elements cellContent = courseCells.first().getElementsByTag("a");
        		Element courseCell = cellContent.first();
            	String courseHtml = courseCell.html();
            	String courseString = courseHtml.replaceAll("<br />", ",");
            	String[] attributes = courseString.split(",");
            	String courseCode = attributes[0];
            	String room = attributes[3];
            	int crn = Integer.parseInt(attributes[1].split(" ")[0]);
            	String[] times = attributes[2].split("-");
            	int startHour = Integer.parseInt(times[0].split(" ")[0].split(":")[0]);
            	int startMinute = Integer.parseInt(times[0].split(" ")[0].split(":")[1]);
            	int endHour = Integer.parseInt(times[1].split(" ")[0].split(":")[0]);
            	int endMinute = Integer.parseInt(times[1].split(" ")[0].split(":")[1]);
            	CourseSched schedule = new CourseSched(crn, courseCode, day, startHour, startMinute, endHour, endMinute, room);
            	courseList.add(schedule);
        	}
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
            InputStream inputStream = getResources().openRawResource(R.raw.minsched);;

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
}