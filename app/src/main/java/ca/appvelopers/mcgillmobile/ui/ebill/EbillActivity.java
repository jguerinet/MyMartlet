/*
 * Copyright 2014-2017 Julien Guerinet
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.exception.MinervaException;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Displays the user's ebill statements
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class EbillActivity extends DrawerActivity {
    /**
     * List of statements
     */
    @BindView(android.R.id.list)
    protected RecyclerView mList;
    /**
     * Adapter for the list of {@link Statement}s
     */
    private EbillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebill);
        ButterKnife.bind(this);
        analytics.sendScreen("Ebill");

        mList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EbillAdapter();
        mList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.EBILL;
    }

    /**
     * Refreshes the list of statements
     */
    protected void refresh() {
        if (!canRefresh()) {
            return;
        }

        mcGillService.ebill().enqueue(new Callback<List<Statement>>() {
            @Override
            public void onResponse(Call<List<Statement>> call, Response<List<Statement>> response) {
                App.setEbill(response.body());
                showToolbarProgress(false);
                adapter.update();
            }

            @Override
            public void onFailure(Call<List<Statement>> call, Throwable t) {
                Timber.e(t, "Error refreshing the ebill");
                showToolbarProgress(false);
                //If this is a MinervaException, broadcast it
                if (t instanceof MinervaException) {
                    LocalBroadcastManager.getInstance(EbillActivity.this)
                            .sendBroadcast(new Intent(Constants.BROADCAST_MINERVA));
                } else {
                    DialogHelper.error(EbillActivity.this, R.string.error_other);
                }
            }
        });
    }
}