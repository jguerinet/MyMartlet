package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerActivity;

//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Ryan Singzon on 14/03/14.
 */
public class MapActivity extends DrawerActivity {

//    private GoogleMap mMap;

    @SuppressLint("NewApi")
     @Override
     public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_map);
        super.onCreate(savedInstanceState);

    }


}
