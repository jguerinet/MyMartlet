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

package com.guerinet.mymartlet.ui.settings

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.suitcase.settings.BooleanSetting
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

/**
 * Displays the EULA
 * @author Julien Guerinet
 * @author Joshua David Alfaro
 * @since 1.0.0
 */
class AgreementActivity : BaseActivity() {

    private val eulaPref by inject<BooleanSetting>(named(Prefs.EULA))

    private val buttonsContainer by getView<View>(R.id.buttonsContainer)
    private val agree by getView<Button>(R.id.agree)
    private val decline by getView<Button>(R.id.decline)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agreement)

        val isRequired = intent.getBooleanExtra(Prefs.EULA, false)
        setUpToolbar(!isRequired)

        if (isRequired) {
            // Display the buttons if the user is required to agree to it
            buttonsContainer.visibility = View.VISIBLE

            agree.setOnClickListener {
                eulaPref.value = true
                setResult(Activity.RESULT_OK)
                finish()
            }

            decline.setOnClickListener {
                eulaPref.value = false
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}
