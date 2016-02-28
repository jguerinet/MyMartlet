/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.guerinet.utils.Utils;

import junit.framework.Assert;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.retrofit.McGillService;
import ca.appvelopers.mcgillmobile.ui.dialog.DialogHelper;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.manager.LanguageManager;
import dagger.Lazy;

/**
 * The base class for all activities
 * @author Julien Guerinet
 * @since 1.0.0
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    /**
     * The toolbar
     */
    @Nullable @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    /**
     * The progress bar shown in the toolbar
     */
    @Nullable @Bind(R.id.toolbar_progress)
    protected ProgressBar toolbarProgress;
    /**
     * The {@link McGillService} instance
     */
    @Inject
    protected McGillService mcGillService;
    /**
     * {@link Analytics} instance
     */
    @Inject
    protected Analytics analytics;
    /**
     * The {@link LanguageManager} instance
     */
    @Inject
    protected LanguageManager languageManager;
    /**
     * {@link ConnectivityManager} instance, lazily loaded
     */
    @Inject
    protected Lazy<ConnectivityManager> connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.component(this).inject(this);
        //Update locale and config (it sometimes get reset in between activities)
        updateLocale();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Update locale and config (it gets reset when a configuration is changed)
        updateLocale();
    }

    /**
     * Updates the locale
     */
    private void updateLocale() {
        Locale locale = new Locale(languageManager.getCode());
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Go back if the home button is clicked
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the toolbar as the activity's action bar.
     *  Must be declared in the activity's layout file
     *
     * @param homeAsUp True if the home button should be displayed as up, false otherwise
     */
    protected void setUpToolbar(boolean homeAsUp) {
        Assert.assertNotNull(toolbar);

        //Set is as the action bar
        setSupportActionBar(toolbar);

        Assert.assertNotNull(getSupportActionBar());
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUp);
    }

    /**
     * Shows or hides the progress bar in the toolbar
     *
     * @param visible True if it should be visible, false otherwise
     */
    public void showToolbarProgress(boolean visible) {
        Assert.assertNotNull(toolbarProgress);
        toolbarProgress.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public McGillService getMcGillService() {
        //TODO This shouldn't exist
        return mcGillService;
    }

    /**
     * Checks if we can refresh the information on the page and shows the toolbar progress bar if so
     *
     * @return True if the content can be refreshed, false otherwise
     */
    public boolean canRefresh() {
        //Check internet connection
        if (!Utils.isConnected(connectivityManager.get())) {
            DialogHelper.error(this, R.string.error_no_internet);
            return false;
        }

        showToolbarProgress(true);

        return true;
    }

}
