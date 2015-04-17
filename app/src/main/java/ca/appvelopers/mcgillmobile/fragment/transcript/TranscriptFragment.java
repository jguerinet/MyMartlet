package ca.appvelopers.mcgillmobile.fragment.transcript;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.fragment.BaseFragment;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.thread.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.util.Analytics;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 4:32 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class TranscriptFragment extends BaseFragment{
    private Transcript mTranscript;
    private TextView mCGPA, mTotalCredits;
    private ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        lockPortraitMode();

        View view = View.inflate(mActivity, R.layout.fragment_transcript, null);

        //Title
        mActivity.setTitle(getString(R.string.title_transcript));

        Analytics.getInstance().sendScreen("Transcript");

        //Get the stored transcript from the App
        mTranscript = App.getTranscript();

        //Get the views
        mCGPA = (TextView)view.findViewById(R.id.transcript_cgpa);
        mTotalCredits = (TextView)view.findViewById(R.id.transcript_credits);
        mListView = (ListView)view.findViewById(android.R.id.list);

        //Load the info stored on the device
        loadInfo();

        //Hide the loading indicator
        hideLoadingIndicator();

        return view;
    }

    private void loadInfo(){
        //Fill out the transcript info
        mCGPA.setText(getResources().getString(R.string.transcript_CGPA, mTranscript.getCgpa()));
        mTotalCredits.setText(getResources().getString(R.string.transcript_credits, mTranscript.getTotalCredits()));

        //Reload the adapter
        TranscriptAdapter adapter = new TranscriptAdapter(mActivity, mTranscript);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                //Start thread to retrieve inbox
                executeTranscriptDownloader();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeTranscriptDownloader(){
        new TranscriptDownloader(mActivity) {
            @Override
            protected void onPreExecute() {
                //Show the user we are downloading new info
                mActivity.showToolbarProgress(true);
            }

            @Override
            protected void onPostExecute(Boolean loadInfo) {
                mTranscript = App.getTranscript();

                if(loadInfo){
                    //Reload the info in the views
                    loadInfo();
                }
                mActivity.showToolbarProgress(false);
            }
        }.execute();
    }
}