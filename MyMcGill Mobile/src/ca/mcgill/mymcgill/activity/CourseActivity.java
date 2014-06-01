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
import ca.mcgill.mymcgill.object.ClassItem;
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
        ClassItem classItem = (ClassItem)getIntent().getSerializableExtra(Constants.CLASS);

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
        courseCode.setText(classItem.getCourseCode());

        TextView courseTitle = (TextView)findViewById(R.id.course_title);
        courseTitle.setText(classItem.getCourseTitle());

        TextView courseTime = (TextView)findViewById(R.id.course_time);
        courseTime.setText(classItem.getTimeString(this));

        TextView courseLocation = (TextView)findViewById(R.id.course_location);
        courseLocation.setText(classItem.getLocation());

        TextView scheduleType = (TextView)findViewById(R.id.schedule_type);
        scheduleType.setText(classItem.getSectionType());

        TextView courseProfessor = (TextView)findViewById(R.id.course_professor);
        courseProfessor.setText(classItem.getInstructor());

        TextView courseSection = (TextView)findViewById(R.id.course_section);
        courseSection.setText(classItem.getSection());

        TextView courseCredits = (TextView)findViewById(R.id.course_credits);
        courseCredits.setText(classItem.getCredits());

        TextView courseCRN = (TextView)findViewById(R.id.course_crn);
        courseCRN.setText(String.valueOf(classItem.getCRN()));
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
