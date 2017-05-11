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

package com.guerinet.mymartlet.ui.transcript.semester;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.Semester;
import com.guerinet.mymartlet.model.Semester_Table;
import com.guerinet.mymartlet.ui.BaseActivity;
import com.guerinet.mymartlet.ui.dialog.DialogHelper;
import com.guerinet.mymartlet.util.Constants;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Displays information about a semester from the user's transcript
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class SemesterActivity extends BaseActivity {
    /**
     * Semester's bachelor degree
     */
    @BindView(R.id.semester_bachelor)
    TextView bachelor;
    /**
     * Semester program
     */
    @BindView(R.id.semester_program)
    TextView program;
    /**
     * Semester GPA
     */
    @BindView(R.id.semester_GPA)
    TextView gpa;
    /**
     * Semester credits
     */
    @BindView(R.id.semester_credits)
    TextView credits;
    /**
     * User's status during this semester
     */
    @BindView(R.id.semester_full_time)
    TextView fullTime;
    /**
     * Courses taken during this semester
     */
    @BindView(android.R.id.list)
    RecyclerView list;
    /**
     * Adapter for the list of courses
     */
    private SemesterAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);
        ButterKnife.bind(this);
        setUpToolbar(true);
        analytics.sendScreen("Transcript - Semester");

        // Try finding the semester
        Semester semester = SQLite.select()
                .from(Semester.class)
                .where(Semester_Table.id.eq(getIntent().getIntExtra(Constants.ID, -1)))
                .querySingle();

        if (semester == null) {
            DialogHelper.error(this);
            Timber.e(new IllegalArgumentException("Semester was null"));
            finish();
            return;
        }

        // Set the title as this current semester
        setTitle(semester.getSemesterName(this));

        // Set the info up
        bachelor.setText(semester.getBachelor());
        program.setText(semester.getProgram());
        gpa.setText(getString(R.string.transcript_termGPA, String.valueOf(semester.getGPA())));
        credits.setText(getString(R.string.semester_termCredits, String.valueOf(
                semester.getCredits())));
        fullTime.setText(semester.isFullTime() ?
                R.string.semester_fullTime : R.string.semester_partTime);

        // Set up the courses list
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SemesterAdapter(semester.getId());
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.update();
    }
}
