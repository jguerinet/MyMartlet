package ca.mcgill.mymcgill.activity.semester;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author: Julien
 * Date: 31/01/14, 7:59 PM
 */
public class SemesterActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        //Get the semester from the intent
        Semester semester = (Semester) getIntent().getSerializableExtra(Constants.SEMESTER);

        //Quick check
        assert (semester != null);

        //Set the title as this current semester
        setTitle(semester.getSemesterName());

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
        setListAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
