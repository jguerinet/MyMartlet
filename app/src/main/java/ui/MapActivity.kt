/*
 * Copyright 2014-2019 Julien Guerinet
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
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.transaction
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.guerinet.morf.TextViewItem
import com.guerinet.morf.morf
import com.guerinet.morf.util.Position
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.viewmodel.MapViewModel
import com.guerinet.suitcase.dialog.singleListDialog
import com.guerinet.suitcase.lifecycle.observe
import com.guerinet.suitcase.ui.extensions.setDrawableTint
import com.guerinet.suitcase.util.Utils
import com.guerinet.suitcase.util.extensions.getColorCompat
import com.guerinet.suitcase.util.extensions.hasPermission
import kotlinx.android.synthetic.main.activity_map.*
import org.koin.androidx.viewmodel.ext.android.viewModel
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

    /** List of markers on the map with their associated place Ids */
    private val markers = mutableListOf<Pair<Int, Marker>>()

    private lateinit var categoryItem: TextViewItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Icon coloring
        val red = getColorCompat(R.color.red)
        directions.setDrawableTint(0, red)
        favorite.setDrawableTint(0, red)

        // Set up the category filter
        container.morf {
            categoryItem = text {
                icon(Position.START, R.drawable.ic_location)
                icon(Position.END, R.drawable.ic_chevron_right, true, Color.GRAY)
                onClick {
                    // Get the categories from the ViewModel and transform them into
                    val categories = mapViewModel.categories.value ?: listOf()

                    singleListDialog(categories.map { it.name }.toTypedArray(),
                        R.string.map_filter,
                        categories.indexOfFirst { it == mapViewModel.category.value }) {

                        mapViewModel.category.postValue(categories[it])
                    }
                }
            }
        }

        observe(mapViewModel.category) {
            val category = it ?: return@observe

            // Update the text
            categoryItem.text = category.name
        }

        observe(mapViewModel.shownPlaces) {
            val shownPlaces = it ?: return@observe

            markers.forEach { marker ->
                // Show the marker if the shown places contains a place with the Id of this marker
                marker.second.isVisible = shownPlaces.any { place -> place.id == marker.first }
            }

            if (shownPlaces.size == 1) {
                // Get the corresponding marker
                val marker = markers.first { marker -> marker.first == shownPlaces.first().id }
                // Zoom in on that place
                map?.animateCamera(CameraUpdateFactory.newLatLng(marker.second.position))
            }
        }

        observe(mapViewModel.place) {
            val place = it ?: return@observe

            // Set all markers to red
            markers.forEach { marker ->
                marker.second.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }

            // Find the marker for this place
            val marker = markers.firstOrNull { marker -> marker.first == place.id }

            if (marker == null) {
                Timber.e("Tapped place marker with id ${place.id} not found")
                return@observe
            }

            // Set the icon to blue
            marker.second.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))

            // Set up the info
            placeTitle.text = place.name
            address.text = place.address

            // Set up the favorite text
            favorite.setText(if (place.isFavorite) R.string.map_favorites_remove else R.string.map_favorites_add)
        }

        // Get the MapFragment (Create it if null)
        val fragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            ?: createAndGetMapFragment()

        // Request the map
        fragment.getMapAsync(this)

        // OnClickListeners
        directions.setOnClickListener { getDirections() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        // Get the SearchView
        val item = menu.findItem(R.id.action_search)
        val searchView = SearchView(this)
        val textViewID = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchTextView = searchView.findViewById<AutoCompleteTextView>(textViewID)
        try {
            // Set the cursor to the same color as the text
            val cursorDrawable = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            cursorDrawable.isAccessible = true
            cursorDrawable.set(searchTextView, 0)
        } catch (e: Exception) {
            Timber.e(e, "Cannot change color of cursor")
        }

        // Set up the query listener
        item.actionView = searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mapViewModel.searchTerm.postValue(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mapViewModel.searchTerm.postValue(newText)
                return false
            }
        })

        // Reset the search view
        searchView.setOnCloseListener {
            mapViewModel.searchTerm.postValue("")
            false
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            // Check if the permission has been granted
            LOCATION_REQUEST -> if (Utils.isPermissionGranted(grantResults)) {
                // Show the user on the map if that is the case
                @SuppressLint("MissingPermission")
                map?.isMyLocationEnabled = true
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Creates the [SupportMapFragment], adds it to the layout, and returns it
     */
    private fun createAndGetMapFragment(): SupportMapFragment {
        val fragment = SupportMapFragment.newInstance()
        supportFragmentManager.transaction {
            replace(R.id.map, fragment)
            addToBackStack(null)
        }
        return fragment
    }

    private fun getDirections() {
        // Open Google Maps
        val place = mapViewModel.place.value ?: return
        val intent = Intent(
            Intent.ACTION_VIEW,
            "http://maps.google.com/maps?f=d &daddr=${place.latitude},${place.longitude}".toUri()
        )
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Set the camera's center position to the McGill campus
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(45.504435, -73.576006))
            .zoom(14f)
            .bearing(-54f)
            .tilt(0f)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST)) {
            // Show the user's location if we have the permission to
            @SuppressLint("MissingPermission")
            googleMap.isMyLocationEnabled = true
        }

        googleMap.setOnMarkerClickListener(this)

        // Create a marker for each place
        val places = mapViewModel.places.value ?: return

        places.mapTo(markers) {
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(it.coordinates)
                    .draggable(false)
                    .visible(true)
            )

            Pair(it.id, marker)
        }

        // Re-post the shown places to show them on the now loaded map
        mapViewModel.shownPlaces.postValue(mapViewModel.shownPlaces.value)

        // Click on the place sent with the intent, if there is one
        mapViewModel.getPlace(intent.getIntExtra(Constants.ID, -1))
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // Find the corresponding place Id
        val placeId = markers.firstOrNull { it.second == marker }?.first ?: -1

        mapViewModel.getPlace(placeId)

        return false
    }

    companion object {
        private const val LOCATION_REQUEST = 101
    }
}
