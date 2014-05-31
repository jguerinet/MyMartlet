package ca.mcgill.mymcgill.activity;

import android.os.Bundle;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.base.BaseActivity;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;

/**
 * Created by Adnan
 */
public class AboutActivity extends DrawerActivity {
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);

    }
}