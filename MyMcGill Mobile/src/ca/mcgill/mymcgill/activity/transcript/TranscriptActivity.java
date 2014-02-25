package ca.mcgill.mymcgill.activity.transcript;

import java.io.IOException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.Exceptions.MinervaLoggedOutException;
import ca.mcgill.mymcgill.object.ConnectionStatus;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Load;

/**
 * Author: Ryan Singzon
 * Date: 30/01/14, 6:01 PM
 */
public class TranscriptActivity extends ListActivity {
    private Transcript mTranscript;
    private TextView mCGPA, mTotalCredits;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_transcript);

        //Get the stored transcript from the ApplicationClass
        mTranscript = ApplicationClass.getTranscript();

        //Get the views
        mCGPA = (TextView)findViewById(R.id.transcript_cgpa);
        mTotalCredits = (TextView)findViewById(R.id.transcript_credits);

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
        setListAdapter(adapter);
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
        	Context context = TranscriptActivity.this;
            String transcriptString="";
            
			try {
				transcriptString = Connection.getInstance().getUrl(Connection.minervaTranscript);
			} catch (MinervaLoggedOutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ConnectionStatus connectionResult = Connection.getInstance().connectToMinerva(context,Load.loadUsername(context),Load.loadPassword(context));
                //Successful connection: MainActivity
                if(connectionResult == ConnectionStatus.CONNECTION_OK){
                	
                	//TRY again
                	try {
                		transcriptString = Connection.getInstance().getUrl(Connection.minervaSchedule);
					} catch (Exception e1) {
						// TODO display error message
						e1.printStackTrace();
						return null;
					} 
                }
                else{
                    //TODO: display error Message
                	return null;
                }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
