package ca.mcgill.mymcgill.activity.ebill;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.EbillItem;
import ca.mcgill.mymcgill.object.UserInfo;
import ca.mcgill.mymcgill.util.ApplicationClass;
import ca.mcgill.mymcgill.util.Connection;

public class EbillActivity extends ListActivity {
	private List<EbillItem> mEbillItems = new ArrayList<EbillItem>();
    private UserInfo mUserInfo;

    private TextView mUserName, mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_ebill);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the initial info from the ApplicationClass
        mEbillItems = ApplicationClass.getEbill();
        mUserInfo = ApplicationClass.getUserInfo();

        //Get the views
        mUserName = (TextView)findViewById(R.id.ebill_user_name);
        mUserId = (TextView)findViewById(R.id.ebill_user_id);

        boolean refresh = !mEbillItems.isEmpty();
        if(refresh){
            loadInfo();
        }

        //Start the thread to get the ebill
        //If the ebill list is not empty, we only need to refresh
        new EbillGetter(refresh).execute();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadInfo(){
        if(mUserInfo != null){
            mUserName.setText(mUserInfo.getName());
            mUserId.setText(mUserInfo.getId());
        }
        EbillAdapter adapter = new EbillAdapter(this, mEbillItems);
        setListAdapter(adapter);
    }

    private class EbillGetter extends AsyncTask<Void, Void, Void> {
        private boolean mRefresh;
        private ProgressDialog mProgressDialog;

        public EbillGetter(boolean refresh){
            this.mRefresh = refresh;
        }

        @Override
        protected void onPreExecute(){
            //Only show a ProgressDialog if we are not refreshing the content but
            //downloading it for the first timeC
            if(!mRefresh){
                mProgressDialog = new ProgressDialog(EbillActivity.this);
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
            String ebillString = Connection.getInstance().getUrl(Connection.minervaEbill);

            mEbillItems.clear();

            Document doc = Jsoup.parse(ebillString);
            Element ebillTable = doc.getElementsByClass("datadisplaytable").first();
            Elements ebillRows = ebillTable.getElementsByTag("tr");
            getEBill(ebillRows);

            //Parse the user info
            Elements userInfo = ebillTable.getElementsByTag("caption");
            String id = userInfo.get(0).text().replace("Statements for ", "");
            String[] userInfoItems = id.split("-");
            mUserInfo = new UserInfo(userInfoItems[1].trim(), userInfoItems[0].trim());

            //Save it to the instance variable in the Application class
            ApplicationClass.setEbill(mEbillItems);
            ApplicationClass.setUserInfo(mUserInfo);

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

        //parser algorithm
        private void getEBill(Elements rows){
            for (int i = 2; i < rows.size(); i+=2) {
                Element row = rows.get(i);
                Elements cells = row.getElementsByTag("td");
                String statementDate = cells.get(0).text();
                String dueDate = cells.get(3).text();
                String amountDue = cells.get(5).text();
                mEbillItems.add(new EbillItem(statementDate, dueDate, amountDue));
            }
        }
    }
}


