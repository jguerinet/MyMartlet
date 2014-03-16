package ca.mcgill.mymcgill.activity.transcript;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.DialogHelper;

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

        //Get the stored transcript from the ApplicationClass
        mTranscript = ApplicationClass.getTranscript();

        //Get the views
        mCGPA = (TextView)findViewById(R.id.transcript_cgpa);
        mTotalCredits = (TextView)findViewById(R.id.transcript_credits);
        mListView = (ListView)findViewById(android.R.id.list);

        //If it is not null, we only need to refresh it
        boolean refresh = (mTranscript != null);
        //Load the info if we have a stored copy
        if(refresh){
            loadInfo();
        }

        //Start thread to retrieve transcript
        new TranscriptGetter(refresh).execute();
    }

    private void loadInfo(){
        //Fill out the transcript info
        mCGPA.setText(getResources().getString(R.string.transcript_cgpa, mTranscript.getCgpa()));
        mTotalCredits.setText(getResources().getString(R.string.transcript_credits, mTranscript.getTotalCredits()));

        //Reload the adapter
        TranscriptAdapter adapter = new TranscriptAdapter(TranscriptActivity.this, mTranscript);
        mListView.setAdapter(adapter);
    }

    private class TranscriptGetter extends AsyncTask<Void, Void, Void> {
        private boolean mRefresh;
        private ProgressDialog mProgressDialog;

        public TranscriptGetter(boolean refresh){
            this.mRefresh = refresh;
        }

        @Override
        protected void onPreExecute(){
            //Only show a ProgressDialog if we are not refreshing the content but
            //downloading it for the first time
            if(!mRefresh){
                mProgressDialog = new ProgressDialog(TranscriptActivity.this);
                mProgressDialog.setMessage(getResources().getString(R.string.please_wait));
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
            }
            //If not, just put it in the Action bar
            else{
                setProgressBarIndeterminateVisibility(true);
            }
        }

        //Retrieve content from transcript page
        @Override
        protected Void doInBackground(Void... params){
            String transcriptString;
            final Activity activity = TranscriptActivity.this;

            transcriptString = Connection.getInstance().getUrl(activity, Connection.minervaTranscript);

            if(transcriptString == null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                activity.getResources().getString(R.string.login_error_other));
                    }
                });
                return null;
            }
            //Empty String: no need for an alert dialog but no need to reload
            else if(TextUtils.isEmpty(transcriptString)){
                return null;
            }

            //Parse the transcript
            mTranscript = new Transcript(transcriptString);

            //Save it to the instance variable in the Application class
            ApplicationClass.setTranscript(mTranscript);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Reload the info in the views
                    loadInfo();
                }
            });

            return null;
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Void result){
            //Dismiss the progress dialog if there was one
            if(!mRefresh){
                mProgressDialog.dismiss();
            }
            setProgressBarIndeterminateVisibility(false);
        }
    }

}
