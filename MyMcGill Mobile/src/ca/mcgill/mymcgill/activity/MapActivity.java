package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.maps.GoogleMap;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerFragmentActivity;

/**
 * Created by Ryan Singzon on 14/03/14.
 */
public class MapActivity extends DrawerFragmentActivity {


    private GoogleMap mMap;

    @SuppressLint("NewApi")
     @Override
     public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        showDrawer(true);

//        if (mMap == null) {
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
//                    R.id.map)).getMap();
//
//            // check if map is created successfully or not
//            if (mMap == null) {
//                Toast.makeText(getApplicationContext(),
//                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
    }


}
