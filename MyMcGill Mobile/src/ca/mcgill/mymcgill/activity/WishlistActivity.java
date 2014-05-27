package ca.mcgill.mymcgill.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.util.ApplicationClass;

/**
 * Created by c on 2014-05-26.
 */
public class WishlistActivity extends DrawerActivity {
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_transcript);
        super.onCreate(savedInstanceState);

        //Get the stored transcript from the ApplicationClass
        //mTranscript = ApplicationClass.getTranscript();

        //Get the views
        //mCGPA = (TextView)findViewById(R.id.transcript_cgpa);
        //mTotalCredits = (TextView)findViewById(R.id.transcript_credits);
        //mListView = (ListView)findViewById(android.R.id.list);

        //Load the info stored on the device
        //loadInfo();

        //Start thread to retrieve transcript
        //new TranscriptGetter().execute();
    }
}