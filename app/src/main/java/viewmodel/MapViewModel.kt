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

package com.guerinet.mymartlet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.model.place.Place
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.room.daos.PlaceDao

/**
 * ViewModel used for the Map section
 * @author Julien Guerinet
 * @since 2.0.0
 */
class MapViewModel(app: Application, placeDao: PlaceDao) : AndroidViewModel(app) {

    /** User inputted search term, null/empty String if none */
    val searchTerm = MutableLiveData<String>()

    /** Currently chosen category we're filtering by */
    val category = MutableLiveData<Category>().apply {
        // First category is always ALL
        postValue(Category(app))
    }

    /** List of all categories */
    val categories = MutableLiveData<List<Category>>()

    /** Currently shown place */
    val place = MutableLiveData<Place>()

    /** List of all places */
    val places = placeDao.getLivePlaces()

    /** List of places that fit the current category */
    val categoryPlaces = MediatorLiveData<List<Place>>().apply {
        // Add the category as a source
        addSource(category) {
            val category = it ?: return@addSource
            val places = places.value ?: listOf()

            // Whenever the category changes, update the list of places that fit that requirement
            val categoryPlaces = when (category.id) {
                Category.ALL -> places
                else -> places.filter { place -> place.categories.contains(category.id) }
            }

            postValue(categoryPlaces)
        }
    }

    /** List of places currently shown on the map */
    val shownPlaces = MediatorLiveData<List<Place>>().apply {
        // Keep track of the list of category places. Whenever that changes, we need to update the shown places
        addSource(categoryPlaces) {
            updateShownPlaces()
        }

        // Also keep track of the search term
        addSource(searchTerm) {
            updateShownPlaces()
        }
    }

    init {
        FirebaseFirestore.getInstance().collection(Constants.Firebase.CATEGORIES)
            .get()
            .addOnSuccessListener { task ->
                // Get the categories from Firebase
                val firebaseCategories = task.documents.mapNotNull { it.toObject(Category::class.java) }.toMutableList()

                // Add All category
                firebaseCategories.add(0, Category(app))

                categories.postValue(firebaseCategories)
            }
    }

    fun getPlace(placeId: Int) {
        val place = places.value?.firstOrNull { it.id == placeId }
        if (place != null) {
            this.place.postValue(place)
        }
    }

    /**
     * Updates the shown places by taking into account the latest chosen category and typed search String
     */
    private fun updateShownPlaces() {
        // Get the current list of places for the category, and the search term
        val places = categoryPlaces.value ?: listOf()
        val searchString = searchTerm.value ?: ""

        // If there is no search String, show everything for this category
        if (searchString.isBlank()) {
            shownPlaces.postValue(places)
            return
        }

        // Show the places whose name contain the search String
        shownPlaces.postValue(places.filter { it.name.equals(searchString, true) })
    }
}