package ca.appvelopers.mcgillmobile.activity.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.drawer.DrawerFragmentActivity;
import ca.appvelopers.mcgillmobile.object.Place;
import ca.appvelopers.mcgillmobile.object.PlaceCategory;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;

/**
 * Author: Ryan Singzon
 * Date: 14/03/14 9:49 PM
 */

public class MapActivity extends DrawerFragmentActivity {
    private List<MapPlace> mPlaces;

    private static final LatLng MCGILL = new LatLng(45.504435,-73.576006);

    private TextView mTitle;
    private TextView mAddress;
    private MapPlace mPlaceMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        loadDrawer();

        GoogleAnalytics.sendScreen(this, "Map");

        //Bind the TextViews
        mTitle = (TextView)findViewById(R.id.place_title);
        mAddress = (TextView)findViewById(R.id.place_address);

        mPlaces = new ArrayList<MapPlace>();

        final GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (map !=null){
            //Set the camera's center position
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(MCGILL)
                    .zoom(Constants.DEFAULT_ZOOM)
                    .bearing(Constants.DEFAULT_BEARING)
                    .tilt(0)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //Show the user's location
            map.setMyLocationEnabled(true);

            //Go through all of the places
            for(Place place : App.getPlaces()){
                //Create a MapPlace for this
                Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLatitude(), place.getLongitude()))
                    .draggable(false)
                    .visible(true));

                //Add it to the list
                mPlaces.add(new MapPlace(place, marker));
            }

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //If there was a marker that was selected before set it back to red
                    if(mPlaceMarker != null){
                        mPlaceMarker.mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    //Find the concerned place
                    mPlaceMarker = findPlace(marker);

                    //Set it to blue
                    mPlaceMarker.mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    //Set up the info
                    mTitle.setText(mPlaceMarker.mPlace.getName());
                    mAddress.setText(mPlaceMarker.mPlace.getAddress());

                    return false;
                }
            });

            //Set up the two buttons
            TextView directions = (TextView) findViewById(R.id.map_directions);
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlaceMarker != null) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?f=d &daddr=" +
                                        mPlaceMarker.mMarker.getPosition().latitude + "," +
                                        mPlaceMarker.mMarker.getPosition().longitude));
                        startActivity(intent);
                    }
                }
            });

            TextView favorites = (TextView) findViewById(R.id.map_add_favorite);
            favorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlaceMarker != null) {
                        Toast.makeText(MapActivity.this, mPlaceMarker.mPlace.getName() + " added to Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Set up the spinner
        Spinner filter = (Spinner)findViewById(R.id.map_filter);
        final PlacesAdapter adapter = new PlacesAdapter(this);
        filter.setAdapter(adapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Get the selected category
                PlaceCategory category = adapter.getItem(position);

                //If it's null, show everything
                if(category == null){
                    for(MapPlace place : mPlaces){
                        place.mMarker.setVisible(true);
                    }
                }
                //If not, only show the ones pertaining to the current category
                else{
                    for(MapPlace place : mPlaces){
                        place.mMarker.setVisible(place.mPlace.hasCategory(category));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    class MapPlace{
        private Place mPlace;
        private Marker mMarker;

        MapPlace(Place place, Marker marker){
            this.mPlace = place;
            this.mMarker = marker;
        }
    }

    private MapPlace findPlace(Marker marker){
        for(MapPlace mapPlace : mPlaces){
            if(mapPlace.mMarker.equals(marker)){
                return mapPlace;
            }
        }

        return null;
    }
}
