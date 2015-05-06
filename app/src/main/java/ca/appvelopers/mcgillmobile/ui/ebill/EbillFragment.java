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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.User;
import ca.appvelopers.mcgillmobile.ui.base.BaseFragment;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Parser;
import ca.appvelopers.mcgillmobile.util.thread.DownloaderThread;

/**
 * Displays the user's ebill statements
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class EbillFragment extends BaseFragment {
    /**
     * The user name
     */
    @InjectView(R.id.user_name)
    TextView mUserName;
    /**
     * The user Id
     */
    @InjectView(R.id.user_id)
    TextView mUserId;
    /**
     * The statements ListView
     */
    @InjectView(android.R.id.list)
    RecyclerView mListView;

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
        ButterKnife.inject(this, view);
        lockPortraitMode();
        Analytics.getInstance().sendScreen("Ebill");

        //Title
        mActivity.setTitle(getString(R.string.title_ebill));

        mListView.setLayoutManager(new LinearLayoutManager(mActivity));

        update();

        hideLoadingIndicator();

        return view;
    }

    /**
     * Updates the view
     */
    private void update(){
        User user = App.getUserInfo();
        List<Statement> statements = App.getEbill();

        if(user != null){
            mUserName.setText(user.getName());
            mUserId.setText(user.getId());
        }

        mListView.setAdapter(new EbillAdapter(mActivity, statements));
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

        new DownloaderThread(mActivity, "Ebill Download", Connection.EBILL_URL)
                .execute(new DownloaderThread.Callback() {
                    @Override
                    public void onDownloadFinished(String result){
                        if(result != null){
                            Parser.parseEbill(result);
                            update();
                        }
                        mActivity.showToolbarProgress(false);
                    }
                });
    }
}