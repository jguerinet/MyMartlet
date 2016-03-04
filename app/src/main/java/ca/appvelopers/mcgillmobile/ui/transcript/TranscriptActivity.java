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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.TranscriptManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
    /**
     * {@link TranscriptManager} instance
     */
    @Inject
    protected TranscriptManager transcriptManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        analytics.sendScreen("Transcript");

        mList.setLayoutManager(new LinearLayoutManager(this));
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
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
     * Refreshes the transcript
     */
    private void refresh() {
        if (!canRefresh()) {
            return;
        }

        mcGillService.transcript().enqueue(new Callback<Transcript>() {
            @Override
            public void onResponse(Call<Transcript> call, Response<Transcript> response) {
                transcriptManager.set(response.body());
                update();
                showToolbarProgress(false);
            }

            @Override
            public void onFailure(Call<Transcript> call, Throwable t) {
                Timber.e(t, "Error refreshing transcript");
                showToolbarProgress(false);
                //If this is a MinervaException, broadcast it
                if (t instanceof MinervaException) {
                    LocalBroadcastManager.getInstance(TranscriptActivity.this)
                            .sendBroadcast(new Intent(Constants.BROADCAST_MINERVA));
                } else {
                    DialogHelper.error(TranscriptActivity.this, R.string.error_other);
                }
            }
        });
    }

    /**
     * Updates the view
     */
    private void update() {
        //Reload all of the info
        mCGPA.setText(getString(R.string.transcript_CGPA, transcriptManager.get().getCGPA()));
        mTotalCredits.setText(getString(R.string.transcript_credits,
                transcriptManager.get().getTotalCredits()));
        mList.setAdapter(new TranscriptAdapter(transcriptManager.get().getSemesters()));
    }
}