/*
 * Copyright 2014-2016 Appvelopers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.appvelopers.mcgillmobile.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.formgenerator.TextViewFormItem;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.ui.dialog.list.PlaceTypeListAdapter;
import ca.appvelopers.mcgillmobile.util.Analytics;
import timber.log.Timber;

/**
 * Displays a campus map
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 */
public class MapActivity extends DrawerActivity {
    private static final int LOCATION_REQUEST = 101;
    /**
     * Coordinates used to center the map initially
     */
    private static final LatLng MCGILL = new LatLng(45.504435, -73.576006);
    /**
     * Info container used to show the current place's detail
     */
    @Bind(R.id.info_container)
    protected LinearLayout mInfoContainer;
    /**
     * {@link FormGenerator} container for the filter
     */
    @Bind(R.id.container)
    protected LinearLayout mContainer;
    /**
     * Current place's title
     */
    @Bind(R.id.place_title)
    protected TextView mTitle;
    /**
     * Current place's address
     */
    @Bind(R.id.place_address)
    protected TextView mAddress;
    /**
     * Button to get directions to a place
     */
    @Bind(R.id.directions)
    protected Button mDirections;
    /**
     * Button to add or remove a place from the user's favorites
     */
    @Bind(R.id.map_favorite)
    protected Button mFavorite;
    /**
     * Primary color for the {@link TextView} drawables
     */
    @BindColor(R.color.red)
    protected @ColorInt int primaryColor;
    /**
     * Fragment containing the map
     */
    private SupportMapFragment mFragment;
    /**
     * Map instance
     */
    private GoogleMap mMap;
    /**
     * List of places on the map
     */
    private List<MapPlace> mPlaces;
    /**
     * List of the user's favorite places
     */
    private List<Place> mFavoritePlaces;
    /**
     * Currently shown map places
     */
    private List<MapPlace> mCurrentPlaces;
    /**
     * Currently shown place
     */
    private MapPlace mPlace;
    /**
     * Currently selected category
     */
    private PlaceType mType;
    /**
     * Current search String
     */
    private String mSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        Analytics.get().sendScreen("Map");

        //Set up the initial information
        mPlaces = new ArrayList<>();
        mCurrentPlaces = new ArrayList<>();
        mFavoritePlaces = App.getFavoritePlaces();
        mSearchString = "";
        mType = new PlaceType(false);

        FormGenerator fg = FormGenerator.bind(this, mContainer);

        //Set up the place filter
        final TextViewFormItem typeView = fg.text(mType.toString());

        typeView.leftIcon(R.drawable.ic_location)
                .rightIcon(R.drawable.ic_chevron_right, R.color.grey)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.list(MapActivity.this, R.string.map_filter,
                                new PlaceTypeListAdapter(mType) {
                                    @Override
                                    public void onPlaceTypeSelected(PlaceType type) {
                                        mType = type;

                                        //Update the text
                                        typeView.view().setText(mType.toString());

                                        //Update the filtered places
                                        filterByCategory();
                                    }
                                });
                    }
                });

        //Tint the drawables for the two buttons
        Utils.setTint(mDirections, 0, primaryColor);
        Utils.setTint(mFavorite, 0, primaryColor);

        FragmentManager fragmentManager = getSupportFragmentManager();
        //Get the MapFragment
        mFragment = (SupportMapFragment)fragmentManager.findFragmentById(R.id.map);
        //If it's null, initialize it and put it in its view
        if (mFragment == null) {
            mFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.map, mFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //If the map is null, bind it and add the markers
        if (mMap == null) {
            mMap = mFragment.getMap();
            //Set the camera's center position
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(MCGILL)
                    .zoom(14)
                    .bearing(-54)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //Show the user's location if we have the permission to
            if (Utils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                    LOCATION_REQUEST)) {
                mMap.setMyLocationEnabled(true);
            }
            //If we don't, it will be requested

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

            //Filter
            filterByCategory();

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //If there was a marker that was selected before set it back to red
                    if (mPlace != null) {
                        mPlace.mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_RED));
                    }
                    //Pull up the info container
                    mInfoContainer.setVisibility(View.VISIBLE);

                    //Find the concerned place
                    mPlace = null;
                    for (MapPlace mapPlace : mPlaces) {
                        if (mapPlace.mMarker.equals(marker)) {
                            mPlace = mapPlace;
                        }
                    }

                    if (mPlace == null) {
                        Timber.e("Tapped place marker was not found");
                        return false;
                    }

                    //Set it to blue
                    mPlace.mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE));

                    //Set up the info
                    mTitle.setText(mPlace.mPlace.getName());
                    mAddress.setText(mPlace.mPlace.getAddress());

                    //Set up the favorite text
                    if (mFavoritePlaces.contains(mPlace.mPlace)) {
                        mFavorite.setText(R.string.map_favorites_remove);
                    } else {
                        mFavorite.setText(R.string.map_favorites_add);
                    }

                    return false;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        //Get the SearchView
        MenuItem item = menu.findItem(R.id.action_search);
        Assert.assertNotNull(getSupportActionBar());
        final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        final int textViewID = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        final AutoCompleteTextView searchTextView =
                (AutoCompleteTextView) searchView.findViewById(textViewID);
        try {
            //Set the cursor to the same color as the text
            Field cursorDrawable = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawable.setAccessible(true);
            cursorDrawable.set(searchTextView, 0);
        } catch (Exception e) {
            Timber.e(e, "Cannot change color of cursor");
        }

        //Set up the query listener
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchString = query;
                filterBySearchString();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchString = newText;
                filterBySearchString();
                return false;
            }
        });

        //Reset the search view
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchString = "";
                filterBySearchString();
                return false;
            }
        });

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                //Check if the permission has been granted
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Show the user on the map if that is the case
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected @Homepage.Type int getCurrentPage() {
        return Homepage.MAP;
    }

    /**
     * Opens Google Maps with directions to the chosen place
     */
    @OnClick(R.id.directions)
    protected void directions() {
        //Open Google Maps
        if (mPlace != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?f=d &daddr=" +
                            mPlace.mMarker.getPosition().latitude + "," +
                            mPlace.mMarker.getPosition().longitude));
            startActivity(intent);
        }
    }

    /**
     * Adds or remove a place from the user's favorites
     */
    @OnClick(R.id.map_favorite)
    protected void favorites() {
        if (mPlace != null) {
            //Check if it was in the favorites
            if (mFavoritePlaces.contains(mPlace.mPlace)) {
                mFavoritePlaces.remove(mPlace.mPlace);

                //Alert the user
                Toast.makeText(this, getString(R.string.map_favorites_removed,
                        mPlace.mPlace.getName()), Toast.LENGTH_SHORT).show();

                //Change the text to "Add Favorites"
                mFavorite.setText(R.string.map_favorites_add);

                //If we are in the favorites category, we need to hide this pin
                if (mType.getName().equals(PlaceType.FAVORITES)) {
                    mPlace.mMarker.setVisible(false);
                }
            } else {
                mFavoritePlaces.add(mPlace.mPlace);

                //Alert the user
                Toast.makeText(this, getString(R.string.map_favorites_added,
                        mPlace.mPlace.getName()), Toast.LENGTH_SHORT).show();

                //Change the text to "Remove Favorites"
                mFavorite.setText(getString(R.string.map_favorites_remove));
            }

            //Save the places
            App.setFavoritePlaces(mFavoritePlaces);
        }
    }

    /**
     * Shows or hides the given place
     *
     * @param place   The place
     * @param visible True if the place should be visible, false otherwise
     */
    private void showPlace(MapPlace place, boolean visible) {
        place.mMarker.setVisible(visible);
        if (visible) {
            mCurrentPlaces.add(place);
        }
    }

    /**
     * Filters the current places by the selected category
     */
    private void filterByCategory() {
        //Reset the current places
        mCurrentPlaces.clear();

        //Go through the places
        for (MapPlace place : mPlaces) {
            switch (mType.getName()) {
                //Show all of the places
                case PlaceType.ALL:
                    showPlace(place, true);
                    break;
                //Show only the favorite places
                case PlaceType.FAVORITES:
                    showPlace(place, mFavoritePlaces.contains(place.mPlace));
                    break;
                //Show the places for the current category
                default:
                    showPlace(place, place.mPlace.isOfType(mType));
                    break;
            }
        }

        //Filter also by the search String if there is one
        filterBySearchString();
    }

    /**
     * Filters the current places by the entered search String
     */
    private void filterBySearchString() {
        //If there is no search String, just show everything
        if (mSearchString.isEmpty()) {
            for (MapPlace mapPlace : mCurrentPlaces) {
                mapPlace.mMarker.setVisible(true);
            }
            return;
        }

        //Keep track of the number of places you're showing
        int numberOfPlaces = 0;
        MapPlace place = null;
        for (MapPlace mapPlace : mCurrentPlaces) {
            if (mapPlace.mPlace.getName().toLowerCase().contains(mSearchString.toLowerCase())) {
                mapPlace.mMarker.setVisible(true);
                numberOfPlaces ++;
                place = mapPlace;
            } else {
                mapPlace.mMarker.setVisible(false);
            }
        }

        //If you're showing only one place, focus on that place
        if (numberOfPlaces == 1) {
            focusPlace(place);
        }
    }

    /**
     * Focuses on the given place
     *
     * @param place The place
     */
    private void focusPlace(MapPlace place) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(place.mPlace.getLatitude(), place.mPlace.getLongitude())));
    }

    /**
     * Represents a place with its associated marker on the map
     */
    class MapPlace {
        /**
         * The place
         */
        private Place mPlace;
        /**
         * The map marker
         */
        private Marker mMarker;

        /**
         * Default Constructor
         *
         * @param place  The place
         * @param marker The marker
         */
        MapPlace(Place place, Marker marker) {
            this.mPlace = place;
            this.mMarker = marker;
        }
    }
}