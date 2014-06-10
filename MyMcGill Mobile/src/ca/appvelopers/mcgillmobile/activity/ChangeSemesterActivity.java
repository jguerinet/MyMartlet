package ca.appvelopers.mcgillmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.object.Semester;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Help;


/**
 * Author: JDA
 * Date: 15/03/14
 */
public class ChangeSemesterActivity extends BaseActivity {

    private boolean mRegisterTerms;
    private List<Term> mTerms;
    private Term mTerm;
    private CheckBox mDefaultCheckbox;

	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.in_from_top, R.anim.stay);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_semester);

        GoogleAnalytics.sendScreen(this, "Schedule - Change Semester");
    	
        //Get the screen width
        int displayWidth = Help.getDisplayWidth(getWindowManager().getDefaultDisplay());

        //Set the width to 2/3 of the screen
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_change_semester);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (5 * displayWidth) / 6;
        layout.setLayoutParams(params);

        //Check if this is the user list of terms or the registration list of terms
        mRegisterTerms = getIntent().getBooleanExtra(Constants.REGISTER_TERMS, false);

        //Set up the default checkbox
        mDefaultCheckbox = (CheckBox)findViewById(R.id.change_semester_default);

        mTerms = new ArrayList<Term>();

        //Get the current default term
        mTerm = App.getDefaultTerm();

        //Extract all the terms that the user has registered in
        for (Semester semester : App.getTranscript().getSemesters()) {
            mTerms.add(semester.getTerm());
        }

        //If we are also using the registration term
        if(mRegisterTerms){
            //Get the current terms that the user can register in
            for(Term term : App.getRegisterTerms()){
                if(!mTerms.contains(term)){
                    mTerms.add(term);
                }
            }
        }

        //Order them chronologically
        Collections.sort(mTerms, new Comparator<Term>() {
            @Override
            public int compare(Term term, Term term2) {
                return term.isAfter(term2) ? -1 : 1;
            }
        });

        //Make a list with their strings
        List<String> termStrings = new ArrayList<String>();
        for(Term term : mTerms){
            termStrings.add(term.toString(this));
        }

        //Set up the adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, termStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set up the spinner
        Spinner spinner = (Spinner)findViewById(R.id.change_semester_term);
        spinner.setAdapter(adapter);
        spinner.setSelection(mTerms.indexOf(mTerm));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the selected term
                mTerm = mTerms.get(position);
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
        //Check if the default checkbox is checked
        if(mDefaultCheckbox.isChecked()){
            //Store this semester as the default semester if it is
            App.setDefaultTerm(mTerm);
        }

        Intent replyIntent = new Intent();
        replyIntent.putExtra(Constants.TERM, mTerm);
        setResult(RESULT_OK, replyIntent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
	}
}
