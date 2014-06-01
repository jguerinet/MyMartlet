package ca.mcgill.mymcgill.activity.transcript;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ca.mcgill.mymcgill.App;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.DialogHelper;
import ca.mcgill.mymcgill.util.Parser;

/**
 * Author: Ryan Singzon
 * Date: 30/01/14, 6:01 PM
 */
public class TranscriptActivity extends DrawerActivity {
    private Transcript mTranscript;
    private TextView mCGPA, mTotalCredits;
    private ListView mListView;
    private boolean mDoubleBackToExit;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_transcript);
        super.onCreate(savedInstanceState);

        //Get the stored transcript from the ApplicationClass
        mTranscript = App.getTranscript();

        //Get the views
        mCGPA = (TextView)findViewById(R.id.transcript_cgpa);
        mTotalCredits = (TextView)findViewById(R.id.transcript_credits);
        mListView = (ListView)findViewById(android.R.id.list);

        //Load the info stored on the device
        loadInfo();

        //Start thread to retrieve transcript
        new TranscriptGetter().execute();
    }

    @Override
    public void onBackPressed(){
        if(App.getHomePage() != HomePage.TRANSCRIPT){
            startActivity(new Intent(TranscriptActivity.this, App.getHomePage().getHomePageClass()));
            super.onBackPressed();
        }
        else{
            if (mDoubleBackToExit) {
                super.onBackPressed();
                return;
            }
            this.mDoubleBackToExit = true;
            Toast.makeText(this, R.string.back_toaster_message, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDoubleBackToExit=false;
                }
            }, 2000);
        }
    }

    private void loadInfo(){
        //Fill out the transcript info
        mCGPA.setText(getResources().getString(R.string.transcript_CGPA, mTranscript.getCgpa()));
        mTotalCredits.setText(getResources().getString(R.string.transcript_credits, mTranscript.getTotalCredits()));

        //Reload the adapter
        TranscriptAdapter adapter = new TranscriptAdapter(TranscriptActivity.this, mTranscript);
        mListView.setAdapter(adapter);
    }

    private class TranscriptGetter extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            //Show the user we are downloading new info
            setProgressBarIndeterminateVisibility(true);
        }

        //Retrieve content from transcript page
        @Override
        protected Boolean doInBackground(Void... params){
            final Activity activity = TranscriptActivity.this;

            String transcriptString = Connection.getInstance().getUrl(activity, Connection.minervaTranscript);

            if(transcriptString == null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
							DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
							        activity.getResources().getString(R.string.error_other));
						} catch (Exception e) {
							e.printStackTrace();
						}
                    }
                });
                return false;
            }
            //Empty String: no need for an alert dialog but no need to reload
            else if(TextUtils.isEmpty(transcriptString)){
                return false;
            }

            //Parse the transcript
            mTranscript = Parser.parseTranscript(transcriptString);

            //Save it to the instance variable in the Application class
            App.setTranscript(mTranscript);

            return true;
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean loadInfo){
            if(loadInfo){
                //Reload the info in the views
                loadInfo();
            }
            setProgressBarIndeterminateVisibility(false);
        }
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
                new TranscriptGetter().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
