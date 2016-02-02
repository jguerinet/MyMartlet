/*
 * Copyright 2014-2016 Appvelopers
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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.guerinet.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Shows the user's transcript
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class TranscriptActivity extends DrawerActivity {
    /**
     * User's CGPA
     */
    @Bind(R.id.transcript_cgpa)
    protected TextView mCGPA;
    /**
     * User's total credits
     */
    @Bind(R.id.transcript_credits)
    protected TextView mTotalCredits;
    /**
     * List of semesters
     */
    @Bind(android.R.id.list)
    protected RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);
        ButterKnife.bind(this);
        Analytics.get().sendScreen("Transcript");

        mList.setLayoutManager(new LinearLayoutManager(this));
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.refresh, menu);
        Utils.setTint(menu.findItem(R.id.action_refresh).getIcon(), Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                showToolbarProgress(true);
                new DownloaderThread(this, Connection.TRANSCRIPT_URL)
                        .execute(new DownloaderThread.Callback() {
                            @Override
                            public void onDownloadFinished(final String result) {
                                //Parse the transcript if possible
                                if (result != null) {
                                    Parser.parseTranscript(result);
                                }

                                //Reload the view
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result != null) {
                                            update();
                                        }
                                        showToolbarProgress(false);
                                    }
                                });
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.TRANSCRIPT;
    }

    /**
     * Updates the view
     */
    private void update() {
        Transcript transcript = App.getTranscript();

        //Reload all of the info
        mCGPA.setText(getString(R.string.transcript_CGPA, transcript.getCGPA()));
        mTotalCredits.setText(getString(R.string.transcript_credits, transcript.getTotalCredits()));
        mList.setAdapter(new TranscriptAdapter(transcript.getSemesters()));
    }
}