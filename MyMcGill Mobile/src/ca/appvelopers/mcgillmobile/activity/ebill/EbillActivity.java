package ca.appvelopers.mcgillmobile.activity.ebill;

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

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerActivity;
import ca.appvelopers.mcgillmobile.object.EbillItem;
import ca.appvelopers.mcgillmobile.object.HomePage;
import ca.appvelopers.mcgillmobile.object.UserInfo;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

public class EbillActivity extends DrawerActivity {
	private List<EbillItem> mEbillItems = new ArrayList<EbillItem>();
    private UserInfo mUserInfo;
    private boolean mDoubleBackToExit;
    private TextView mUserName, mUserId;
    private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_ebill);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Ebill");

        //Get the initial info from the App
        mEbillItems = App.getEbill();
        mUserInfo = App.getUserInfo();

        //Get the views
        mUserName = (TextView)findViewById(R.id.ebill_user_name);
        mUserId = (TextView)findViewById(R.id.ebill_user_id);
        mListView = (ListView)findViewById(android.R.id.list);

        //Load the stored info
        loadInfo();

        //Start the thread to get the ebill
        new EbillGetter().execute();
	}

    @Override
    public void onBackPressed(){
        if(App.getHomePage() != HomePage.EBILL){
            startActivity(new Intent(EbillActivity.this, App.getHomePage().getHomePageClass()));
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
        if(mUserInfo != null){
            mUserName.setText(mUserInfo.getName());
            mUserId.setText(mUserInfo.getId());
        }
        EbillAdapter adapter = new EbillAdapter(this, mEbillItems);
        mListView.setAdapter(adapter);
    }

    private class EbillGetter extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            //Show the user we are refreshing his content
            setProgressBarIndeterminateVisibility(true);

        }

        //Retrieve content from transcript page
        @Override
        protected Boolean doInBackground(Void... params){
            final Activity activity = EbillActivity.this;

            String ebillString = Connection.getInstance().getUrl(activity, Connection.EBILL);

            if(ebillString == null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.showNeutralAlertDialog(activity, activity.getResources().getString(R.string.error),
                                activity.getResources().getString(R.string.error_other));
                    }
                });
                return false;
            }
            //Empty String: no need for an alert dialog but no need to reload
            else if(TextUtils.isEmpty(ebillString)){
                return false;
            }

            mEbillItems.clear();

            //Parse the ebill and the user info
            Parser.parseEbill(ebillString);
            Parser.parseUserInfo(ebillString);

            //Save it to the instance variable
            mEbillItems = App.getEbill();
            mUserInfo = App.getUserInfo();

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
                new EbillGetter().execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


