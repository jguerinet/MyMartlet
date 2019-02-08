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

package com.guerinet.mymartlet.util.extensions

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Extensions for the FirebaseFirestore
 * @author Julien Guerinet
 * @since 2.0.0
 */

/**
 * Retrieves the collection with [collectionName] from the Firestore, parses it using the [init] function, and returns
 *  the corresponding list
 */
suspend inline fun <reified T : Any> FirebaseFirestore.get(
    collectionName: String,
    crossinline init: (DocumentSnapshot) -> T?
): List<T> = suspendCancellableCoroutine { block ->
    collection(collectionName)
        .get()
        .addOnSuccessListener { task ->
            // Get the documents from Firebase and map them to local classes
            val objects = task.documents.mapNotNull { init(it) }

            block.resume(objects)
        }
}