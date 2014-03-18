package ca.mcgill.mymcgill.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.ApplicationClass;


/**
 * Author: JDA
 * Date: 15/03/14
 */
public class ChangeSemesterActivity extends BaseActivity {

	private List<Semester> semesters = new ArrayList<Semester>();
	private List<String> seasonList;
	private List<String> yearList;
	private String seasonNum;
	private String yearNum;
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_top, R.anim.stay);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_semester);
    	
        //Get the screen height
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            size.set(display.getWidth(), display.getHeight());
        }
        else{
            display.getSize(size);
        }

        int displayWidth = size.x;
        int displayHeight = size.y;

        //Set the width and height to 2/3 of the screen
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_change_semester);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        //Quick check
        assert (params != null);
        params.height = (2 * displayHeight) / 3;
        params.width = (5 * displayWidth) / 6;
        layout.setLayoutParams(params);

        // Extract all the seasons that the user has registered in
        semesters = ApplicationClass.getTranscript().getSemesters();
        seasonList = new ArrayList<String>();
        String seasonName;
        Boolean old;
        for(int i = 0; i < semesters.size(); i = i + 1) {
        	old = false;
        	seasonName = semesters.get(i).getSemesterName().replaceAll("[^A-Za-z]+", "");
        	for(int j = 0; j < seasonList.size();j = j + 1) {
        		if(seasonName.equals(seasonList.get(j))){
        			old = true;
        		}
        	}
        	if (!old) {
        		seasonList.add(seasonName);
        	}
        }
        
        Spinner season = (Spinner) findViewById(R.id.change_semester_season);
    	
    	//Standard ArrayAdapter
        ArrayAdapter<String> seasonAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, seasonList);
        //Specify the layout to use when the list of choices appears
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        season.setAdapter(seasonAdapter);
        // TODO: Need to be better done
        season.setSelection(0);
        season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            	if (seasonList.get(position).equals("Fall")) {
                	seasonNum = "09";
                } else if (seasonList.get(position).equals("Fall")) {
                	seasonNum = "01";
                } else {
                	seasonNum = "05";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        
        // Extract all the years that the user has registered in
        yearList = new ArrayList<String>();
        String yearName;
        for(int i = 0; i < semesters.size(); i = i + 1) {
        	old = false;
        	yearName = semesters.get(i).getSemesterName().replaceAll("\\D+","");;
        	for(int j = 0; j < yearList.size();j = j + 1) {
        		if(yearName.equals(yearList.get(j))){
        			old = true;
        		}
        	}
        	if (!old) {
        		yearList.add(yearName);
        	}
        }
        
        Spinner year = (Spinner) findViewById(R.id.change_semester_year);
    	
    	//Standard ArrayAdapter
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearList);
        //Specify the layout to use when the list of choices appears
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        year.setAdapter(yearAdapter);
        // TODO: Need to be better done
        year.setSelection(0);
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            	seasonNum = yearList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public void cancelPress(View v){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
    }
    
    public void okPress(View v){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
    }
}
