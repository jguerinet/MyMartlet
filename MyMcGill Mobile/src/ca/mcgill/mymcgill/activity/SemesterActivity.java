package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.os.Bundle;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author: Julien
 * Date: 31/01/14, 7:59 PM
 */
public class SemesterActivity extends Activity{
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


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
