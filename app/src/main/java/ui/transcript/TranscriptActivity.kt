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

package com.guerinet.mymartlet.ui.transcript

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.DrawerActivity
import com.guerinet.mymartlet.util.extensions.observe
import com.guerinet.mymartlet.util.manager.HomepageManager
import com.guerinet.mymartlet.viewmodel.TranscriptViewModel
import kotlinx.android.synthetic.main.activity_transcript.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Shows the user's transcript
 * @author Julien Guerinet
 * @since 1.0.0
 */
class TranscriptActivity : DrawerActivity() {

    override val currentPage = HomepageManager.HomePage.TRANSCRIPT

    private val adapter by lazy { TranscriptAdapter() }

    private val transcriptViewModel by viewModel<TranscriptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transcript)
        ga.sendScreen("Transcript")

        list.apply {
            layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(this@TranscriptActivity)
            adapter = this@TranscriptActivity.adapter
        }

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
            val isVisible = it ?: return@observe
            toolbarProgress.isVisible = isVisible
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

    /**
     * Refreshes the transcript
     */
    private fun refresh() {
        if (!canRefresh()) {
            return
        }

        launch {
            val e = transcriptViewModel.refresh()
            handleError("Transcript Refresh", e)
        }
    }
}