package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.os.Bundle;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Transcript;

/**
 * Created by Ryan Singzon on 30/01/14.
 */
public class TranscriptActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);
    }

    Transcript transcript = new Transcript();


}
