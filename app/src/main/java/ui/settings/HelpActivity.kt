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

import android.os.Bundle
import android.widget.LinearLayout
import com.guerinet.morf.morf
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.ui.walkthrough.WalkthroughActivity
import com.guerinet.mymartlet.util.extensions.getView
import com.guerinet.mymartlet.util.extensions.start
import com.guerinet.suitcase.dialog.neutralDialog
import com.guerinet.suitcase.util.extensions.openPlayStoreApp
import com.guerinet.suitcase.util.extensions.openUrl

/**
 * Displays useful information to the user
 * @author Julien Guerinet
 * @author Rafi Uddin
 * @since 1.0.0
 */
class HelpActivity : BaseActivity() {

    private val container by getView<LinearLayout>(R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        setUpToolbar(true)

        container.morf {

            // EULA
            text {
                textId = R.string.title_agreement
                onClick { start<AgreementActivity>() }
            }

            // Email
            text {
                textId = R.string.help_email_walkthrough
                onClick {
                    analytics.event("help_mcgill_email")

                    // Show the user the info about the Chrome bug
                    neutralDialog(message = R.string.help_email_walkthrough_info) {
                        // Open the official McGill Guide
                        openUrl("http://kb.mcgill.ca/kb/article?ArticleId=4774")
                    }
                }
            }

            // Help
            text {
                textId = R.string.help_walkthrough
                onClick { start<WalkthroughActivity>() }
            }

            // McGill App
            text {
                textId = R.string.help_download
                onClick { openPlayStoreApp("com.mcgill") }
            }

            // Become Beta Tester
            text {
                textId = R.string.help_beta_tester
                onClick { openUrl("https://play.google.com/apps/testing/com.guerinet.mymartlet") }
            }
        }
    }
}
