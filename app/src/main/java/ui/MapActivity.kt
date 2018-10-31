/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.ui

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.guerinet.morf.Morf
import com.guerinet.morf.util.Position
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.extensions.observe
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.viewmodel.MapViewModel
import com.guerinet.suitcase.ui.extensions.setDrawableTint
import com.guerinet.suitcase.util.Utils
import com.guerinet.suitcase.util.extensions.getColorCompat
import com.guerinet.suitcase.util.extensions.hasPermission
import kotlinx.android.synthetic.main.activity_map.*
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber

/**
 * Displays a campus map
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 */
class MapActivity : DrawerActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override val currentPage = HomepageManager.HomePage.MAP

    private val mapViewModel by viewModel<MapViewModel>()

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        ga.sendScreen("Map")

        val morf = Morf.bind(container)

        // Icon coloring
        getColorCompat(R.color.red).apply {
            directions.setDrawableTint(0, this)
            favorite.setDrawableTint(0, this)
        }

        // Set up the place filter
        val categoryItem = morf.text {
            icon(Position.START, R.drawable.ic_location)
            icon(Position.END, R.drawable.ic_chevron_right, true, Color.GRAY)
            onClick(mapViewModel.onCategoryItemClicked())
        }

        val manager = supportFragmentManager

        // Get the MapFragment
        var fragment: SupportMapFragment? =
                manager.findFragmentById(R.id.map) as? SupportMapFragment

        // If it's null, initialize it and put it in its view
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance()
            manager.beginTransaction()
                    .replace(R.id.map, fragment)
                    .addToBackStack(null)
                    .commit()
        }

        fragment?.getMapAsync(this)

        // OnClickListeners
        directions.setOnClickListener(mapViewModel.onDirectionsClicked())
        favorite.setOnClickListener(mapViewModel.onFavoritesClicked())

        observe(mapViewModel.query) {
            // TODO There must be a better way to observe within the ViewModel
            mapViewModel.filterByQuery()
        }

        observe(mapViewModel.category) {
            val category = it ?: return@observe
            categoryItem.text(category.getString(this))
        }

        observe(mapViewModel.place) {
            val place = it ?: return@observe

            // Change the favorites button text
            val text = if (place.place.isFavorite) R.string.map_favorites_remove else
                R.string.map_favorites_add
            favorite.setText(text)

            // Set the icon color to blue
            place.marker.setIcon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE))

            // Set up the info
            placeTitle.text = place.place.name
            address.text = place.place.address

            // Zoom to this place
            map?.animateCamera(CameraUpdateFactory.newLatLng(place.marker.position))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        // Get the SearchView
        val item = menu.findItem(R.id.action_search)
        SearchView(this).apply {
            val textViewId = resources.getIdentifier("android:id/search_src_text", null, null)
            val searchTextView = findViewById<AutoCompleteTextView>(textViewId)
            try {
                // Set the cursor to the same color as the text
                TextView::class.java.getDeclaredField("mCursorDrawableRes").apply {
                    isAccessible = true
                    set(searchTextView, 0)
                }
            } catch (e: Exception) {
                Timber.e(e, "Cannot change color of cursor")
            }

            // Set up the query listener
            item.actionView = this
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean = false

                override fun onQueryTextChange(newText: String): Boolean {
                    mapViewModel.query.postValue(newText)
                    return false
                }
            })

            // Reset the search view
            setOnCloseListener {
                mapViewModel.query.postValue("")
                false
            }
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
            grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST ->
                // Check if the permission has been granted
                if (Utils.isPermissionGranted(grantResults)) {
                    // Show the user on the map if that is the case
                    @SuppressLint("MissingPermission")
                    map?.isMyLocationEnabled = true
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map

        // Set the camera's center position to the McGill campus
        val cameraPosition = CameraPosition.Builder()
                .target(LatLng(45.504435, -73.576006))
                .zoom(14f)
                .bearing(-54f)
                .tilt(0f)
                .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST)) {
            // Show the user's location if we have the permission to
            @SuppressLint("MissingPermission")
            map.isMyLocationEnabled = true
        }

        map.setOnMarkerClickListener(this)

        observe(mapViewModel.places) { it ->
            val places = it ?: return@observe
            mapViewModel.createMapPlaces(places, map)

            val placeId = intent.getIntExtra(Constants.ID, -1)
            mapViewModel.mapPlaces.value?.find { it.place.id == placeId }?.apply {
                // If we find a place with the Id of the place in the intent, click on it
                onMarkerClick(marker)
            }
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // Pull up the info container
        infoContainer.isVisible = true

        return mapViewModel.onMarkerClicked(marker)
    }

    companion object {
        private const val LOCATION_REQUEST = 101
    }
}