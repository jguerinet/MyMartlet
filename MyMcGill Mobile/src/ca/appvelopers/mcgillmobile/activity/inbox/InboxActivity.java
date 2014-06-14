package ca.appvelopers.mcgillmobile.activity.inbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.Inbox;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Load;

/**
 * Created by Ryan Singzon on 14/02/14.
 */
public class InboxActivity extends DrawerActivity{

    private Inbox mInbox;
    private TextView mTotalNew;
    private ListView mListView;
    private boolean mFirstLoad = false;
    
    InboxAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_inbox);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Email - Inbox");

        mFirstLoad = true;

        //Get the stored inbox from the App
        mInbox = App.getInbox();

        //Get views
        mTotalNew = (TextView)findViewById(R.id.inbox_total_new);

        mListView = (ListView)findViewById(android.R.id.list);

        //If inbox is not null, we only need to refresh it
        boolean refresh = (mInbox != null);
        //Load the info if we have a stored copy
        if(refresh){
            loadInfo();
        }

        //Start thread to retrieve inbox
        new InboxGetter(refresh).execute();
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
    	menu.add(Menu.NONE, Constants.MENU_ITEM_REFRESH, Menu.NONE, R.string.refresh);
    	menu.add(Menu.NONE, Constants.MENU_ITEM_SEND, Menu.NONE, R.string.email_reply);
		return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Constants.MENU_ITEM_REFRESH:
                new InboxGetter(true).execute();
                return true;
            case Constants.MENU_ITEM_SEND:
            	//TODO 
                Intent replyIntent = new Intent(this,ReplyActivity.class);
                this.startActivity(replyIntent);
            	return true;
            case R.id.action_refresh:
                //Start thread to retrieve inbox
                new InboxGetter(true).execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


	//JDA
    @Override
    protected void onResume() {
        super.onResume();
        if(!mFirstLoad){
            //Update
            new InboxGetter(true).execute();
        }
        else{
            mFirstLoad = false;
        }
    }

    //Populates the list with the emails contained in the Inbox object
    public void loadInfo(){
        //Show a message if the user has no new emails
        if(mInbox.getNumNewEmails() == 0){
            mTotalNew.setVisibility(View.GONE);
            TextView noNew = (TextView)findViewById(R.id.inbox_no_new);
            noNew.setVisibility(View.VISIBLE);
        }
        else{
            TextView noNew = (TextView)findViewById(R.id.inbox_no_new);
            noNew.setVisibility(View.GONE);

            //Get the number of new emails
            mTotalNew.setVisibility(View.VISIBLE);
            mTotalNew.setText(getResources().getString(R.string.email_newMessages, mInbox.getNumNewEmails()));
        }

        //Load adapter
        adapter = new InboxAdapter(InboxActivity.this, mInbox);
        mListView.setAdapter(adapter);
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

        //Retrieve content from inbox page
        @Override
        protected Void doInBackground(Void... params){
            Context context = InboxActivity.this;

            //If null, create new Inbox
            if(mInbox == null){
                mInbox = new Inbox(Load.loadFullUsername(context),Load.loadPassword(context));
            }

            //Check messages
            mInbox.retrieveEmail();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Reload the info in the views
                    loadInfo();
                }
            });

            return null;
        }

        //Update or create email object and display data
        @Override
        protected void onPostExecute(Void result){
            //Dismiss the progress dialog if there was one
            if(!mRefresh){
                mProgressDialog.dismiss();
            }
            setProgressBarIndeterminateVisibility(false);

            //Update the number of unread messages in the drawer
            updateUnreadMessages();
        }
    }
}
