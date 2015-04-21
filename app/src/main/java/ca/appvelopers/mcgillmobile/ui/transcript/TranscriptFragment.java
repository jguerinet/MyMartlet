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

package ca.appvelopers.mcgillmobile.ui.transcript;

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
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.thread.TranscriptDownloader;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
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
        final TranscriptDownloader downloader = new TranscriptDownloader(mActivity, false);
        downloader.start();

        //Show the user we are downloading new info
        mActivity.showToolbarProgress(true);

        //Wait for the downloader to finish
        synchronized(downloader){
            try{
                downloader.wait();
            } catch(InterruptedException e){}
        }

        mTranscript = App.getTranscript();

        if(downloader.success()){
            //Reload the info in the views
            loadInfo();
        }

        mActivity.showToolbarProgress(false);
    }
}