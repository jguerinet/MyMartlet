package ca.appvelopers.mcgillmobile.fragment.ebill;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.MainActivity;
import ca.appvelopers.mcgillmobile.object.EbillItem;
import ca.appvelopers.mcgillmobile.object.UserInfo;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.view.DialogHelper;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 5:21 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class EbillFragment extends Fragment {
    private MainActivity mActivity;

    private List<EbillItem> mEbillItems = new ArrayList<EbillItem>();
    private UserInfo mUserInfo;
    private TextView mUserName, mUserId;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(mActivity, R.layout.fragment_ebill, null);

        //Title
        mActivity.setTitle(getString(R.string.title_ebill));

        GoogleAnalytics.sendScreen(mActivity, "Ebill");

        //Get the initial info from the App
        mEbillItems = App.getEbill();
        mUserInfo = App.getUserInfo();

        //Get the views
        mUserName = (TextView)view.findViewById(R.id.ebill_user_name);
        mUserId = (TextView)view.findViewById(R.id.ebill_user_id);
        mListView = (ListView)view.findViewById(android.R.id.list);

        //Load the stored info
        loadInfo();

        return view;
    }

    private void loadInfo(){
        if(mUserInfo != null){
            mUserName.setText(mUserInfo.getName());
            mUserId.setText(mUserInfo.getId());
        }
        EbillAdapter adapter = new EbillAdapter(mActivity, mEbillItems);
        mListView.setAdapter(adapter);
    }

    private class EbillGetter extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute(){
            //Show the user we are refreshing his content
            mActivity.showToolbarSpinner(true);
        }

        //Retrieve content from transcript page
        @Override
        protected Boolean doInBackground(Void... params){
            String ebillString = Connection.getInstance().getUrl(mActivity, Connection.EBILL);

            if(ebillString == null){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogHelper.showNeutralAlertDialog(mActivity, mActivity.getResources().getString(R.string.error),
                                mActivity.getResources().getString(R.string.error_other));
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

            mActivity.showToolbarSpinner(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh, menu);
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