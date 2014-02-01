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
    private Semester mSemester;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester);

        overridePendingTransition(R.anim.right_in, R.anim.left_out);

        //Get the semester from the intent
        mSemester = (Semester)getIntent().getSerializableExtra(Constants.SEMESTER);

        //Quick check
        assert (mSemester != null);

        //Set the title as this current semester
        setTitle(mSemester.getSemesterName());

        //Set the info up
        TextView semesterBachelor = (TextView)findViewById(R.id.semester_bachelor);
        semesterBachelor.setText(mSemester.getBachelor());

        TextView semesterProgram = (TextView)findViewById(R.id.semester_program);
        semesterProgram.setText(mSemester.getProgram());

        TextView semesterGPA = (TextView)findViewById(R.id.semester_GPA);
        semesterGPA.setText(getResources().getString(R.string.transcript_termGPA, String.valueOf(mSemester.getTermGPA())));

        TextView semesterCredits = (TextView)findViewById(R.id.semester_credits);
        semesterCredits.setText(getResources().getString(R.string.semester_termCredits, mSemester.getTermCredits()));

        TextView semesterFullTime = (TextView)findViewById(R.id.semester_fullTime);
        semesterFullTime.setText(mSemester.isFullTime() ? getResources().getString(R.string.semester_fullTime) :
                getResources().getString(R.string.semester_partTime));

        //Set up the courses list
        SemesterAdapter adapter = new SemesterAdapter(this, mSemester);
        setListAdapter(adapter);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
