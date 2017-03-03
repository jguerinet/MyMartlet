/*
 * Copyright 2014-2017 Julien Guerinet
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
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.TranscriptDB;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.retrofit.TranscriptConverter.TranscriptResponse;
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
    @BindView(R.id.transcript_cgpa)
    TextView cgpa;
    /**
     * User's total credits
     */
    @BindView(R.id.transcript_credits)
    TextView totalCredits;
    /**
     * List of semesters
     */
    @BindView(android.R.id.list)
    RecyclerView list;
    /**
     * Adapter used for the list of semesters
     */
    private TranscriptAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        analytics.sendScreen("Transcript");

        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TranscriptAdapter();
        list.setAdapter(adapter);
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @HomepageManager.Homepage
    protected int getCurrentPage() {
        return HomepageManager.TRANSCRIPT;
    }

    /**
     * Refreshes the transcript
     */
    private void refresh() {
        if (!canRefresh()) {
            return;
        }

        mcGillService.transcript().enqueue(new Callback<TranscriptResponse>() {
            @Override
            public void onResponse(Call<TranscriptResponse> call,
                    Response<TranscriptResponse> response) {
                TranscriptDB.saveTranscript(TranscriptActivity.this, response.body());
                update();
                showToolbarProgress(false);
            }

            @Override
            public void onFailure(Call<TranscriptResponse> call, Throwable t) {
                Timber.e(t, "Error refreshing transcript");
                showToolbarProgress(false);
                Help.handleException(TranscriptActivity.this, t);
            }
        });
    }

    /**
     * Updates the view
     */
    private void update() {
        // Reload all of the info
        Transcript transcript = SQLite.select().from(Transcript.class).querySingle();
        if (transcript != null) {
            cgpa.setText(getString(R.string.transcript_CGPA, String.valueOf(transcript.getCGPA())));
            totalCredits.setText(getString(R.string.transcript_credits, String.valueOf(
                    transcript.getTotalCredits())));
        }
        adapter.update();
    }
}