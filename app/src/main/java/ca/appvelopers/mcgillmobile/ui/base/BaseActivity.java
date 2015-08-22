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

package ca.appvelopers.mcgillmobile.ui.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Locale;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * The base class for all activities
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * The progress bar shown in the toolbar
     */
    private ProgressBar mToolbarProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Update locale and config (it sometimes get reset in between activities)
        updateLocale();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //Update locale and config (it gets reset when a configuration is changed)
        updateLocale();
    }

    /**
     * Updates the locale
     */
    public void updateLocale(){
        Locale locale = new Locale(App.getLanguage().toString());
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * Sets up the toolbar as the activity's action bar.
     *  Must be declared in the activity's layout file
     *
     * @param homeAsUp True if the home button should be displayed as up, false otherwise
     * @return The toolbar
     */
    public Toolbar setUpToolbar(boolean homeAsUp){
        //Get the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //Set is as the action bar
        setSupportActionBar(toolbar);

        //Set up the progress bar
        mToolbarProgressBar = (ProgressBar)toolbar.findViewById(R.id.toolbar_progress);

        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUp);

        return toolbar;
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
     * Shows or hides the progress bar in the toolbar
     *
     * @param visible True if it should be visible, false otherwise
     */
    public void showToolbarProgress(boolean visible){
        mToolbarProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
