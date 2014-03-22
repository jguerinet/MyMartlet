package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.DialogHelper;


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
	
	private Boolean invalid;
	
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

        //Set the width to 2/3 of the screen
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_change_semester);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        //Quick check
        assert (params != null);
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
                } else if (seasonList.get(position).equals("Winter")) {
                	seasonNum = "01";
                } else {
                	seasonNum = "05";
                }
            	new ScheduleChecker(true,Connection.minervaSchedulePrefix+yearNum+seasonNum).execute();
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
            	yearNum = yearList.get(position);
            	new ScheduleChecker(true,Connection.minervaSchedulePrefix+yearNum+seasonNum).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    // Cancel Button
    // Simply closes the dialog
    public void cancelPress(View v){
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
    }
    
    // Ok Button
    // Accepts changes and changes the schedule
    public void okPress(View v){
		if (invalid) {
			DialogHelper.showNeutralAlertDialog(this, this
					.getResources().getString(R.string.error), "You are not currently registered for the term selected");
		} else {
			Intent replyIntent = new Intent();
			replyIntent.putExtra("season", seasonNum);
			replyIntent.putExtra("year", yearNum);
            setResult(RESULT_OK, replyIntent);
            finish();
            overridePendingTransition(R.anim.stay, R.anim.out_to_top);
		}
	}
    
    private class ScheduleChecker extends AsyncTask<Void, Void, Void> {
        private boolean mRefresh;
        private ProgressDialog mProgressDialog;
        private String scheduleURL;

        public ScheduleChecker(boolean refresh, String url){
            mRefresh = refresh;
            scheduleURL = url;
        }

        @Override
        protected void onPreExecute(){
            //Only show a ProgressDialog if we are not refreshing the content but
            //downloading it for the first time
            if(!mRefresh){
                mProgressDialog = new ProgressDialog( ChangeSemesterActivity.this);
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
            String scheduleString;
            final Activity activity = ChangeSemesterActivity.this;
            
  			scheduleString = Connection.getInstance().getUrl(ChangeSemesterActivity.this, scheduleURL);
  			
            if(scheduleString == null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                activity.getResources().getString(R.string.login_error_other));
                    }
                });
                return null;
            }
            //Empty String: no need for an alert dialog but no need to reload
            else if(TextUtils.isEmpty(scheduleString)){
                return null;
            }
            
            Document doc = Jsoup.parse(scheduleString);
            String all = doc.text();
            if(all.contains("You are not currently registered for the term.")) {
            	invalid = true;
            } else {
            	invalid = false;
            }

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
    }
}
