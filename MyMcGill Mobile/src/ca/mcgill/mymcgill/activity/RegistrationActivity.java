package ca.mcgill.mymcgill.activity;

import android.os.Bundle;
import android.view.Window;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;

/**
 * Created by Ryan Singzon on 19/05/14.
 */
public class RegistrationActivity extends DrawerActivity{


    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_registration);
        super.onCreate(savedInstanceState);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                //Refresh code here if necessary?
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
