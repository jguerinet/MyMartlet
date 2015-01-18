package ca.appvelopers.mcgillmobile.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Author: Julien Guerinet
 * Date: 2014-09-23 9:45 AM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class CourseDialog extends AlertDialog {
    private AlertDialog mDialog;

    public CourseDialog(final Activity activity, final ClassItem classItem) {
        super(activity);

        GoogleAnalytics.sendScreen(activity, "Schedule - Course");

        //Inflate the title
        View title = View.inflate(activity, R.layout.dialog_course_title, null);

        //Fill in the info
        TextView courseCode = (TextView)title.findViewById(R.id.course_code);
        courseCode.setText(classItem.getCourseCode());

        TextView courseTitle = (TextView)title.findViewById(R.id.course_title);
        courseTitle.setText(classItem.getCourseTitle());

        //Inflate the right view
        View layout = View.inflate(activity, R.layout.dialog_course, null);

        //Set up the info
        TextView courseTime = (TextView)layout.findViewById(R.id.course_time);
        courseTime.setText(classItem.getTimeString(activity));

        TextView courseLocation = (TextView)layout.findViewById(R.id.course_location);
        courseLocation.setText(classItem.getLocation());

        TextView scheduleType = (TextView)layout.findViewById(R.id.schedule_type);
        scheduleType.setText(classItem.getSectionType());

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
                Help.openURL(activity, Help.getDocuumLink(classItem.getCourseSubject(), classItem.getCourseNumber()));
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