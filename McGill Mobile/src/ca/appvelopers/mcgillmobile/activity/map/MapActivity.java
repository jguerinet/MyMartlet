package ca.appvelopers.mcgillmobile.activity.map;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

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
    private List<Place> mFavoritePlaces;

    private static final LatLng MCGILL = new LatLng(45.504435,-73.576006);

    private TextView mTitle;
    private TextView mAddress;
    private TextView mFavorite;
    private MapPlace mPlaceMarker;
    private LinearLayout mInfoContainer;
    private PlaceCategory mCategory;
    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        loadDrawer();

        GoogleAnalytics.sendScreen(this, "Map");

        //Bind the TextViews
        mInfoContainer = (LinearLayout)findViewById(R.id.info_container);
        mTitle = (TextView)findViewById(R.id.place_title);
        mAddress = (TextView)findViewById(R.id.place_address);
        mFavorite = (TextView)findViewById(R.id.map_favorite);

        mPlaces = new ArrayList<MapPlace>();
        mFavoritePlaces = App.getFavoritePlaces();

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap !=null) {
            //Set the camera's center position
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(MCGILL)
                    .zoom(Constants.DEFAULT_ZOOM)
                    .bearing(Constants.DEFAULT_BEARING)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //Show the user's location
            mMap.setMyLocationEnabled(true);

            //Go through all of the places
            for (Place place : App.getPlaces()) {
                //Create a MapPlace for this
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(place.getLatitude(), place.getLongitude()))
                        .draggable(false)
                        .visible(true));

                //Add it to the list
                mPlaces.add(new MapPlace(place, marker));
            }

            //Get search intent
            Intent intent = getIntent();
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                findPlaceByString(query);
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //If there was a marker that was selected before set it back to red
                    if (mPlaceMarker != null) {
                        mPlaceMarker.mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    //If it was null, we need to pull up the info container
                    else {
                        mInfoContainer.setVisibility(View.VISIBLE);
                    }

                    //Find the concerned place
                    mPlaceMarker = findPlace(marker);

                    //Set it to blue
                    mPlaceMarker.mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    //Set up the info
                    mTitle.setText(mPlaceMarker.mPlace.getName());
                    mAddress.setText(mPlaceMarker.mPlace.getAddress());

                    if (mFavoritePlaces.contains(mPlaceMarker.mPlace)) {
                        mFavorite.setText(getString(R.string.map_favorites_remove));
                    } else {
                        mFavorite.setText(getString(R.string.map_favorites_add));
                    }

                    return false;
                }
            });

            //Set up the two buttons
            TextView directions = (TextView) findViewById(R.id.map_directions);
            directions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlaceMarker != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?f=d &daddr=" +
                                        mPlaceMarker.mMarker.getPosition().latitude + "," +
                                        mPlaceMarker.mMarker.getPosition().longitude));
                        startActivity(intent);
                    }
                }
            });

            //Set up the spinner
            final Spinner filter = (Spinner) findViewById(R.id.map_filter);
            final PlacesAdapter adapter = new PlacesAdapter(this);
            filter.setAdapter(adapter);
            filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Get the selected category
                    mCategory = adapter.getItem(position);

                    //If it's null, show everything
                    if (mCategory == null) {
                        for (MapPlace place : mPlaces) {
                            place.mMarker.setVisible(true);
                        }
                    }
                    //Check if the favorites was selected
                    else if (mCategory == PlaceCategory.FAVORITES) {
                        for (MapPlace place : mPlaces) {
                            place.mMarker.setVisible(mFavoritePlaces.contains(place.mPlace));
                        }
                    }
                    //If not, only show the ones pertaining to the current category
                    else {
                        for (MapPlace place : mPlaces) {
                            place.mMarker.setVisible(place.mPlace.hasCategory(mCategory));
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            mFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlaceMarker != null) {
                        //Check if it was in the favorites
                        if (mFavoritePlaces.contains(mPlaceMarker.mPlace)) {
                            mFavoritePlaces.remove(mPlaceMarker.mPlace);
                            Toast.makeText(MapActivity.this, getString(R.string.map_favorites_removed, mPlaceMarker.mPlace.getName()), Toast.LENGTH_SHORT).show();
                            mFavorite.setText(getString(R.string.map_favorites_add));

                            //If we are in the favorites category, we need to hide this pin
                            if(mCategory == PlaceCategory.FAVORITES){
                                mPlaceMarker.mMarker.setVisible(false);
                            }
                        } else {
                            mFavoritePlaces.add(mPlaceMarker.mPlace);
                            Toast.makeText(MapActivity.this, getString(R.string.map_favorites_added, mPlaceMarker.mPlace.getName()), Toast.LENGTH_SHORT).show();
                            mFavorite.setText(getString(R.string.map_favorites_remove));
                        }
                        App.setFavoritePlaces(mFavoritePlaces);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);

        // Get the SearchView and set the search configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the search activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            findPlaceByString(query);
        }
    }

    private void findPlaceByString(String query) {
        for (MapPlace mapPlace : mPlaces) {
            if (containsString(mapPlace.mPlace.getName(), query)) {
                mapPlace.mMarker.setVisible(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mapPlace.mPlace.getLatitude(), mapPlace.mPlace.getLongitude())));
            } else {
                mapPlace.mMarker.setVisible(false);
            }
        }
    }

    private boolean containsString(String container, String query) {
        return container.toLowerCase().contains(query.toLowerCase());
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

    private MapPlace findMarker(Place place) {
        for (MapPlace mapPlace : mPlaces) {
            if(mapPlace.mPlace.equals(place)) {
                return mapPlace;
            }
        }
        return null;
    }
}
