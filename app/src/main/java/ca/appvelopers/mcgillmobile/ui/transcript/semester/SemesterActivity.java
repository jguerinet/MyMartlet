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
import android.widget.ListView;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Semester;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Displays information about a semester from the user's transcript
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class SemesterActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        setUpToolbar(true);

        Analytics.getInstance().sendScreen("Transcript - Semester");

        //Get the semester from the intent
        Semester semester = (Semester) getIntent().getSerializableExtra(Constants.SEMESTER);

        //Set the title as this current semester
        setTitle(semester.getSemesterName());

        //Set the info up
        TextView semesterBachelor = (TextView)findViewById(R.id.semester_bachelor);
        semesterBachelor.setText(semester.getBachelor());

        TextView semesterProgram = (TextView)findViewById(R.id.semester_program);
        semesterProgram.setText(semester.getProgram());

        TextView semesterGPA = (TextView)findViewById(R.id.semester_GPA);
        semesterGPA.setText(getString(R.string.transcript_termGPA,
                String.valueOf(semester.getGPA())));

        TextView semesterCredits = (TextView)findViewById(R.id.semester_credits);
        semesterCredits.setText(getString(R.string.semester_termCredits,
                semester.getCredits()));

        TextView semesterFullTime = (TextView)findViewById(R.id.semester_fullTime);
        semesterFullTime.setText(semester.isFullTime() ? getString(R.string.semester_fullTime) :
                getString(R.string.semester_partTime));

        //Set up the courses list
        SemesterAdapter adapter = new SemesterAdapter(this, semester);
        ListView semesterList = (ListView)findViewById(R.id.semester_list);
        semesterList.setAdapter(adapter);
    }
}
