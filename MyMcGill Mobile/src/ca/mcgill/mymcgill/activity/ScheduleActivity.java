package ca.mcgill.mymcgill.activity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import ca.mcgill.mymcgill.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import ca.mcgill.mymcgill.R;

import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Constants;
/**
 * Author: Shabbir
 * Date: 22/01/14, 9:07 PM
 * 
 * This Activity loads the schedule from https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchd
 */
public class ScheduleActivity extends Activity {
	
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
        
        //get the week and schedule table
        Document doc = Jsoup.parse(fileContent);
        Elements tables = doc.getElementsByTag("table");
        
        //check if correct amount of tables appeared
        if(tables.size()<7){
        	//TODO: handle parse Error
        }
        else{
	        Element week = tables.get(6);
	        Element schedule = tables.get(7);
	        
	        //display week on screen
	        displayWeek(week);
	        
	        //display schedule on screen
	        displaySchedule(schedule);        
        }
        
        TextView textView = new TextView(this);
        String htmlAsAString = Connection.getInstance().getUrl(Connection.minervaSchedule);
        textView.setText(Html.fromHtml(htmlAsAString));
        //textView.setText("hell0");
        setContentView(textView);
		// Show the Up button in the action bar.
		setupActionBar();
        
        
    }
    
    //displays the current week in the TextView element
    private void displayWeek(Element table){
    	String tableText = table.toString();
    	
    	String pat = "(Week of.*?)<";
		  Pattern p = Pattern.compile(pat);
		  Matcher m = p.matcher(tableText);

		  //If found change text
		  if (m.find()) {
			  
			  //set text to extract week value
		      String week = m.group(1);
		      TextView timeIndicator = (TextView)findViewById(R.id.timeIndicator);
		      timeIndicator.setText(week);
		  }
    	
    }
    
    //populates the table element with schedule information
    private void displaySchedule(Element table){
    	
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


	

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}




}