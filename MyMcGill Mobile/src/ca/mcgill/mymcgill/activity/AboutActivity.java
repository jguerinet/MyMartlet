package ca.mcgill.mymcgill.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TabHost;
import android.widget.TextView;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerFragmentActivity;

/**
 * Created by Adnan2
 */
public class AboutActivity extends DrawerActivity implements ActionBar.TabListener {
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // tab listener
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // add tabs

        TextView teamName = new TextView(this);
        teamName.setText(getResources().getString(R.string.about_team_name));
        teamName.setTextColor(Color.BLACK);
        teamName.setPadding(0,20,0,0);

        TextView help = new TextView(this);
        help.setText(getResources().getString(R.string.about_help));
        help.setTextColor(Color.BLACK);
        help.setGravity(Gravity.CENTER);
        help.setPadding(0,20,0,0);


        ActionBar.Tab teamNameTab = actionBar.newTab().setCustomView(teamName).setTabListener(tabListener);
        ActionBar.Tab helpTab = actionBar.newTab().setCustomView(help).setTabListener(tabListener);


        actionBar.addTab(teamNameTab);
        actionBar.addTab(helpTab);


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}