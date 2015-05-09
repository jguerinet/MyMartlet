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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Shows the user's transcript
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class TranscriptFragment extends BaseFragment{
    /**
     * The user's CGPA
     */
    @InjectView(R.id.transcript_cgpa)
    TextView mCGPA;
    /**
     * The user's total credits
     */
    @InjectView(R.id.transcript_credits)
    TextView mTotalCredits;
    /**
     * The list of semesters
     */
    @InjectView(android.R.id.list)
    RecyclerView mListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = View.inflate(mActivity, R.layout.fragment_transcript, null);
        ButterKnife.inject(this, view);
        lockPortraitMode();
        Analytics.getInstance().sendScreen("Transcript");
        mActivity.setTitle(getString(R.string.title_transcript));

        mListView.setLayoutManager(new LinearLayoutManager(mActivity));

        //Load the info stored on the device
        loadInfo();

        //Hide the loading indicator
        hideLoadingIndicator();

        return view;
    }

    private void loadInfo(){
        Transcript transcript = App.getTranscript();

        //Reload all of the info
        mCGPA.setText(getString(R.string.transcript_CGPA, transcript.getCgpa()));
        mTotalCredits.setText(getString(R.string.transcript_credits, transcript.getTotalCredits()));
        mListView.setAdapter(new TranscriptAdapter(transcript.getSemesters()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mActivity.showToolbarProgress(true);
                new DownloaderThread(mActivity, "Transcript Downloader", Connection.TRANSCRIPT_URL)
                        .execute(new DownloaderThread.Callback() {
                            @Override
                            public void onDownloadFinished(final String result){
                                //Parse the transcript if possible
                                if(result != null){
                                    Parser.parseTranscript(result);
                                }

                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run(){
                                        if(result != null){
                                            loadInfo();
                                        }
                                        mActivity.showToolbarProgress(false);
                                    }
                                });
                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}