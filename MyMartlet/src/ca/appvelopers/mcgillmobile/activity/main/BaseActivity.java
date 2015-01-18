package ca.appvelopers.mcgillmobile.activity.main;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import java.util.Locale;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien
 * Date: 2014-03-16 11:42
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Update locale and config
        Locale locale = new Locale(App.getLanguage().getLanguageString());
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //Update locale and config
        Locale locale = new Locale(App.getLanguage().getLanguageString());
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * Sets up the toolbar at the top of the activity
     * @return The toolbar
     */
    public Toolbar setUpToolbar(){
        //Get the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //Set is as the action bar
        setSupportActionBar(toolbar);

        return toolbar;
    }
}
