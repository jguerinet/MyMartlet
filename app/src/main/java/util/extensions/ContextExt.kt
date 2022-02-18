/*
 * Copyright 2014-2022 Julien Guerinet
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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.guerinet.mymartlet.R
import com.guerinet.suitcase.dialog.neutralDialog
import com.guerinet.suitcase.util.extensions.toast
import splitties.activities.start
import java.io.Serializable

/**
 * Extensions for the Context
 * @author Julien Guerinet
 * @since 2.0.0
 */

/**
 * Displays a toast with a generic error message
 */
fun Context.errorToast() = toast(getString(R.string.error_other))

/**
 * Shows an error [AlertDialog] with one button and the [messageId]
 */
fun Context.errorDialog(@StringRes messageId: Int) = neutralDialog(R.string.error, messageId)

/**
 * Shows an error [AlertDialog] with one button and the [message]
 */
fun Context.errorDialog(message: String) = neutralDialog(R.string.error, message)

/**
 * Broadcasts an [intent] using the [LocalBroadcastManager]
 */
fun Context.broadcast(intent: Intent) =
    androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(
        intent
    )

/**
 * Broadcasts an [action] using the [LocalBroadcastManager]
 */
fun Context.broadcast(action: String) = broadcast(Intent(action))

/**
 * Starts an activity [T] with some intent [params]
 */
inline fun <reified T : Activity> Context.start(vararg params: Pair<String, Any?>) = start<T> {
    if (params.isNotEmpty()) {
        fillIntentArguments(params)
    }
}

/**
 * Takes a list of [params] and adds them to the [Intent]
 */
fun Intent.fillIntentArguments(params: Array<out Pair<String, Any?>>) {
    params.forEach {
        when (val value = it.second) {
            null -> putExtra(it.first, null as Serializable?)
            is Int -> putExtra(it.first, value)
            is Long -> putExtra(it.first, value)
            is CharSequence -> putExtra(it.first, value)
            is String -> putExtra(it.first, value)
            is Float -> putExtra(it.first, value)
            is Double -> putExtra(it.first, value)
            is Char -> putExtra(it.first, value)
            is Short -> putExtra(it.first, value)
            is Boolean -> putExtra(it.first, value)
            is Serializable -> putExtra(it.first, value)
            is Bundle -> putExtra(it.first, value)
            is Parcelable -> putExtra(it.first, value)
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> putExtra(it.first, value)
                value.isArrayOf<String>() -> putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> putExtra(it.first, value)
                else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is IntArray -> putExtra(it.first, value)
            is LongArray -> putExtra(it.first, value)
            is FloatArray -> putExtra(it.first, value)
            is DoubleArray -> putExtra(it.first, value)
            is CharArray -> putExtra(it.first, value)
            is ShortArray -> putExtra(it.first, value)
            is BooleanArray -> putExtra(it.first, value)
            else -> throw Exception("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
        return@forEach
    }
}
