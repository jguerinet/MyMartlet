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

package com.guerinet.mymartlet.ui.settings

import android.os.Bundle
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.guerinet.morf.Morf
import com.guerinet.mymartlet.R
import com.guerinet.mymartlet.ui.BaseActivity
import com.guerinet.mymartlet.ui.walkthrough.WalkthroughActivity
import com.guerinet.suitcase.dialog.neutralDialog
import com.guerinet.suitcase.ui.BaseRecyclerViewAdapter
import com.guerinet.suitcase.util.extensions.openPlayStoreApp
import com.guerinet.suitcase.util.extensions.openUrl
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.item_faq.view.*
import org.jetbrains.anko.startActivity

/**
 * Displays useful information to the user
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
class HelpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        setUpToolbar(true)

        val morf = Morf.bind(container)

        // EULA
        morf.text {
            text(R.string.title_agreement)
            onClick { startActivity<AgreementActivity>() }
        }

        // Email
        morf.text {
            text(R.string.help_email_walkthrough)
            onClick {
                ga.sendEvent("Help", "McGill Email")

                // Show the user the info about the Chrome bug
                neutralDialog(message = R.string.help_email_walkthrough_info,
                        listener = MaterialDialog.SingleButtonCallback { _, _ ->
                            // Open the official McGill Guide
                            openUrl("http://kb.mcgill.ca/kb/article?ArticleId=4774")
                        })
            }
        }

        // Help
        morf.text {
            text(R.string.help_walkthrough)
            onClick { startActivity<WalkthroughActivity>() }
        }

        // McGill App
        morf.text {
            text(R.string.help_download)
            onClick { openPlayStoreApp("com.mcgill") }
        }

        // Become Beta Tester
        morf.text {
            text(R.string.help_beta_tester)
            onClick { openUrl("https://betas.to/iRinaygk") }
        }

        // FAQ
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = FAQAdapter()
    }

    /**
     * Displays the FAQs
     */
    private inner class FAQAdapter : BaseRecyclerViewAdapter() {

        private val faqs = mutableListOf(
                Pair(R.string.help_question1, R.string.help_answer1),
                Pair(R.string.help_question2, R.string.help_answer2),
                Pair(R.string.help_question3, R.string.help_answer3))

        override fun getItemCount(): Int = faqs.size

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): FAQHolder =
                FAQHolder(viewGroup)

        internal inner class FAQHolder(parent: ViewGroup) :
                BaseRecyclerViewAdapter.BaseHolder(parent, R.layout.item_faq) {

            override fun bind(position: Int) {
                val faq = faqs[position]
                val question = faq.first ?: return
                val answer = faq.second ?: return
                itemView.question.setText(question)
                itemView.answer.setText(answer)
            }
        }
    }
}

