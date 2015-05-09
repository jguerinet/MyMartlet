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
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Shows the course details when clicked in the schedule
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class CourseDialog extends AlertDialog {
    /**
     * The activity instance
     */
    private Activity mActivity;
    /**
     * The course
     */
    private Course mCourse;

    /**
     * Default Constructor
     *
     * @param activity The calling activity
     * @param course   The course
     */
    public CourseDialog(final Activity activity, final Course course){
        super(activity);

        this.mActivity = activity;
        this.mCourse = course;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_course);

        Analytics.getInstance().sendScreen("Schedule - Course");

        //Inflate the title
        View title = View.inflate(mActivity, R.layout.dialog_course_title, null);

        //Course Code
        TextView courseCode = (TextView)title.findViewById(R.id.course_code);
        courseCode.setText(mCourse.getCode());

        //Course Title
        TextView courseTitle = (TextView)title.findViewById(R.id.course_title);
        courseTitle.setText(mCourse.getTitle());

        //Set it
        setCustomTitle(title);

        //Inflate the body
        View layout = View.inflate(mActivity, R.layout.dialog_course, null);

        //Course Time
        TextView courseTime = (TextView)layout.findViewById(R.id.course_time);
        courseTime.setText(mCourse.getTimeString());

        //Course Location
        TextView courseLocation = (TextView)layout.findViewById(R.id.course_location);
        courseLocation.setText(mCourse.getLocation());

        //Type
        TextView type = (TextView)layout.findViewById(R.id.course_type);
        type.setText(mCourse.getType());

        //Instructor
        TextView instructor = (TextView)layout.findViewById(R.id.course_instructor);
        instructor.setText(mCourse.getInstructor());

        //Section
        TextView section = (TextView)layout.findViewById(R.id.course_section);
        section.setText(mCourse.getSection());

        //Credits
        TextView credits = (TextView)layout.findViewById(R.id.course_credits);
        credits.setText(String.valueOf(mCourse.getCredits()));

        //CRN
        TextView crn = (TextView)layout.findViewById(R.id.course_crn);
        crn.setText(String.valueOf(mCourse.getCRN()));

        //Docuum Link
        TextView docuum = (TextView)layout.findViewById(R.id.course_docuum);
        docuum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Help.openURL(mActivity, Help.getDocuumLink(mCourse.getSubject(),
                        mCourse.getNumber()));
            }
        });

        TextView map = (TextView)layout.findViewById(R.id.course_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO
            }
        });

        //Dismiss button
        setButton(DialogInterface.BUTTON_NEUTRAL, mActivity.getString(R.string.done),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        dialog.dismiss();
                    }
                });

        //Cancelable
        setCancelable(true);
    }
}