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
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.guerinet.mymartlet.model.place.Category
import com.guerinet.mymartlet.util.Constants

/**
 * ViewModel used for the Map section
 * @author Julien Guerinet
 * @since 2.0.0
 */
class MapViewModel(app: Application) : AndroidViewModel(app) {

    val categories = MutableLiveData<List<Category>>()

    init {
        FirebaseFirestore.getInstance().collection(Constants.Firebase.CATEGORIES)
            .get()
            .addOnSuccessListener { task ->
                // Get the categories from Firebase
                val firebaseCategories = task.documents.mapNotNull { it.toObject(Category::class.java) }.toMutableList()

                // Add All and Favorites
                firebaseCategories.add(0, Category(true, app))
                firebaseCategories.add(0, Category(false, app))

                categories.postValue(firebaseCategories)
            }
    }
}