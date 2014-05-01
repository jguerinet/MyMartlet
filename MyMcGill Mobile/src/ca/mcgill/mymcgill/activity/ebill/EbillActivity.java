package ca.mcgill.mymcgill.activity.ebill;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.object.EbillItem;
import ca.mcgill.mymcgill.object.UserInfo;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;
import ca.mcgill.mymcgill.util.DialogHelper;

public class EbillActivity extends DrawerActivity {
	private List<EbillItem> mEbillItems = new ArrayList<EbillItem>();
    private UserInfo mUserInfo;

    private TextView mUserName, mUserId;
    private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_ebill);
        super.onCreate(savedInstanceState);

        //Get the initial info from the ApplicationClass
        mEbillItems = ApplicationClass.getEbill();
        mUserInfo = ApplicationClass.getUserInfo();

        //Get the views
        mUserName = (TextView)findViewById(R.id.ebill_user_name);
        mUserId = (TextView)findViewById(R.id.ebill_user_id);
        mListView = (ListView)findViewById(android.R.id.list);

        //Load the stored info
        loadInfo();

        //Start the thread to get the ebill
        new EbillGetter().execute();
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

            String ebillString = Connection.getInstance().getUrl(activity, Connection.minervaEbill);

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
            mEbillItems = EbillItem.parseEbill(ebillString);
            mUserInfo = new UserInfo(ebillString);

            //Save it to the instance variable in the Application class
            ApplicationClass.setEbill(mEbillItems);
            ApplicationClass.setUserInfo(mUserInfo);

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
}


