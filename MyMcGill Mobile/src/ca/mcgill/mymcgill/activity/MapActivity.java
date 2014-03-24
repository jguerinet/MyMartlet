package ca.mcgill.mymcgill.activity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.activity.drawer.DrawerFragmentActivity;
import ca.mcgill.mymcgill.util.Constants;

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
        loadDrawer();

        final LatLng MCGILL = new LatLng(45.503835,-73.574787);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap!=null){
            Marker mcgill = mMap.addMarker(new MarkerOptions().position(MCGILL).title("McGill"));
            mcgill.setVisible(false);

            mMap.setMyLocationEnabled(true);

            //Change bearing so that north is not at an angle
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(MCGILL)
                    .zoom(Constants.DEFAULT_ZOOM)
                    .bearing(Constants.DEFAULT_BEARING)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //Set the centre of the map to the user's location
            Location myLocation = mMap.getMyLocation();
            if (myLocation != null) {
                LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                CameraPosition myPosition = new CameraPosition.Builder()
                        .target(myLatLng)
                        .zoom(Constants.DEFAULT_ZOOM)
                        .bearing(Constants.DEFAULT_BEARING)
                        .tilt(0)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(myPosition));
            }
        }
    }
}
