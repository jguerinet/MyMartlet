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

package com.guerinet.mymartlet.viewmodel

import android.content.Intent
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.guerinet.morf.TextViewItem
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.model.place.Place
import com.guerinet.mymartlet.util.room.daos.MapDao
import com.guerinet.suitcase.dialog.singleListDialog
import kotlinx.coroutines.CommonPool
import org.jetbrains.anko.toast
import timber.log.Timber

/**
 * [ViewModel] used for the map section
 * @author Julien Guerinet
 * @since 2.0.0
 */
class MapViewModel(mapDao: MapDao) : BaseViewModel() {

    /** Currently shown [Category] */
    var category: MutableLiveData<Category> = MutableLiveData<Category>()
            // Initially set to all
            .apply { postValue(Category(false)) }

    private val categories: LiveData<List<Category>> = mapDao.getCategories()

    /** Currently shown [MapPlace] */
    val place = MutableLiveData<MapPlace?>()

    val places = mapDao.getPlaces()

    val mapPlaces = MutableLiveData<List<MapPlace>>()

    private val shownMapPlaces = mutableListOf<MapPlace>()

    val query = MutableLiveData<String>()

    private val defaultMarkerIcon = BitmapDescriptorFactory.defaultMarker(
            BitmapDescriptorFactory.HUE_RED)

    /**
     * Creates the list of [MapPlace]s from the list of [places] and the [map]
     */
    fun createMapPlaces(places: List<Place>, map: GoogleMap) {
        mapPlaces.postValue(places.map {
            // Create a marker for this
            val marker = map.addMarker(MarkerOptions()
                    .position(it.coordinates)
                    .draggable(false)
                    .visible(true))

            MapPlace(it, marker)
        })
    }

    /**
     * Returns the listener for the category button.
     */
    fun onCategoryItemClicked(): (TextViewItem) -> Unit = { item ->
        val context = item.view.context
        launch(CommonPool) {
            val allCategories = categories.value
                    ?.map { Pair(it, it.getString(context)) }
                    ?.sortedWith(Comparator { o1, o2 -> o1.second.compareTo(o2.second, true) })
                    ?.toMutableList() ?: mutableListOf()

            // Add the favorites option
            var type = Category(true)
            allCategories.add(0, Pair(type, type.getString(context)))

            // Add the All option
            type = Category(false)
            allCategories.add(0, Pair(type, type.getString(context)))

            val currentChoice = allCategories.indexOfFirst { it.first == category.value }

            val choices = allCategories.map { it.second }.toTypedArray()

            launch(UI) {
                context.singleListDialog(choices, R.string.map_filter, currentChoice) {
                    val chosenCategory = allCategories[it].first
                    category.postValue(chosenCategory)
                    filterByCategory(chosenCategory)
                }
            }
        }
    }

    private fun filterByCategory(category: Category) {
        // Go through the places, show them or hide them depending on the category
        shownMapPlaces.clear()
        mapPlaces.value?.filterTo(shownMapPlaces) {
            it.place.isWithinCategory(category)
        }

        // Filter also by the search String if there is one
        filterByQuery()
    }

    /**
     * Filters the current places by the entered [query]
     */
    fun filterByQuery() {
        val query = this.query.value ?: return

        // If there is no search String, just show everything
        if (query.isEmpty()) {
            shownMapPlaces.forEach { it.marker.isVisible = true }
            return
        }

        // Keep track of the shown place if there's only one
        val shownPlaces = shownMapPlaces.filter {
            val isVisible = it.place.name.contains(query, true)
            it.marker.isVisible = isVisible
            isVisible
        }

        // If you're showing only one place, choose it
        if (shownPlaces.count() == 1) {
            place.postValue(shownPlaces.first())
        }
    }

    /**
     * Returns the listener for the directions button
     */
    fun onDirectionsClicked() = View.OnClickListener { v ->
        val place = this.place.value ?: return@OnClickListener
        val intent = Intent(Intent.ACTION_VIEW, ("http://maps.google.com/maps?f=d" +
                "&daddr=${place.place.latitude},${place.place.longitude}").toUri())
        v.context.startActivity(intent)
    }

    /**
     * Returns the listener for the favorites button
     */
    fun onFavoritesClicked() = View.OnClickListener { v ->
        val place = this.place.value ?: return@OnClickListener

        // Inverse the current favorite setting
        place.place.isFavorite = !place.place.isFavorite

        // Choose the right String depending on whether this place was in the favorites
        val message = if (place.place.isFavorite) R.string.map_favorites_added else
            R.string.map_favorites_removed

        // Alert the user
        v.context.toast(v.context.getString(message, place.place.name))
    }

    fun onMarkerClicked(marker: Marker?): Boolean {
        // If there was a marker that was selected before set it back to red
        place.value?.marker?.setIcon(defaultMarkerIcon)

        // Find the concerned place
        val newPlace = mapPlaces.value?.firstOrNull { it.marker == marker }
        place.postValue(newPlace)

        if (newPlace == null) {
            Timber.e("Tapped place marker was not found")
        }

        return false
    }

    data class MapPlace(val place: Place, val marker: Marker, var isShown: Boolean = false)
}