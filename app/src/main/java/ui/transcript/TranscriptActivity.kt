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

package com.guerinet.mymartlet.ui.transcript

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.viewmodel.TranscriptViewModel
import com.guerinet.suitcase.coroutines.uiDispatcher
import com.guerinet.suitcase.lifecycle.observe
import com.guerinet.suitcase.log.TimberTag
import kotlinx.android.synthetic.main.activity_transcript.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Shows the user's transcript
 * @author Julien Guerinet
 * @since 1.0.0
 */
class TranscriptActivity : DrawerActivity(), TimberTag {

    override val tag: String = "TranscriptActivity"

    override val currentPage = HomepageManager.HomePage.TRANSCRIPT

    private val transcriptViewModel by viewModel<TranscriptViewModel>()

    private val adapter by lazy { TranscriptAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transcript)
        ga.sendScreen("Transcript")

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        observe(transcriptViewModel.transcript) {
            if (it != null) {
                cgpa.text = getString(R.string.transcript_CGPA, it.cgpa.toString())
                credits.text = getString(R.string.transcript_credits, it.totalCredits.toString())
            }
        }

        observe(transcriptViewModel.semesters) {
            adapter.update(it)
        }

        observe(transcriptViewModel.isToolbarProgressVisible) {
            if (it != null) {
                toolbarProgress.isVisible = it
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
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

    private fun refresh() {
        if (!canRefresh()) {
            return
        }

        launch(uiDispatcher) {
            val e = transcriptViewModel.refresh()
            handleError("Transcript Refresh", e)
        }
    }
}