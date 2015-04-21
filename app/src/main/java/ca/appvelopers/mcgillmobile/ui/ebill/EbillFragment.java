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

package ca.appvelopers.mcgillmobile.ui.ebill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.User;
import ca.appvelopers.mcgillmobile.thread.DownloaderThread;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;

/**
 * Displays the user's ebill statements
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class EbillFragment extends BaseFragment {
    /**
     * The user-related TextViews
     */
    private TextView mUserName, mUserId;
    /**
     * The statements ListView
     */
    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        View view = View.inflate(mActivity, R.layout.fragment_ebill, container);

        lockPortraitMode();

        //Title
        mActivity.setTitle(getString(R.string.title_ebill));

        Analytics.getInstance().sendScreen("Ebill");

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
        User user = App.getUserInfo();
        List<Statement> statements = App.getEbill();

        //Set the user info
        if(user != null){
            mUserName.setText(user.getName());
            mUserId.setText(user.getId());
        }

        //Statements Adapter
        EbillAdapter adapter = new EbillAdapter(mActivity, statements);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshView(){
        //Show the user we are reloading
        mActivity.showToolbarProgress(true);

        String html = new DownloaderThread(mActivity, "Ebill Download", Connection.EBILL_URL)
                .execute();

        //If the download was successful, parse and reload the info
        if(html != null){
            Parser.parseEbill(html);
            loadInfo();
        }

        mActivity.showToolbarProgress(false);
    }
}