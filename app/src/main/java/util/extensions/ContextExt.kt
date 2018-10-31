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

package com.guerinet.mymartlet.util.extensions

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.guerinet.mymartlet.R
import com.guerinet.suitcase.dialog.neutralDialog
import org.jetbrains.anko.toast

/**
 * Extensions for the Context
 * @author Julien Guerinet
 * @since 2.0.0
 */

/**
 * Displays a toast with a generic error message
 */
fun Context.errorToast() = toast(R.string.error_other)

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