package ca.appvelopers.mcgillmobile.activity.courseslist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Created by JDAlfaro on 2014-05-31.
 */
public class ChangeListActivity extends BaseActivity {

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_top, R.anim.stay);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_list);

        //Get the screen width
        int displayWidth = Help.getDisplayWidth(getWindowManager().getDefaultDisplay());

        //Set the width to 2/3 of the screen
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_change_list);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        //Quick check
        assert (params != null);
        params.width = (5 * displayWidth) / 6;
        layout.setLayoutParams(params);

        //Get the current year
        //Semester defaultSemester = App.getDefaultSemester();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = Calendar.JUNE;

        //Make a list with their strings
        List<String> semesterStrings = new ArrayList<String>();
        if(month < Calendar.JUNE) {
            semesterStrings.add("Summer " + year);
        }
        if(month < Calendar.OCTOBER) {
            semesterStrings.add("Fall " + year);
        }
        semesterStrings.add("Winter " + (year + 1));

        //Set up the semester adapter
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, semesterStrings);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set up the semester spinner
        Spinner semester = (Spinner) findViewById(R.id.change_semester);
        semester.setAdapter(semesterAdapter);
        //season.setSelection(mSeasonList.indexOf(defaultSemester.getSeason()));
        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Search Wishlist

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    // Cancel Button
    // Simply closes the dialog
    public void cancelPress(View v){
        setResult(RESULT_CANCELED);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
    }

    // Ok Button
    // Accepts changes and changes the schedule
    public void okPress(View v){
        // TODO Display Wishlist

    }
}