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

package com.guerinet.mymartlet.ui.settings.about

import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.suitcase.util.extensions.openUrl

/**
 * Displays information about the team
 * @author Julien Guerinet
 * @author Rafi Uddin
 * @since 1.0.0
 */
class AboutActivity : BaseActivity() {

    private val list by getView<RecyclerView>(android.R.id.list)
    private val github by getView<Button>(R.id.github)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setUpToolbar(true)

        // Set up the list
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = PersonAdapter()

        github.setOnClickListener { openUrl("https://github.com/jguerinet/MyMartlet/") }
    }
}
