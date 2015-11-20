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

package ca.appvelopers.mcgillmobile.ui.transcript.semester;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Displays information about a semester from the user's transcript
 * @author Julien Guerinet
 * @version 2.0.1
 * @since 1.0.0
 */
public class SemesterActivity extends BaseActivity {
    /**
     * The semester's bachelor degree
     */
    @Bind(R.id.semester_bachelor)
    TextView mBachelor;
    /**
     * The semester program
     */
    @Bind(R.id.semester_program)
    TextView mProgram;
    /**
     * The semester GPA
     */
    @Bind(R.id.semester_GPA)
    TextView mGPA;
    /**
     * The semester credits
     */
    @Bind(R.id.semester_credits)
    TextView mCredits;
    /**
     * The user's status during this semester
     */
    @Bind(R.id.semester_fullTime)
    TextView mFullTime;
    /**
     * The courses taken during this semester
     */
    @Bind(android.R.id.list)
    RecyclerView mCourses;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);
        ButterKnife.bind(this);
        setUpToolbar(true);
        Analytics.getInstance().sendScreen("Transcript - Semester");

        //Get the semester from the intent
        Semester semester = (Semester) getIntent().getSerializableExtra(Constants.SEMESTER);

        //Set the title as this current semester
        setTitle(semester.getSemesterName());

        //Set the info up
        mBachelor.setText(semester.getBachelor());
        mProgram.setText(semester.getProgram());
        mGPA.setText(getString(R.string.transcript_termGPA, String.valueOf(semester.getGPA())));
        mCredits.setText(getString(R.string.semester_termCredits, semester.getCredits()));
        mFullTime.setText(semester.isFullTime() ? getString(R.string.semester_fullTime) :
                getString(R.string.semester_partTime));

        //Set up the courses list
        mCourses.setLayoutManager(new LinearLayoutManager(this));
        mCourses.setAdapter(new SemesterAdapter(this, semester.getCourses()));
    }
}
