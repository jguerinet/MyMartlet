/*
 * Copyright 2014-2015 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.fragment.ebill;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.exception.MinervaLoggedOutException;
import ca.appvelopers.mcgillmobile.exception.NoInternetException;
import ca.appvelopers.mcgillmobile.model.EbillItem;
import ca.appvelopers.mcgillmobile.model.UserInfo;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.ui.view.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;

public class EbillFragment extends BaseFragment {
    private List<EbillItem> mEbillItems = new ArrayList<EbillItem>();
    private UserInfo mUserInfo;
    private TextView mUserName, mUserId;
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = View.inflate(mActivity, R.layout.fragment_ebill, null);

        lockPortraitMode();

        //Title
        mActivity.setTitle(getString(R.string.title_ebill));

        Analytics.getInstance().sendScreen("Ebill");

        //Get the initial info from the App
        mEbillItems = App.getEbill();
        mUserInfo = App.getUserInfo();

        //Get the views
        mUserName = (TextView)view.findViewById(R.id.ebill_user_name);
        mUserId = (TextView)view.findViewById(R.id.ebill_user_id);
        mListView = (ListView)view.findViewById(android.R.id.list);

        //Load the stored info
        loadInfo();

        hideLoadingIndicator();

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
            mActivity.showToolbarProgress(true);
        }

        //Retrieve content from transcript page
        @Override
        protected Boolean doInBackground(Void... params){
            try{
                String ebillString = Connection.getInstance().get(Connection.EBILL_URL);
                mEbillItems.clear();

                //Parse the ebill and the user info
                Parser.parseEbill(ebillString);
                Parser.parseUserInfo(ebillString);

                //Save it to the instance variable
                mEbillItems = App.getEbill();
                mUserInfo = App.getUserInfo();

                return true;
            } catch(MinervaLoggedOutException e){
                //TODO
                e.printStackTrace();
            } catch(IOException e){
                DialogHelper.showNeutralAlertDialog(mActivity, mActivity.getString(R.string.error),
                        mActivity.getString(R.string.error_other));
            } catch(NoInternetException e){
                e.printStackTrace();
            }
            return false;
        }

        //Update or create transcript object and display data
        @Override
        protected void onPostExecute(Boolean loadInfo){
            if(loadInfo){
                //Reload the info in the views
                loadInfo();
            }

            mActivity.showToolbarProgress(false);
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