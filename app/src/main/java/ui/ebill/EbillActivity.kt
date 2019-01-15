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

package com.guerinet.mymartlet.ui.ebill

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.viewmodel.EbillViewModel
import com.guerinet.suitcase.coroutines.uiDispatcher
import com.guerinet.suitcase.lifecycle.observe
import com.guerinet.suitcase.log.TimberTag
import kotlinx.android.synthetic.main.activity_ebill.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Displays the user's ebill statements
 * @author Julien Guerinet
 * @author Rafi Uddin
 * @since 1.0.0
 */
class EbillActivity : DrawerActivity(), TimberTag {

    override val tag: String = "EbillActivity"

    override val currentPage = HomepageManager.HomePage.EBILL

    private val ebillViewModel by viewModel<EbillViewModel>()

    private val adapter by lazy { EbillAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ebill)
        ga.sendScreen("Ebill")

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        observe(ebillViewModel.statements) {
            adapter.update(it)
        }

        observe(ebillViewModel.isToolbarProgressVisible) {
            if (it != null) {
                toolbarProgress.isVisible = it
            }
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

        launch(uiDispatcher) {
            val e = ebillViewModel.refresh()
            handleError("Ebill Refresh", e)
        }
    }
}