package ca.mcgill.mymcgill.activity.base;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.Locale;

import ca.mcgill.mymcgill.util.ApplicationClass;

/**
 * Author: Julien
 * Date: 2014-03-16 11:42
 */
public class BaseFragmentActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Update locale and config
        Locale locale = new Locale(ApplicationClass.getLanguage().getLanguageString());
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        //Update locale and config
        Locale locale = new Locale(ApplicationClass.getLanguage().getLanguageString());
        Locale.setDefault(locale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}
