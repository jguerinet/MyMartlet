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
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.model.Statement
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.util.dbflow.DBUtils
import com.guerinet.mymartlet.util.dbflow.databases.StatementDB
import com.guerinet.mymartlet.util.manager.HomepageManager
import kotlinx.android.synthetic.main.activity_ebill.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Displays the user's ebill statements
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
class EbillActivity : DrawerActivity() {

    override val currentPage = HomepageManager.HomePage.EBILL

    private val adapter = EbillAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ebill)
        ga.sendScreen("Ebill")

        list.apply {
            layoutManager = LinearLayoutManager(this@EbillActivity)
            adapter = adapter
        }
        adapter.update()
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

        mcGillService.ebill().enqueue(object : Callback<List<Statement>> {
            override fun onResponse(call: Call<List<Statement>>,
                    response: Response<List<Statement>>) {
                DBUtils.replaceDB(this@EbillActivity, StatementDB.NAME, Statement::class.java,
                        response.body()) {
                    toolbarProgress.isVisible = false
                    adapter.update()
                }
            }

            override fun onFailure(call: Call<List<Statement>>, t: Throwable) {
                handleError("refreshing the ebill", t)
            }
        })
    }
}