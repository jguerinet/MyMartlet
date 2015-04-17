package ca.appvelopers.mcgillmobile.fragment.map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.fragment.BaseFragment;
import ca.appvelopers.mcgillmobile.object.Place;
import ca.appvelopers.mcgillmobile.object.PlaceCategory;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Analytics;

/**
 * Author: Julien Guerinet
 * Date: 2015-01-17 5:26 PM
 * Copyright (c) 2014 Appvelopers. All rights reserved.
 */

public class MapFragment extends BaseFragment {
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
    private SupportMapFragment mFragment;
    private List<MapPlace> mCurrentMapPlaces;
    private String mSearchString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fragment has a menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = View.inflate(mActivity, R.layout.fragment_map, null);

        //Title
        mActivity.setTitle(getString(R.string.title_map));

        Analytics.getInstance().sendScreen("Map");

        //Bind the TextViews
        mInfoContainer = (LinearLayout)view.findViewById(R.id.info_container);
        mTitle = (TextView)view.findViewById(R.id.place_title);
        mAddress = (TextView)view.findViewById(R.id.place_address);
        mFavorite = (TextView)view.findViewById(R.id.map_favorite);

        //Check if the places already exist
        if(mPlaces == null){
            mPlaces = new ArrayList<MapPlace>();
            mCurrentMapPlaces = new ArrayList<MapPlace>();
        }
        mFavoritePlaces = App.getFavoritePlaces();
        mSearchString = "";

        //Set up the spinner
        final Spinner filter = (Spinner) view.findViewById(R.id.map_filter);
        final MapCategoriesAdapter adapter = new MapCategoriesAdapter(mActivity);
        filter.setAdapter(adapter);
        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Get the selected category
                mCategory = adapter.getItem(position);

                //Filter the places
                filterByCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Set the all category as the first one
        mCategory = adapter.getItem(0);

        //Set up the two buttons
        TextView directions = (TextView) view.findViewById(R.id.map_directions);
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


        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlaceMarker != null) {
                    //Check if it was in the favorites
                    if (mFavoritePlaces.contains(mPlaceMarker.mPlace)) {
                        mFavoritePlaces.remove(mPlaceMarker.mPlace);
                        Toast.makeText(mActivity, getString(R.string.map_favorites_removed, mPlaceMarker.mPlace.getName()), Toast.LENGTH_SHORT).show();
                        mFavorite.setText(getString(R.string.map_favorites_add));

                        //If we are in the favorites category, we need to hide this pin
                        if(mCategory.getName().equals(PlaceCategory.FAVORITES)){
                            mPlaceMarker.mMarker.setVisible(false);
                        }
                    } else {
                        mFavoritePlaces.add(mPlaceMarker.mPlace);
                        Toast.makeText(mActivity, getString(R.string.map_favorites_added, mPlaceMarker.mPlace.getName()), Toast.LENGTH_SHORT).show();
                        mFavorite.setText(getString(R.string.map_favorites_remove));
                    }
                    App.setFavoritePlaces(mFavoritePlaces);
                }
            }
        });

        FragmentManager fragmentManager = getChildFragmentManager();
        //Get the MapFragment
        mFragment = (SupportMapFragment)fragmentManager.findFragmentById(R.id.map);
        //If it's null, initialize it and put it in its view
        if(mFragment == null){
            mFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction()
                    .replace(R.id.map, mFragment)
                    .addToBackStack(null)
                    .commit();
        }

        hideLoadingIndicator();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        //If the map is null, bind it and add the markers
        if(mMap == null){
            mMap = mFragment.getMap();
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

            //Filter
            filterByCategory();

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //If there was a marker that was selected before set it back to red
                    if (mPlaceMarker != null) {
                        mPlaceMarker.mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }
                    //Pull up the info container
                    mInfoContainer.setVisibility(View.VISIBLE);

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
        }
    }

    public void filterByCategory(){
        //Reset the current places
        mCurrentMapPlaces.clear();

        String categoryName = mCategory.getName();

        //Go through the places
        for(MapPlace place : mPlaces){
            //If it's all, add everything
            if(categoryName.equals(PlaceCategory.ALL)){
                mCurrentMapPlaces.add(place);
                place.mMarker.setVisible(true);
            }
            //If it's favorites, check if it's part of favorites
            else if(categoryName.equals(PlaceCategory.FAVORITES)){
                boolean partOfFavorites = mFavoritePlaces.contains(place.mPlace);
                place.mMarker.setVisible(partOfFavorites);
                if(partOfFavorites){
                    mCurrentMapPlaces.add(place);
                }
            }
            //If not, show the ones pertaining to the current category
            else{
                boolean partOfCategory = place.mPlace.hasCategory(mCategory);
                place.mMarker.setVisible(partOfCategory);
                if(partOfCategory){
                    mCurrentMapPlaces.add(place);
                }
            }
        }

        //Filter also by the search String if there is one
        filterBySearchString();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView =
                new SearchView(mActivity.getSupportActionBar().getThemedContext());
        final int textViewID = searchView.getContext().getResources().
                getIdentifier("android:id/search_src_text",null, null);
        final AutoCompleteTextView searchTextView =
                (AutoCompleteTextView) searchView.findViewById(textViewID);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            //Set the cursor to the same color as the text
            mCursorDrawableRes.set(searchTextView, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mSearchString = "";
                filterBySearchString();
                return false;
            }
        });
    }

    public void filterBySearchString() {
        //If there is no search String, just show everything
        if(mSearchString.isEmpty()){
            for(MapPlace mapPlace : mCurrentMapPlaces){
                mapPlace.mMarker.setVisible(true);
            }
            return;
        }

        //Keep track of the number of places you're showing
        int numberOfPlaces = 0;
        MapPlace place = null;
        for (MapPlace mapPlace : mCurrentMapPlaces) {
            if (mapPlace.mPlace.getName().toLowerCase().contains(mSearchString.toLowerCase())) {
                mapPlace.mMarker.setVisible(true);
                numberOfPlaces ++;
                place = mapPlace;
            } else {
                mapPlace.mMarker.setVisible(false);
            }
        }

        //If you're showing only one place, focus on that place
        if(numberOfPlaces == 1){
            focusPlace(place);
        }
    }

    public void focusPlace(MapPlace place) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(
                new LatLng(place.mPlace.getLatitude(), place.mPlace.getLongitude())));
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