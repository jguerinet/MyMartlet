package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.util.Constants;
import ca.mcgill.mymcgill.util.Help;

/**
 * Author: Julien
 * Date: 04/02/14, 8:22 PM
 */
public class CourseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_top, R.anim.stay);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_course);

        //Get the course from the intent
        CourseSched course = (CourseSched)getIntent().getSerializableExtra(Constants.COURSE);

        assert (course != null);

        //Get the screen height
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            size.set(display.getWidth(), display.getHeight());
        }
        else{
            display.getSize(size);
        }

        int displayWidth = size.x;
        int displayHeight = size.y;

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
        courseSection.setText(course.getCourseCode());

        TextView courseCredits = (TextView)findViewById(R.id.course_credits);
        courseCredits.setText(course.getCredits());

        TextView courseCRN = (TextView)findViewById(R.id.course_crn);
        courseCRN.setText(String.valueOf(course.getCRN()));
    }

    public void done(View v){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
    }
}
