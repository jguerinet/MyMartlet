package ca.mcgill.mymcgill.activity.transcript;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.semester.SemesterActivity;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Constants;

/**
 * Author: Julien
 * Date: 31/01/14, 6:06 PM
 */
public class TranscriptAdapter extends BaseAdapter {
    private Context mContext;
    private List<Semester> mSemesters;

    public TranscriptAdapter(Context context, Transcript transcript){
        this.mContext = context;
        this.mSemesters = transcript.getSemesters();
    }

    @Override
    public int getCount() {
        return mSemesters.size();
    }

    @Override
    public Semester getItem(int position) {
        return mSemesters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        //Reuse the view if it's already been used
        if(view == null){
            //Get the inflater
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_transcript_semester, null);
        }

        //Get the current semester we are inflating
        final Semester semester = getItem(position);

        //Set up the info
        TextView semesterName = (TextView)view.findViewById(R.id.semester_name);
        semesterName.setText(semester.getSemesterName());

        TextView semesterGPA = (TextView)view.findViewById(R.id.semester_termGPA);
        semesterGPA.setText(mContext.getResources().getString(R.string.transcript_termGPA, String.valueOf(semester.getTermGPA())));

        //Set up the chevron
        TextView chevron = (TextView)view.findViewById(R.id.semester_chevron);
        chevron.setTypeface(ApplicationClass.getIconFont());

        //Set up the onClicklistener for the view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SemesterActivity.class);
                intent.putExtra(Constants.SEMESTER, semester);
                mContext.startActivity(intent);
            }
        });

        return view;
    }
}
