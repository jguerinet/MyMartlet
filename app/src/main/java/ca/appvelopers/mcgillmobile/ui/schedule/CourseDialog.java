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

package ca.appvelopers.mcgillmobile.ui.schedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.ClassItem;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;

public class CourseDialog extends AlertDialog {
    private AlertDialog mDialog;

    public CourseDialog(final Activity activity, final ClassItem classItem) {
        super(activity);

        Analytics.getInstance().sendScreen("Schedule - Course");

        //Inflate the title
        View title = View.inflate(activity, R.layout.dialog_course_title, null);

        //Fill in the info
        TextView courseCode = (TextView)title.findViewById(R.id.course_code);
        courseCode.setText(classItem.getCode());

        TextView courseTitle = (TextView)title.findViewById(R.id.course_title);
        courseTitle.setText(classItem.getTitle());

        //Inflate the right view
        View layout = View.inflate(activity, R.layout.dialog_course, null);

        //Set up the info
        TextView courseTime = (TextView)layout.findViewById(R.id.course_time);
        courseTime.setText(classItem.getTimeString(activity));

        TextView courseLocation = (TextView)layout.findViewById(R.id.course_location);
        courseLocation.setText(classItem.getLocation());

        TextView scheduleType = (TextView)layout.findViewById(R.id.schedule_type);
        scheduleType.setText(classItem.getType());

        TextView courseProfessor = (TextView)layout.findViewById(R.id.course_professor);
        courseProfessor.setText(classItem.getInstructor());

        TextView courseSection = (TextView)layout.findViewById(R.id.course_section);
        courseSection.setText(classItem.getSection());

        TextView courseCredits = (TextView)layout.findViewById(R.id.course_credits);
        courseCredits.setText(String.valueOf(classItem.getCredits()));

        TextView courseCRN = (TextView)layout.findViewById(R.id.course_crn);
        courseCRN.setText(String.valueOf(classItem.getCRN()));

        TextView courseDocuum = (TextView)layout.findViewById(R.id.course_docuum);
        courseDocuum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Help.openURL(activity, Help.getDocuumLink(classItem.getSubject(), classItem.getNumber()));
            }
        });

        TextView courseMap = (TextView)layout.findViewById(R.id.course_map);
        courseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO
                Toast.makeText(activity, "Not Implemented", Toast.LENGTH_SHORT).show();
            }
        });

        //Build the dialog
        Builder builder = new Builder(activity);
        builder.setCancelable(true)
            .setCustomTitle(title)
            .setView(layout)
            .setNeutralButton(activity.getString(R.string.done), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        mDialog = builder.create();
    }

    @Override
    public void show(){
        mDialog.show();
    }
}