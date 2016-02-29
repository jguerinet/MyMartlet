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

package ca.appvelopers.mcgillmobile.ui.transcript.semester;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Constants;
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
    @Bind(R.id.semester_bachelor)
    protected TextView mBachelor;
    /**
     * Semester program
     */
    @Bind(R.id.semester_program)
    protected TextView mProgram;
    /**
     * Semester GPA
     */
    @Bind(R.id.semester_GPA)
    protected TextView mGPA;
    /**
     * Semester credits
     */
    @Bind(R.id.semester_credits)
    protected TextView mCredits;
    /**
     * User's status during this semester
     */
    @Bind(R.id.semester_full_time)
    protected TextView mFullTime;
    /**
     * Courses taken during this semester
     */
    @Bind(android.R.id.list)
    protected RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);
        ButterKnife.bind(this);
        setUpToolbar(true);
        analytics.sendScreen("Transcript - Semester");

        //Get the semester from the intent
        Semester semester = (Semester) getIntent().getSerializableExtra(Constants.SEMESTER);

        if (semester == null) {
            DialogHelper.error(this);
            Timber.e(new IllegalArgumentException(), "Semester was null");
            finish();
            return;
        }

        //Set the title as this current semester
        setTitle(semester.getSemesterName(this));

        //Set the info up
        mBachelor.setText(semester.getBachelor());
        mProgram.setText(semester.getProgram());
        mGPA.setText(getString(R.string.transcript_termGPA, String.valueOf(semester.getGPA())));
        mCredits.setText(getString(R.string.semester_termCredits, semester.getCredits()));
        mFullTime.setText(semester.isFullTime() ?
                R.string.semester_fullTime : R.string.semester_partTime);

        //Set up the courses list
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(new SemesterAdapter(semester.getCourses()));
    }
}
