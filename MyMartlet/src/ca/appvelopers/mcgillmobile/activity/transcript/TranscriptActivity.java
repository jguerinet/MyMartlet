package ca.appvelopers.mcgillmobile.activity.transcript;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.downloader.TranscriptDownloader;

/**
 * Author: Ryan Singzon
 * Date: 30/01/14, 6:01 PM
 */
public class TranscriptActivity extends DrawerActivity {
    private Transcript mTranscript;
    private TextView mCGPA, mTotalCredits;
    private ListView mListView;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_transcript);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Transcript");

        //Get the stored transcript from the App
        mTranscript = App.getTranscript();

        //Get the views
        mCGPA = (TextView)findViewById(R.id.transcript_cgpa);
        mTotalCredits = (TextView)findViewById(R.id.transcript_credits);
        mListView = (ListView)findViewById(android.R.id.list);

        //Load the info stored on the device
        loadInfo();
    }

    private void loadInfo(){
        //Fill out the transcript info
        mCGPA.setText(getResources().getString(R.string.transcript_CGPA, mTranscript.getCgpa()));
        mTotalCredits.setText(getResources().getString(R.string.transcript_credits, mTranscript.getTotalCredits()));

        //Reload the adapter
        TranscriptAdapter adapter = new TranscriptAdapter(TranscriptActivity.this, mTranscript);
        mListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                //Start thread to retrieve inbox
                executeTranscriptDownloader();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeTranscriptDownloader(){
        new TranscriptDownloader(this) {
            @Override
            protected void onPreExecute() {
                //Show the user we are downloading new info
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected void onPostExecute(Boolean loadInfo) {
                mTranscript = App.getTranscript();

                if(loadInfo){
                    //Reload the info in the views
                    loadInfo();
                }
                setProgressBarIndeterminateVisibility(false);
            }
        }.execute();
    }
}
