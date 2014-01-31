package ca.mcgill.mymcgill.activity.transcript;

import android.app.ListActivity;
import android.os.Bundle;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.ApplicationClass;

/**
 * Author: Ryan Singzon
 * Date: 30/01/14, 6:01 PM
 */
public class TranscriptActivity extends ListActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);

        //Get the transcript from the ApplicationClass
        Transcript transcript= ApplicationClass.getTranscript();

        //Create the adapter
        TranscriptAdapter adapter = new TranscriptAdapter(this, transcript);

        //Set the listview's adapter to this
        setListAdapter(adapter);
    }
}
