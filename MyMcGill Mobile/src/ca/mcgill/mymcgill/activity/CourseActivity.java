package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.object.Class;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.Help;

/**
 * Author: Julien
 * Date: 04/02/14, 8:22 PM
 */
public class CourseActivity extends BaseActivity {

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_top, R.anim.stay);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_course);

        //Get the course from the intent
        Class course = (Class)getIntent().getSerializableExtra(Constants.COURSE);

        assert (course != null);

        //Get the screen dimensions
        int displayWidth = Help.getDisplayWidth(getWindowManager().getDefaultDisplay());
        int displayHeight = Help.getDisplayHeight(getWindowManager().getDefaultDisplay());

        //Set the width and height to 2/3 of the screen
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_course_container);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        //Quick check
        assert (params != null);
        params.height = (2 * displayHeight) / 3;
        params.width = (5 * displayWidth) / 6;
        layout.setLayoutParams(params);

        //Set up the info
        TextView courseCode = (TextView)findViewById(R.id.course_code);
        courseCode.setText(course.getCourseCode());

        TextView courseName = (TextView)findViewById(R.id.course_name);
        courseName.setText(course.getCourseName());

        TextView courseTime = (TextView)findViewById(R.id.course_time);
        courseTime.setText(getResources().getString(R.string.course_time, Help.getLongTimeString(this, course.getStartHour(), course.getStartMinute()),
                Help.getLongTimeString(this, course.getEndHour(), course.getEndMinute())));

        TextView courseLocation = (TextView)findViewById(R.id.course_location);
        courseLocation.setText(course.getRoom());

        TextView scheduleType = (TextView)findViewById(R.id.schedule_type);
        scheduleType.setText(course.getScheduleType());

        TextView courseProfessor = (TextView)findViewById(R.id.course_professor);
        courseProfessor.setText(course.getProfessorName());

        TextView courseSection = (TextView)findViewById(R.id.course_section);
        courseSection.setText(course.getSection());

        TextView courseCredits = (TextView)findViewById(R.id.course_credits);
        courseCredits.setText(course.getCredits());

        TextView courseCRN = (TextView)findViewById(R.id.course_crn);
        courseCRN.setText(String.valueOf(course.getCRN()));
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(CourseActivity.this, App.getHomePage().getHomePageClass()));
        super.onBackPressed();
    }

    public void done(View v){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
    }
}
