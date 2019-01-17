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
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.google.android.gms.maps.model.MarkerOptions
import com.guerinet.morf.Morf
import com.guerinet.morf.util.Position
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.model.place.Place
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.util.room.daos.CategoryDao
import com.guerinet.mymartlet.util.room.daos.PlaceDao
import com.guerinet.suitcase.dialog.singleListDialog
import com.guerinet.suitcase.ui.extensions.setDrawableTint
import com.guerinet.suitcase.util.Utils
import com.guerinet.suitcase.util.extensions.getColorCompat
import com.guerinet.suitcase.util.extensions.hasPermission
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.koin.android.ext.android.inject
import timber.log.Timber

/**
 * Displays a campus map
 * @author Ryan Singzon
 * @author Julien Guerinet
 * @author Quang Dao
 * @since 1.0.0
 */
class MapActivity : DrawerActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private val placeDao by inject<PlaceDao>()

    private val categoryDao by inject<CategoryDao>()

    private var map: GoogleMap? = null

    /**
     * Total list of places with their associated markers
     */
    private val places: MutableList<Pair<Place, Marker>> = mutableListOf()

    /**
     * Currently shown map places with their associated markers
     */
    private val shownPlaces: MutableList<Pair<Place, Marker>> = mutableListOf()

    /**
     * Currently shown place with its associated marker
     */
    private var place: Pair<Place, Marker>? = null

    /**
     * Currently selected category
     */
    private var category: Category = Category(false)

    /**
     * Current search String
     */
    private var searchString: String = ""

    override val currentPage = HomepageManager.HomePage.MAP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        ga.sendScreen("Map")

        val morf = Morf.bind(container)

        // Icon coloring
        val red = getColorCompat(R.color.red)
        directions.setDrawableTint(0, red)
        favorite.setDrawableTint(0, red)

        //Set up the place filter
        morf.text {
            text(category.getString(this@MapActivity))
            icon(Position.START, R.drawable.ic_location)
            icon(Position.END, R.drawable.ic_chevron_right, true, Color.GRAY)
            onClick { textViewItem ->
                doAsync {
                    val categories =
                        categoryDao.getCategories().map { Pair(it, it.getString(this@MapActivity)) }

                    uiThread {
                        singleListDialog(categories.map { it.second }.toTypedArray(),
                            R.string.map_filter,
                            categories.indexOfFirst { it.first == category }) {

                            category = categories[it].first

                            // Update the text
                            textViewItem.text(category.getString(this@MapActivity))

                            // Update the filtered places
                            filterByCategory()
                        }
                    }
                }
            }
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
        directions.setOnClickListener { getDirections() }
        favorite.setOnClickListener { favorites() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)

        // Get the SearchView
        val item = menu.findItem(R.id.action_search)
        val searchView = SearchView(this)
        val textViewID = searchView.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
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
                searchString = query
                filterBySearchString()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchString = newText
                filterBySearchString()
                return false
            }
        })

        // Reset the search view
        searchView.setOnCloseListener {
            searchString = ""
            filterBySearchString()
            false
        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
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

    /**
     * Opens Google Maps with directions to the chosen place
     */
    private fun getDirections() {
        // Open Google Maps
        val place = this.place ?: return
        val intent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?f=d &daddr="
                    + place.second.position.latitude + "," + place.second.position.longitude
            )
        )
        startActivity(intent)
    }

    /**
     * Adds or remove a place from the user's favorites
     */
    private fun favorites() {
        val place = this.place ?: return

        // Choose the right Strings depending on whether this place was in the favorites
        val stringIds = if (place.first.isFavorite) {
            Pair(R.string.map_favorites_removed, R.string.map_favorites_add)
        } else {
            Pair(R.string.map_favorites_added, R.string.map_favorites_remove)
        }

        // Inverse the current favorite setting
        place.first.isFavorite = !place.first.isFavorite

        // Change the button text
        favorite.setText(stringIds.second)

        if (category.id == Category.FAVORITES) {
            // If we are in the favorites category, we need to show/hide this pin
            showPlace(place, place.first.isFavorite)
        }

        // Alert the user
        toast(getString(stringIds.first, place.first.name))
    }

    /**
     * Makes the [place] [visible] or not
     */
    private fun showPlace(place: Pair<Place, Marker>, visible: Boolean) {
        place.second.isVisible = visible
        if (visible) {
            shownPlaces.add(place)
        }
    }

    /**
     * Filters the current places by the selected category
     */
    private fun filterByCategory() {
        // Reset the current places
        shownPlaces.clear()

        // Go through the places
        places.forEach { showPlace(it, it.first.isWithinCategory(category)) }

        // Filter also by the search String if there is one
        filterBySearchString()
    }

    /**
     * Filters the current places by the entered search String
     */
    private fun filterBySearchString() {
        // If there is no search String, just show everything
        if (searchString.isEmpty()) {
            shownPlaces.forEach { it.second.isVisible = true }
            return
        }

        // Keep track of the shown place if there's only one
        val shownPlaces = shownPlaces.filter {
            val visible = it.first.name.toLowerCase().contains(searchString.toLowerCase())
            it.second.isVisible = visible
            visible
        }

        // If you're showing only one place, focus on that place
        if (shownPlaces.count() == 1) {
            map?.animateCamera(CameraUpdateFactory.newLatLng(shownPlaces.first().second.position))
        }
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
        map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST)) {
            // Show the user's location if we have the permission to
            @SuppressLint("MissingPermission")
            map?.isMyLocationEnabled = true
        }

        map?.setOnMarkerClickListener(this)

        doAsync {
            val places = placeDao.getPlaces()
            val placeId = intent.getIntExtra(Constants.ID, -1)
            var theMarker: Marker? = null
            places.mapNotNullTo(this@MapActivity.places) {
                // Create a marker for this
                val marker = map?.addMarker(
                    MarkerOptions()
                        .position(it.coordinates)
                        .draggable(false)
                        .visible(true)
                ) ?: return@mapNotNullTo null

                // Check if there was a place with the intent
                if (theMarker == null && it.id == placeId) {
                    // If the right place is found, perform a click later
                    theMarker = marker
                }

                Pair(it, marker)
            }

            // Filter
            filterByCategory()

            onMarkerClick(theMarker)
        }
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // If there was a marker that was selected before set it back to red
        place?.second?.setIcon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED
            )
        )

        // Pull up the info container
        infoContainer.isVisible = true

        // Find the concerned place
        place = places.firstOrNull { it.second == marker }

        if (place == null) {
            Timber.e("Tapped place marker was not found")
            return false
        }

        //Set it to blue
        place?.second?.setIcon(
            BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE
            )
        )

        // Set up the info
        placeTitle.text = place?.first?.name
        address.text = place?.first?.address

        // Set up the favorite text
        favorite.setText(
            if (place?.first?.isFavorite == true)
                R.string.map_favorites_remove else R.string.map_favorites_add
        )

        return false
    }

    companion object {
        private const val LOCATION_REQUEST = 101
    }
}