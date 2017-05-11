/*
 * Copyright 2014-2017 Julien Guerinet
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

package com.guerinet.mymartlet.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.R;
import com.guerinet.mymartlet.model.place.Category;
import com.guerinet.mymartlet.model.place.Place;
import com.guerinet.mymartlet.ui.dialog.list.CategoryListAdapter;
import com.guerinet.mymartlet.util.Constants;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import junit.framework.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    LinearLayout infoContainer;
    /**
     * {@link FormGenerator} container for the filter
     */
    @BindView(R.id.container)
    LinearLayout container;
    /**
     * Current place's title
     */
    @BindView(R.id.place_title)
    TextView title;
    /**
     * Current place's address
     */
    @BindView(R.id.place_address)
    TextView address;
    /**
     * Button to get directions to a place
     */
    @BindView(R.id.directions)
    Button directions;
    /**
     * Button to add or remove a place from the user's favorites
     */
    @BindView(R.id.map_favorite)
    Button favorite;
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
    private Category category;
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
        category = new Category(false);

        FormGenerator fg = FormGenerator.bind(container);

        // Icon coloring
        int red = ContextCompat.getColor(this, R.color.red);
        Utils.setTint(directions, 0, red);
        Utils.setTint(favorite, 0, red);

        //Set up the place filter
        fg.text()
                .text(category.getString(this))
                .leftIcon(R.drawable.ic_location)
                .rightIcon(R.drawable.ic_chevron_right, Color.GRAY)
                .onClick(new TextViewFormItem.OnClickListener() {
                    @Override
                    public void onClick(final TextViewFormItem item) {
                        DialogUtils.list(MapActivity.this, R.string.map_filter,
                                new CategoryListAdapter(MapActivity.this, category) {
                                    @Override
                                    public void onCategorySelected(Category type) {
                                        MapActivity.this.category = type;

                                        //Update the text
                                        item.view().setText(type.getString(MapActivity.this));

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

        // Get the SearchView
        MenuItem item = menu.findItem(R.id.action_search);
        Assert.assertNotNull(getSupportActionBar());
        final SearchView searchView = new SearchView(this);
        final int textViewID = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        final AutoCompleteTextView searchTextView =
                (AutoCompleteTextView) searchView.findViewById(textViewID);
        try {
            // Set the cursor to the same color as the text
            Field cursorDrawable = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorDrawable.setAccessible(true);
            cursorDrawable.set(searchTextView, 0);
        } catch (Exception e) {
            Timber.e(e, "Cannot change color of cursor");
        }

        // Set up the query listener
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
    protected @HomepageManager.Homepage int getCurrentPage() {
        return HomepageManager.MAP;
    }

    /**
     * Opens Google Maps with directions to the chosen place
     */
    @OnClick(R.id.directions)
    void directions() {
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
    void favorites() {
        if (place != null) {
            int toastMessageId;
            int buttonTextId;

            // Choose the right Strings depending on whether this place was in the favorites
            if (place.first.isFavorite()) {
                toastMessageId = R.string.map_favorites_removed;
                buttonTextId = R.string.map_favorites_add;
            } else {
                toastMessageId = R.string.map_favorites_added;
                buttonTextId = R.string.map_favorites_remove;
            }

            // Inverse the current favorite setting
            place.first.setFavorite(!place.first.isFavorite());

            // Change the button text
            favorite.setText(buttonTextId);

            // If we are in the favorites category, we need to show/hide this pin
            if (category.getId() == Category.FAVORITES) {
                showPlace(place, place.first.isFavorite());
            }

            // Alert the user
            Utils.toast(this, getString(toastMessageId, place.first.getName()));
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
            // Show it if it's part of the given category
            showPlace(place, place.first.isWithinCategory(category));
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

        map.setOnMarkerClickListener(this);

        SQLite.select()
                .from(Place.class)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    int placeId = getIntent().getIntExtra(Constants.ID, -1);
                    Marker theMarker = null;
                    for (Place place : tResult) {
                        // Create a MapPlace for this
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(place.getCoordinates())
                                .draggable(false)
                                .visible(true));

                        // Check if there was a place with the intent
                        if (theMarker == null && place.getId() == placeId) {
                            // If the right place is found, perform a click later
                            theMarker = marker;
                        }

                        // Add it to the list
                        places.add(new Pair<>(place, marker));
                    }

                    // Filter
                    filterByCategory();

                    onMarkerClick(theMarker);
                })
                .execute();
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
        favorite.setText(place.first.isFavorite() ? R.string.map_favorites_remove :
                R.string.map_favorites_add);

        return false;
    }
}