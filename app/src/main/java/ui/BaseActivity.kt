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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.exception.MinervaException
import com.guerinet.mymartlet.util.Constants
import com.guerinet.mymartlet.util.extensions.broadcast
import com.guerinet.mymartlet.util.extensions.errorDialog
import com.guerinet.mymartlet.util.manager.ClearManager
import com.guerinet.mymartlet.util.retrofit.McGillService
import com.guerinet.suitcase.analytics.GAManager
import com.guerinet.suitcase.coroutines.CoroutineActivity
import com.guerinet.suitcase.util.extensions.isConnected
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import timber.log.Timber

/**
 * Base class for all activities
 * @author Julien Guerinet
 * @since 1.0.0
 */
open class BaseActivity : CoroutineActivity() {

    val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    val toolbarProgress: ProgressBar by lazy { findViewById<ProgressBar>(R.id.toolbar_progress) }

    val ga by inject<GAManager>()

    val mcGillService by inject<McGillService>()

    val clearManager by inject<ClearManager>()

    /**
     * Handler for posting delayed actions
     */
    protected var handler = Handler()

    /**
     * BroadcastReceiver for any local broadcasts
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceivedBroadcast(intent)
        }
    }
    /**
     * Intent filter for the broadcast receiver
     */
    private val filter = IntentFilter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add the Minerva broadcast action
        filter.addAction(Constants.BROADCAST_MINERVA)
    }

    override fun onResume() {
        super.onResume()
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Go back if the home button is clicked
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Sets up the toolbar as the activity's action bar with the home displaying as up if
     *  [homeAsUp] is true (defaults to true)
     *  Must be declared in the activity's layout file
     */
    protected fun setUpToolbar(homeAsUp: Boolean = true) {
        // Set it as the action bar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(homeAsUp)
    }

    /**
     * Returns true if we can refresh the information on the page and
     *  shows the toolbar progress bar if so
     */
    fun canRefresh(): Boolean {
        // Check internet connection
        if (!isConnected) {
            errorDialog(R.string.error_no_internet)
            return false
        }
        return true
    }

    /**
     * Handles a network [error] while performing an [action]
     */
    fun handleError(action: String, exception: Throwable?) {
        if (exception == null) {
            return
        }
        Timber.e(exception, "Error: $action")
        toolbarProgress.isVisible = false
        if (exception is MinervaException) {
            // If this is a MinervaException, broadcast it
            broadcast(Constants.BROADCAST_MINERVA)
        } else {
            errorDialog(R.string.error_other)
        }
    }

    /**
     * Handles the reception of a broadcasted [intent]. Should be overridden in subclasses that
     *  have extra receiver actions
     */
    @CallSuper
    protected open fun onReceivedBroadcast(intent: Intent) {
        when (intent.action) {
            Constants.BROADCAST_MINERVA -> {
                // Log the user out
                clearManager.clearUserInfo()
                // Bring them back to the SplashActivity with an exception
                startActivity<SplashActivity>(Constants.EXCEPTION to MinervaException())
                finish()
            }
        }
    }
}
