/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.ui.courses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.thread.ClassDownloader;
import ca.appvelopers.mcgillmobile.thread.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.ui.ChangeSemesterDialog;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.view.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class CoursesFragment extends BaseFragment {
    private ListView mListView;
    private TextView mUnregisterButton;
    private CoursesAdapter mAdapter;
    private Term mTerm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = View.inflate(mActivity, R.layout.fragment_wishlist, null);

        lockPortraitMode();

        Analytics.getInstance().sendScreen("View Courses");

        // Views
        mListView = (ListView)view.findViewById(R.id.courses_list);
        mListView.setEmptyView(view.findViewById(R.id.courses_empty));

        mTerm = App.getDefaultTerm();

        //Register button
        mUnregisterButton = (TextView)view.findViewById(R.id.course_register);
        mUnregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get checked courses from adapter
                final List<Course> unregisterCoursesList = mAdapter.getCheckedClasses();

                //Too many courses
                if (unregisterCoursesList.size() > 10) {
                    Toast.makeText(mActivity, getString(R.string.courses_too_many_courses),
                            Toast.LENGTH_SHORT).show();
                } else if (unregisterCoursesList.isEmpty()) {
                    Toast.makeText(mActivity, getString(R.string.courses_none_selected),
                            Toast.LENGTH_SHORT).show();
                } else if (unregisterCoursesList.size() > 0) {

                    //Ask for confirmation before unregistering
                    new AlertDialog.Builder(mActivity)
                            .setTitle(getString(R.string.unregister_dialog_title))
                            .setMessage(getString(R.string.unregister_dialog_message))
                            .setPositiveButton(getString(R.string.unregister_dialog_positive),
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Execute unregistration of checked classes in a new thread
                                    new UnregistrationThread(unregisterCoursesList).execute();
                                }
                            })
                            .setNegativeButton(getString(R.string.logout_dialog_negative), null)
                            .create()
                            .show();
                }
            }
        });

        //Remove this button
        TextView wishlist = (TextView)view.findViewById(R.id.course_wishlist);
        wishlist.setVisibility(View.GONE);

        //Hide the loading indicator
        hideLoadingIndicator();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        loadInfo();
    }

    private void loadInfo(){
        //Set the title
        mActivity.setTitle(mTerm.toString(mActivity));

        boolean canUnregister = App.getRegisterTerms().contains(mTerm);

        //Change the text and the visibility if we are in the list of currently registered courses
        if(canUnregister){
            mUnregisterButton.setVisibility(View.VISIBLE);
            mUnregisterButton.setText(getString(R.string.courses_unregister));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mUnregisterButton.setLayoutParams(params);
        }
        else{
            mUnregisterButton.setVisibility(View.GONE);
        }

        mAdapter = new CoursesAdapter(mActivity, mTerm, canUnregister);
        mListView.setAdapter(mAdapter);
    }

    // JDAlfaro
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh_change_semester, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_change_semester){
            final ChangeSemesterDialog dialog = new ChangeSemesterDialog(mActivity, false, mTerm);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Term term = dialog.getTerm();

                    //Term selected: download the classes for the selected term
                    if(term != null){
                        mTerm = term;
                        executeClassDownloader();
                    }
                }
            });
            dialog.show();
            return true;
        }
        else if(item.getItemId() == R.id.action_refresh){
            //Execute the class downloader
            executeClassDownloader();

            //Download the Transcript (if ever the user has new semesters on their transcript)
            final TranscriptDownloader downloader = new TranscriptDownloader(mActivity, false);
            downloader.start();

            //Wait for the downloader to finish
            synchronized(downloader){
                try{
                    downloader.wait();
                } catch(InterruptedException e){}
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void executeClassDownloader(){
        //Show the user we are refreshing
        mActivity.showToolbarProgress(true);

        final ClassDownloader downloader = new ClassDownloader(mActivity, mTerm);
        downloader.start();

        //Wait for the downloader to finish
        synchronized(downloader){
            try{
                downloader.wait();
            } catch(InterruptedException e){}
        }

        mActivity.showToolbarProgress(false);

        if(downloader.success()){
            loadInfo();
        }
    }

    //Connects to Minerva in a new thread to register for courses
    private class UnregistrationThread extends AsyncTask<Void, Void, Boolean> {
        private Map<String, String> mRegistrationErrors;
        private List<Course> mClasses;

        public UnregistrationThread(List<Course> classItems){
            this.mClasses = classItems;
        }

        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            mActivity.showToolbarProgress(true);
        }

        //Retrieve page that contains registration status from Minerva
        @Override
        protected Boolean doInBackground(Void... params){
            try{
                String resultString = Connection.getInstance().get(Connection.getRegistrationURL(
                        mTerm, mClasses, true));
                mRegistrationErrors = Parser.parseRegistrationErrors(resultString);
                return true;
            } catch(MinervaLoggedOutException e){
                e.printStackTrace();
                //TODO Receiver stuff
                return false;
            } catch(Exception e){
                return false;
            }
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean success){
            mActivity.showToolbarProgress(false);

            if(success){
                //Display whether the user was successfully registered
                for(String CRN : mRegistrationErrors.keySet()){
                    Log.e("REGERR", CRN);
                }
                for(String error : mRegistrationErrors.values()){
                    Log.e("REGERR", error);
                }

                if(mRegistrationErrors == null || mRegistrationErrors.isEmpty()){
                    Toast.makeText(mActivity, R.string.unregistration_success, Toast.LENGTH_LONG).show();
                }

                //Display a message if a registration error has occurred
                else{
                    String errorMessage = "";
                    for(String crn : mRegistrationErrors.keySet()){
                        //Find the corresponding course
                        for(Course classItem : mClasses){
                            if(classItem.getCRN() == Integer.valueOf(crn)){
                                //Add this class to the error message
                                errorMessage += classItem.getCode() +  " ("
                                        + classItem.getType() + ") - " + mRegistrationErrors.get(crn) + "\n";

                                break;
                            }
                        }
                    }

                    //Show an alert dialog with the errors
                    DialogHelper.showNeutralAlertDialog(mActivity, getString(R.string.unregistration_error),
                            errorMessage);
                }
            }
            else{
                DialogHelper.showNeutralAlertDialog(mActivity, getString(R.string.error),
                        getString(R.string.error_other));
            }

            executeClassDownloader();
        }
    }
}