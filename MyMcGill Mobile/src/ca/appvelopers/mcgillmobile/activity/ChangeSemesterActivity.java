package ca.appvelopers.mcgillmobile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.object.Season;
import ca.appvelopers.mcgillmobile.object.Semester;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Help;


/**
 * Author: JDA
 * Date: 15/03/14
 */
public class ChangeSemesterActivity extends BaseActivity {

    private List<Season> mSeasonList;
	private List<Integer> mYearList;
    private List<Semester> mSemesters;
	private Season mSeason;
	private int mYear;
    private CheckBox mDefaultCheckbox;

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_top, R.anim.stay);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_semester);
    	
        //Get the screen width
        int displayWidth = Help.getDisplayWidth(getWindowManager().getDefaultDisplay());

        //Set the width to 2/3 of the screen
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_change_semester);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        //Quick check
        assert (params != null);
        params.width = (5 * displayWidth) / 6;
        layout.setLayoutParams(params);

        //Get the current default semester
        Semester defaultSemester = App.getDefaultSemester();

        // Extract all the seasons that the user has registered in
        mSemesters = App.getTranscript().getSemesters();
        mSeasonList = new ArrayList<Season>();
        for (Semester semester : mSemesters) {
            if(!mSeasonList.contains(semester.getSeason())){
                mSeasonList.add(semester.getSeason());
            }
        }

        //Order them alphabetically
        Collections.sort(mSeasonList, new Comparator<Season>() {
            @Override
            public int compare(Season season, Season season2) {
                return season.toString(ChangeSemesterActivity.this).compareTo(season2.toString(ChangeSemesterActivity.this));
            }
        });

        //Make a list with their strings
        List<String> seasonStrings = new ArrayList<String>();
        for(Season season : mSeasonList){
            seasonStrings.add(season.toString(this));
        }

        //Set up the season adapter
        ArrayAdapter<String> seasonAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, seasonStrings);
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set up the season spinner
        Spinner season = (Spinner) findViewById(R.id.change_semester_season);
        season.setAdapter(seasonAdapter);
        season.setSelection(mSeasonList.indexOf(defaultSemester.getSeason()));
        season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            	//Get the selected season
                mSeason = mSeasonList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        
        // Extract all the years that the user has registered in
        mYearList = new ArrayList<Integer>();
        for (Semester semester : mSemesters) {
            if(!mYearList.contains(semester.getYear())){
                mYearList.add(semester.getYear());
            }
        }

        //Order them chronologically
        Collections.sort(mYearList);

        //Get a list with their strings
        List<String> yearStrings = new ArrayList<String>();
        for(Integer year : mYearList){
            yearStrings.add(String.valueOf(year));
        }

        //Set up the year adapter
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearStrings);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set up the year spinner
        Spinner year = (Spinner) findViewById(R.id.change_semester_year);
        year.setAdapter(yearAdapter);
        year.setSelection(mYearList.indexOf(defaultSemester.getYear()));
        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            	mYear = mYearList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Set up the default checkbox
        mDefaultCheckbox = (CheckBox)findViewById(R.id.change_semester_default);
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
        //Find the chosen semester
        Semester semester = findChosenSemester();

        if(semester == null){
            DialogHelper.showNeutralAlertDialog(ChangeSemesterActivity.this, ChangeSemesterActivity.this
                    .getResources().getString(R.string.error), "You are not currently registered for the term selected");
        }
        else{
            //Check that we found a valid schedule
            new ScheduleChecker(semester).execute();
        }
	}

    private Semester findChosenSemester(){
        //Find the semester given a season and year
        for(Semester semester : mSemesters){
            if(semester.getYear() == mYear && semester.getSeason() == mSeason){
                return semester;
            }
        }
        return null;
    }
    
    private class ScheduleChecker extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog mProgressDialog;
        private Semester mSemester;

        public ScheduleChecker(Semester semester){
            this.mSemester = semester;
        }

        @Override
        protected void onPreExecute(){
            //Show a progress dialog
            mProgressDialog = new ProgressDialog( ChangeSemesterActivity.this);
            mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final Activity activity = ChangeSemesterActivity.this;
  			String scheduleString = Connection.getInstance().getUrl(ChangeSemesterActivity.this, mSemester.getURL());
  			
            if(scheduleString == null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                activity.getResources().getString(R.string.error_other));
                    }
                });
                return false;
            }
            //Empty String: no need for an alert dialog but no need to reload
            else if(TextUtils.isEmpty(scheduleString)){
                return false;
            }
            
            Document doc = Jsoup.parse(scheduleString);
            String all = doc.text();

            return !all.contains("You are not currently registered for the term.");
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Boolean valid) {
            //Dismiss the progress dialog if there was one
            mProgressDialog.dismiss();

            if(valid){
                //Check if the default checkbox is checked
                if(mDefaultCheckbox.isChecked()){
                    //Store this semester as the default semester if it is
                    App.setDefaultSemester(mSemester);
                }

                Intent replyIntent = new Intent();
                replyIntent.putExtra(Constants.SEMESTER, mSemester);
                setResult(RESULT_OK, replyIntent);
                finish();
                overridePendingTransition(R.anim.stay, R.anim.out_to_top);
            }
            //This shouldn't happen
            else {
                Log.e("Change Semester", "This shouldn't happen");
                DialogHelper.showNeutralAlertDialog(ChangeSemesterActivity.this, ChangeSemesterActivity.this
                        .getResources().getString(R.string.error), "You are not currently registered for the term selected");
            }
        }
    }
}
