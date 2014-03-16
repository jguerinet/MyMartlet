package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;


import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.util.ApplicationClass;


/**
 * Author: JDA
 * Date: 15/03/14
 */
public class ChangeSemesterActivity extends Activity {

    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.in_from_top, R.anim.stay);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_semester);
    	
//        //Get the screen height
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//
//        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
//            size.set(display.getWidth(), display.getHeight());
//        }
//        else{
//            display.getSize(size);
//        }
//
//        int displayWidth = size.x;
//        int displayHeight = size.y;
//
//        //Set the width and height to 2/3 of the screen
//        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_course_container);
//
//        ViewGroup.LayoutParams params = layout.getLayoutParams();
//        //Quick check
//        assert (params != null);
//        params.height = (2 * displayHeight) / 3;
//        params.width = (5 * displayWidth) / 6;
//        layout.setLayoutParams(params);

        Spinner season = (Spinner) findViewById(R.id.change_semester_season);
    	
    	//Standard ArrayAdapter
        ArrayAdapter<CharSequence> seasonAdapter = ArrayAdapter.createFromResource(this,
                R.array.season_entries, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        seasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        season.setAdapter(seasonAdapter);
        //season.setSelection(ApplicationClass.getHomePage().ordinal());
        season.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //Get the chosen language
                HomePage chosenHomePage = HomePage.values()[position];

                //Update it in the ApplicationClass
                    ApplicationClass.setHomePage(chosenHomePage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        
        Spinner year = (Spinner) findViewById(R.id.change_semester_year);
    	
    	//Standard ArrayAdapter
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_entries, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        year.setAdapter(yearAdapter);
//        season.setSelection(ApplicationClass.getHomePage().ordinal());
//        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                //Get the chosen language
//                HomePage chosenHomePage = HomePage.values()[position];
//
//                //Update it in the ApplicationClass
//                    ApplicationClass.setHomePage(chosenHomePage);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {}
//        });
    }

//    public void done(View v){
//        finish();
//        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
//    }
}
