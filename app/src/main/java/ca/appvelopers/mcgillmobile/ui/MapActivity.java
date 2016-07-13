/*
 * Copyright 2014-2016 Julien Guerinet
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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
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
import com.google.android.gms.maps.OnMapReadyCallback;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.ui.dialog.list.PlaceTypeListAdapter;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;
import ca.appvelopers.mcgillmobile.util.manager.PlacesManager;
import timber.log.Timber;

/**
 * Displays a campus map
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 */
public class MapActivity extends DrawerActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {
    private static final int LOCATION_REQUEST = 101;
    /**
     * Info container used to show the current place's detail
     */
    @BindView(R.id.info_container)
    protected LinearLayout infoContainer;
    /**
     * {@link FormGenerator} container for the filter
     */
    @BindView(R.id.container)
    protected LinearLayout container;
    /**
     * Current place's title
     */
    @BindView(R.id.place_title)
    protected TextView title;
    /**
     * Current place's address
     */
    @BindView(R.id.place_address)
    protected TextView address;
    /**
     * Button to get directions to a place
     */
    @BindView(R.id.directions)
    protected Button directions;
    /**
     * Button to add or remove a place from the user's favorites
     */
    @BindView(R.id.map_favorite)
    protected Button favorite;
    /**
     * {@link PlacesManager} instance
     */
    @Inject
    protected PlacesManager placesManager;
    /**
     * Fragment containing the map
     */
    private GoogleMap map;
    /**
     * Total list of places with their associated markers
     */
    private List<Pair<Place, Marker>> places;
    /**
     * Currently shown map places with their associated markers
     */
    private List<Pair<Place, Marker>> shownPlaces;
    /**
     * Currently shown place with its associated marker
     */
    private Pair<Place, Marker> place;
    /**
     * Currently selected category
     */
    private PlaceType type;
    /**
     * Current search String
     */
    private String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        App.component(this).inject(this);
        analytics.sendScreen("Map");

        //Set up the initial information
        places = new ArrayList<>();
        shownPlaces = new ArrayList<>();
        searchString = "";
        type = new PlaceType(false);

        FormGenerator fg = FormGenerator.bind(this, container);

        //Set up the place filter
        fg.text(type.getString(this, languageManager.get()))
                .leftIcon(R.drawable.ic_location)
                .rightIcon(R.drawable.ic_chevron_right, Color.GRAY)
                .onClick(new TextViewFormItem.OnClickListener() {
                    @Override
                    public void onClick(final TextViewFormItem item) {
                        DialogUtils.list(MapActivity.this, R.string.map_filter,
                                new PlaceTypeListAdapter(MapActivity.this, type) {
                                    @Override
                                    public void onPlaceTypeSelected(PlaceType type) {
                                        MapActivity.this.type = type;

                                        //Update the text
                                        item.view().setText(type.getString(MapActivity.this,
                                                languageManager.get()));

                                        //Update the filtered places
                                        filterByCategory();
                                    }
                                });
                    }
                })
                .build();

        FragmentManager manager = getSupportFragmentManager();
        //Get the MapFragment
        SupportMapFragment fragment = (SupportMapFragment) manager.findFragmentById(R.id.map);
        //If it's null, initialize it and put it in its view
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            manager.beginTransaction()
                    .replace(R.id.map, fragment)
                    .addToBackStack(null)
                    .commit();
        }

        fragment.getMapAsync(this);
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
                searchString = query;
                filterBySearchString();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchString = newText;
                filterBySearchString();
                return false;
            }
        });

        //Reset the search view
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchString = "";
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
                    if (map != null) {
                        //noinspection MissingPermission
                        map.setMyLocationEnabled(true);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected @HomepageManager.Homepage
    int getCurrentPage() {
        return HomepageManager.MAP;
    }

    /**
     * Opens Google Maps with directions to the chosen place
     */
    @OnClick(R.id.directions)
    protected void directions() {
        //Open Google Maps
        if (place != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?f=d &daddr=" +
                            place.second.getPosition().latitude + "," +
                            place.second.getPosition().longitude));
            startActivity(intent);
        }
    }

    /**
     * Adds or remove a place from the user's favorites
     */
    @OnClick(R.id.map_favorite)
    protected void favorites() {
        if (place != null) {
            String message;
            //Check if it was in the favorites
            if (placesManager.isFavorite(place.first)) {
                placesManager.removeFavorite(place.first);

                //Set the toast message
                message = getString(R.string.map_favorites_removed, place.first.getName());

                //Change the text to "Add Favorites"
                favorite.setText(R.string.map_favorites_add);

                //If we are in the favorites category, we need to hide this pin
                if (type.getId() == PlaceType.FAVORITES) {
                    place.second.setVisible(false);
                }
            } else {
                placesManager.addFavorite(place.first);

                //Set the toast message
                message = getString(R.string.map_favorites_added, place.first.getName());

                //Change the text to "Remove Favorites"
                favorite.setText(getString(R.string.map_favorites_remove));
            }

            //Alert the user
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Shows or hides the given place
     *
     * @param place   The place
     * @param visible True if the place should be visible, false otherwise
     */
    private void showPlace(Pair<Place, Marker> place, boolean visible) {
        place.second.setVisible(visible);
        if (visible) {
            shownPlaces.add(place);
        }
    }

    /**
     * Filters the current places by the selected category
     */
    private void filterByCategory() {
        //Reset the current places
        shownPlaces.clear();

        //Go through the places
        for (Pair<Place, Marker> place : places) {
            switch (type.getId()) {
                //Show all of the places
                case PlaceType.ALL:
                    showPlace(place, true);
                    break;
                //Show only the favorite places
                case PlaceType.FAVORITES:
                    showPlace(place, placesManager.isFavorite(place.first));
                    break;
                //Show the places for the current category
                default:
                    showPlace(place, place.first.isOfType(type));
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
        if (searchString.isEmpty()) {
            for (Pair<Place, Marker> place : shownPlaces) {
                place.second.setVisible(true);
            }
            return;
        }

        //Keep track of the shown place if there's only one
        Marker shownPlace = null;
        boolean onePlace = false;
        for (Pair<Place, Marker> mapPlace : shownPlaces) {
            boolean visible = mapPlace.first.getName().toLowerCase()
                    .contains(searchString.toLowerCase());
            mapPlace.second.setVisible(visible);
            if (visible) {
                //If onePlace is already set, then set it back to false
                //  since there will be more than 2
                if (onePlace) {
                    onePlace = false;
                }

                if (shownPlace == null) {
                    //If there's no shown place, set it
                    shownPlace = mapPlace.second;
                    onePlace = true;
                }
            }
        }

        //If you're showing only one place, focus on that place
        if (onePlace && map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLng(shownPlace.getPosition()));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Set the camera's center position to the McGill campus
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(45.504435, -73.576006))
                .zoom(14)
                .bearing(-54)
                .tilt(0)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //Show the user's location if we have the permission to
        if (Utils.requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_REQUEST)) {
            //noinspection MissingPermission
            map.setMyLocationEnabled(true);
        }
        //If we don't, it will be requested

        //Go through all of the places
        for (Place place : placesManager.getPlaces()) {
            //Create a MapPlace for this
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(place.getCoordinates())
                    .draggable(false)
                    .visible(true));

            //Add it to the list
            places.add(new Pair<>(place, marker));
        }

        //Filter
        filterByCategory();

        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //If there was a marker that was selected before set it back to red
        if (place != null) {
            place.second.setIcon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        //Pull up the info container
        infoContainer.setVisibility(View.VISIBLE);

        //Find the concerned place
        place = null;
        for (Pair<Place, Marker> mapPlace : places) {
            if (mapPlace.second.equals(marker)) {
                place = mapPlace;
                break;
            }
        }

        if (place == null) {
            Timber.e("Tapped place marker was not found");
            return false;
        }

        //Set it to blue
        place.second.setIcon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        //Set up the info
        title.setText(place.first.getName());
        address.setText(place.first.getAddress());

        //Set up the favorite text
        favorite.setText(placesManager.isFavorite(place.first) ?
                R.string.map_favorites_remove : R.string.map_favorites_add);

        return false;
    }
}