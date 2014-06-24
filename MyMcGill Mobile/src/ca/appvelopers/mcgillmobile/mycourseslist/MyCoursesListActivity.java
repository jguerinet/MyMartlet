package ca.appvelopers.mcgillmobile.mycourseslist;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.ChangeSemesterActivity;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.downloader.ClassDownloader;
import ca.appvelopers.mcgillmobile.util.downloader.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author : Julien
 * Date :  2014-06-13 7:39 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class MyCoursesListActivity extends DrawerActivity {
    public static final int CHANGE_SEMESTER_CODE = 100;

    private ListView mListView;
    private TextView mUnregisterButton;

    private MyCoursesAdapter mAdapter;
    private Term mTerm;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_courseslist);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "View Courses");

        // Views
        mListView = (ListView)findViewById(R.id.courses_list);
        mListView.setEmptyView(findViewById(R.id.courses_empty));

        mTerm = App.getDefaultTerm();

        //Register button
        mUnregisterButton = (TextView)findViewById(R.id.course_register);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get checked courses from adapter
                List<ClassItem> registerCoursesList = mAdapter.getCheckedClasses();

                //Too many courses
                if (registerCoursesList.size() > 10) {
                    Toast.makeText(MyCoursesListActivity.this, getResources().getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                } else if (registerCoursesList.isEmpty()) {
                    Toast.makeText(MyCoursesListActivity.this, getResources().getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                } else if (registerCoursesList.size() > 0) {
                    //Execute unregistration of checked classes in a new thread
                    new UnregistrationThread(Connection.getRegistrationURL(mTerm, registerCoursesList, true)).execute();
                }
            }
        });

        //Remove this button
        TextView wishlist = (TextView)findViewById(R.id.course_wishlist);
        wishlist.setVisibility(View.GONE);

        executeClassDownloader();

        //Download the Transcript (if ever the user has new semesters on his transcript
        new TranscriptDownloader(this) {
            @Override
            protected void onPreExecute() {}
            @Override
            protected void onPostExecute(Boolean loadInfo) {}
        }.execute();
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    private void loadInfo(){
        //Set the title
        setTitle(mTerm.toString(this));

        boolean canUnregister = App.getRegisterTerms().contains(mTerm);

        //Change the text and the visibility if we are in the list of currently registered courses
        View line = findViewById(R.id.course_line);
        if(canUnregister){
            line.setVisibility(View.VISIBLE);
            mUnregisterButton.setVisibility(View.VISIBLE);
            mUnregisterButton.setText(getString(R.string.courses_unregister));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mUnregisterButton.setLayoutParams(params);
        }
        else{
            line.setVisibility(View.GONE);
            mUnregisterButton.setVisibility(View.GONE);
        }

        mAdapter = new MyCoursesAdapter(this, mTerm, canUnregister);
        mListView.setAdapter(mAdapter);
    }

    // JDAlfaro
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.refresh, menu);
        //Change Semester Menu Item
        menu.add(Menu.NONE, Constants.MENU_ITEM_CHANGE_SEMESTER, Menu.NONE, R.string.schedule_change_semester);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == Constants.MENU_ITEM_CHANGE_SEMESTER){
            Intent intent = new Intent(this, ChangeSemesterActivity.class);
            intent.putExtra(Constants.TERM, mTerm);
            startActivityForResult(intent, CHANGE_SEMESTER_CODE);
            return true;
        }
        else if(item.getItemId() == R.id.action_refresh){
            //Execute the class downloader
            executeClassDownloader();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CHANGE_SEMESTER_CODE){
            if(resultCode == RESULT_OK){
                mTerm = (Term)data.getSerializableExtra(Constants.TERM);
                executeClassDownloader();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void executeClassDownloader(){
        new ClassDownloader(this, mTerm) {
            @Override
            protected void onPreExecute() {
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected void onPostExecute(Boolean loadInfo) {
                if(loadInfo){
                    loadInfo();
                }

                setProgressBarIndeterminateVisibility(false);
            }
        }.execute();
    }

    //Connects to Minerva in a new thread to register for courses
    private class UnregistrationThread extends AsyncTask<Void, Void, Boolean> {
        private String mRegistrationURL;
        private Map<String, String> mRegistrationErrors;

        public UnregistrationThread(String registrationURL){
            this.mRegistrationURL = registrationURL;
            this.mRegistrationErrors = null;
        }

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve page that contains registration status from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            String resultString = Connection.getInstance().getUrl(MyCoursesListActivity.this, mRegistrationURL);

            //If result string is null, there was an error
            if(resultString == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Activity activity = MyCoursesListActivity.this;
                        try {
                            DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                    activity.getResources().getString(R.string.error_other));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
            //Otherwise, check for errors
            else{
                mRegistrationErrors = Parser.parseRegistrationErrors(resultString);
                return true;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean success){
            setProgressBarIndeterminateVisibility(false);

            if(success){
                //Display whether the user was successfully registered
                for(String CRN : mRegistrationErrors.keySet()){
                    Log.e("REGERR", CRN);
                }
                for(String error : mRegistrationErrors.values()){
                    Log.e("REGERR", error);
                }

                if(mRegistrationErrors == null || mRegistrationErrors.isEmpty()){
                    Toast.makeText(MyCoursesListActivity.this, R.string.unregistration_success, Toast.LENGTH_LONG).show();
                }

                //Display a message if a registration error has occurred
                else{
                    Toast.makeText(MyCoursesListActivity.this, getResources().getString(R.string.unregistration_error, mRegistrationErrors), Toast.LENGTH_LONG).show();
                }
            }

            executeClassDownloader();
        }
    }
}