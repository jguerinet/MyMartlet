package ca.appvelopers.mcgillmobile.activity;

import android.os.Bundle;
import android.view.Window;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.base.DrawerActivity;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;

/**
 * @author Nhat-Quang Dao
 * Date: 22/01/14, 9:07 PM
 * 
 * This Activity loads the schedule from https://horizon.mcgill.ca/pban1/bwskfshd.P_CrseSchd
 */
public class ScheduleActivity extends DrawerActivity {
    @Override
	public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_schedule);
        super.onCreate(savedInstanceState);

        GoogleAnalytics.sendScreen(this, "Schedule");


    }

}
