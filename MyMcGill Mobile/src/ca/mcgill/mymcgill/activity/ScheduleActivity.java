package ca.mcgill.mymcgill.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;
import ca.mcgill.mymcgill.R;

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
        
        String fileContent = readFromFile("minsched.html");
        
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(fileContent);

        // Set the text view as the activity layout
        setContentView(textView);
        
        
        
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

        String ret = "blank";

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
        	
        	  String path = "."; 
        	  
        	  String files;
        	  File folder = getFilesDir();
        	  File[] listOfFiles = folder.listFiles(); 
        	 
        	  for (int i = 0; i < listOfFiles.length; i++) 
        	  {
        	 
        	   if (listOfFiles[i].isFile()) 
        	   {
        	   files = listOfFiles[i].getName();
        	   System.out.println(files);
        	      }
        	  }
        	
        	return ret;
        }

        //return ret;
    }
}