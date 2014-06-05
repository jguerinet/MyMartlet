package ca.appvelopers.mcgillmobile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerFragmentActivity;
import ca.appvelopers.mcgillmobile.object.HomePage;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Created by Ryan Singzon on 14/03/14.
 */
public class MapActivity extends DrawerFragmentActivity {

    private boolean mDoubleBackToExit;
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

    @Override
    public void onBackPressed(){
        if(App.getHomePage() != HomePage.CAMPUS_MAP){
            startActivity(new Intent(MapActivity.this, App.getHomePage().getHomePageClass()));
            super.onBackPressed();
        }
        else{
            if (mDoubleBackToExit) {
                super.onBackPressed();
                return;
            }
            this.mDoubleBackToExit = true;
            Toast.makeText(this, R.string.back_toaster_message, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDoubleBackToExit=false;
                }
            }, 2000);
        }
    }
}
