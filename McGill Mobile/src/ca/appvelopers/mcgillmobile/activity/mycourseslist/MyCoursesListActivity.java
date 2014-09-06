package ca.appvelopers.mcgillmobile.activity.mycourseslist;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
                final List<ClassItem> unregisterCoursesList = mAdapter.getCheckedClasses();

                //Too many courses
                if (unregisterCoursesList.size() > 10) {
                    Toast.makeText(MyCoursesListActivity.this, getResources().getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                } else if (unregisterCoursesList.isEmpty()) {
                    Toast.makeText(MyCoursesListActivity.this, getResources().getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                } else if (unregisterCoursesList.size() > 0) {

                    //Ask for confirmation before unregistering
                    new AlertDialog.Builder(MyCoursesListActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(MyCoursesListActivity.this.getResources().getString(R.string.unregister_dialog_title))
                            .setMessage(MyCoursesListActivity.this.getResources().getString(R.string.unregister_dialog_message))
                            .setPositiveButton(MyCoursesListActivity.this.getResources().getString(R.string.unregister_dialog_positive), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Execute unregistration of checked classes in a new thread
                                    new UnregistrationThread(unregisterCoursesList).execute();
                                }
                            })
                            .setNegativeButton(MyCoursesListActivity.this.getResources().getString(R.string.logout_dialog_negative), null)
                            .create()
                            .show();


                }
            }
        });

        //Remove this button
        TextView wishlist = (TextView)findViewById(R.id.course_wishlist);
        wishlist.setVisibility(View.GONE);
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
        getMenuInflater().inflate(R.menu.refresh_change_semester, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_change_semester){
            Intent intent = new Intent(this, ChangeSemesterActivity.class);
            intent.putExtra(Constants.TERM, mTerm);
            startActivityForResult(intent, CHANGE_SEMESTER_CODE);
            return true;
        }
        else if(item.getItemId() == R.id.action_refresh){
            //Execute the class downloader
            executeClassDownloader();

            //Download the Transcript (if ever the user has new semesters on their transcript)
            new TranscriptDownloader(this) {
                @Override
                protected void onPreExecute() {}
                @Override
                protected void onPostExecute(Boolean loadInfo) {}
            }.execute();
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
        private Map<String, String> mRegistrationErrors;
        private List<ClassItem> mClasses;

        public UnregistrationThread(List<ClassItem> classItems){
            this.mClasses = classItems;
        }

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve page that contains registration status from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            String resultString = Connection.getInstance().getUrl(MyCoursesListActivity.this,
                    Connection.getRegistrationURL(mTerm, mClasses, true));

            //If result string is null, there was an error
            if(resultString == null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            DialogHelper.showNeutralAlertDialog(MyCoursesListActivity.this, getString(R.string.error),
                                    getString(R.string.error_other));
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
                    String errorMessage = "";
                    for(String crn : mRegistrationErrors.keySet()){
                        //Find the corresponding course
                        for(ClassItem classItem : mClasses){
                            if(classItem.getCRN() == Integer.valueOf(crn)){
                                //Add this class to the error message
                                errorMessage += classItem.getCourseCode() +  " ("
                                        + classItem.getSectionType() + ") - " + mRegistrationErrors.get(crn) + "\n";

                                break;
                            }
                        }
                    }

                    //Show an alert dialog with the errors
                    DialogHelper.showNeutralAlertDialog(MyCoursesListActivity.this, getString(R.string.unregistration_error),
                            errorMessage);
                }
            }

            executeClassDownloader();
        }
    }
}