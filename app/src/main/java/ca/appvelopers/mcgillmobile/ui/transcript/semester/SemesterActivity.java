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
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Author: Julien
 * Date: 31/01/14, 7:59 PM
 * Activity that will show a specific semester from the user's transcript
 */
public class SemesterActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Analytics.getInstance().sendScreen("Transcript - Semester");

        //Get the semester from the intent
        Semester semester = (Semester) getIntent().getSerializableExtra(Constants.SEMESTER);

        //Quick check
        assert (semester != null);

        //Set the title as this current semester
        setTitle(semester.getSemesterName(this));

        //Set the info up
        TextView semesterBachelor = (TextView)findViewById(R.id.semester_bachelor);
        semesterBachelor.setText(semester.getBachelor());

        TextView semesterProgram = (TextView)findViewById(R.id.semester_program);
        semesterProgram.setText(semester.getProgram());

        TextView semesterGPA = (TextView)findViewById(R.id.semester_GPA);
        semesterGPA.setText(getResources().getString(R.string.transcript_termGPA, String.valueOf(semester.getTermGPA())));

        TextView semesterCredits = (TextView)findViewById(R.id.semester_credits);
        semesterCredits.setText(getResources().getString(R.string.semester_termCredits, semester.getTermCredits()));

        TextView semesterFullTime = (TextView)findViewById(R.id.semester_fullTime);
        semesterFullTime.setText(semester.isFullTime() ? getResources().getString(R.string.semester_fullTime) :
                getResources().getString(R.string.semester_partTime));

        //Set up the courses list
        SemesterAdapter adapter = new SemesterAdapter(this, semester);
        ListView semesterList = (ListView)findViewById(R.id.semester_list);
        semesterList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
