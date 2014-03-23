package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

        final LatLng MCGILL = new LatLng(45.504723,-73.576975);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap!=null){
            Marker mcgill = mMap.addMarker(new MarkerOptions().position(MCGILL).title("McGill"));
            mcgill.setVisible(false);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MCGILL, 16));
            mMap.setMyLocationEnabled(true);
        }


    }


}
