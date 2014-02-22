package ca.mcgill.mymcgill.activity.inbox;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import ca.mcgill.mymcgill.Exceptions.MinervaLoggedOutException;
import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.ConnectionStatus;
import ca.mcgill.mymcgill.object.Inbox;
import ca.mcgill.mymcgill.object.Email;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.Load;

/**
 * Created by Ryan Singzon on 14/02/14.
 */
public class InboxActivity extends ListActivity{

    private Inbox mInbox;
    private TextView mTotalNew;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_inbox);

        //Get the stored inbox from the ApplicationClass
        mInbox = ApplicationClass.getInbox();

        //Get views
        mTotalNew = (TextView)findViewById(R.id.inbox_total_new);

        //If inbox is not null, we only need to refresh it
        boolean refresh = (mInbox != null);
        //Load the info if we have a stored copy
        if(refresh){
            loadInfo();
        }

        //Start thread to retrieve inbox
        new InboxGetter(refresh).execute();
    }

    //Populates the list with the emails contained in the Inbox object
    private void loadInfo(){
        //Get the number of new emails
        mTotalNew.setText(getResources().getString(R.string.inbox_newMessages, mInbox.getNumNewEmails()));

        //Load adapter
        InboxAdapter adapter = new InboxAdapter(InboxActivity.this, mInbox);
        setListAdapter(adapter);

    }

    private class InboxGetter extends AsyncTask<Void, Void, Void> {
        private boolean mRefresh;
        private ProgressDialog mProgressDialog;

        public InboxGetter(boolean refresh){
            this.mRefresh = refresh;
        }

        @Override
        protected void onPreExecute(){
            //Only show a ProgressDialog if we are not refreshing the content but
            //downloading it for the first time
            if(!mRefresh){
                mProgressDialog = new ProgressDialog(InboxActivity.this);
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
            Context context = InboxActivity.this;
            String inboxString="";

            //Retrieve inbox
            if(mInbox != null){
                mInbox.retrieveEmail();
            } else{
                mInbox = new Inbox(Load.loadFullUsername(context),Load.loadPassword(context));
            }

            //Save it to the instance variable in the Application class
            ApplicationClass.setInbox(mInbox);

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
