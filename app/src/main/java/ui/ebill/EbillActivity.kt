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

package com.guerinet.mymartlet.ui.ebill

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.util.extensions.observe
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.viewmodel.EbillViewModel
import kotlinx.android.synthetic.main.activity_ebill.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Displays the user's ebill statements
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
class EbillActivity : DrawerActivity() {

    override val currentPage = HomepageManager.HomePage.EBILL

    private val ebillViewModel by viewModel<EbillViewModel>()

    private val adapter by lazy { EbillAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ebill)
        ga.sendScreen("Ebill")

        list.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@EbillActivity)
            adapter = this@EbillActivity.adapter
        }

        observe(ebillViewModel.statements) {
            adapter.update(it)
        }

        observe(ebillViewModel.isToolbarProgressVisible) {
            val isVisible = it ?: return@observe
            toolbarProgress.isVisible = isVisible
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.refresh, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Refreshes the list of statements
     */
    private fun refresh() {
        if (!canRefresh()) {
            return
        }

        launch {
            val e = ebillViewModel.refresh()
            handleError("Ebill Refresh", e)
        }
    }
}