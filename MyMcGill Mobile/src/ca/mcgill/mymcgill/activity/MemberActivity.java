package ca.mcgill.mymcgill.activity;


import android.os.Bundle;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;

public class MemberActivity extends DrawerActivity {
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_member);
        super.onCreate(savedInstanceState);
    }
}