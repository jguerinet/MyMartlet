/*
 * Copyright 2014-2017 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.utils.RecyclerViewBaseAdapter;
import com.guerinet.utils.Utils;
import com.guerinet.utils.dialog.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;

/**
 * Displays useful information to the user
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class HelpActivity extends BaseActivity {
    /**
     * FormGenerator container
     */
    @BindView(R.id.container)
    LinearLayout container;
    /**
     * FAQ List
     */
    @BindView(android.R.id.list)
    RecyclerView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        setUpToolbar(true);

        FormGenerator fg = FormGenerator.bind(this, container);

        // EULA
        fg.text(R.string.title_agreement)
                .onClick(item -> startActivity(new Intent(this, AgreementActivity.class)))
                .build();

        // Email
        fg.text(R.string.help_email_walkthrough)
                .onClick(item -> {
                    analytics.sendEvent("Help", "McGill Email");

                    // Show the user the info about the Chrome bug
                    DialogUtils.neutral(this, -1, R.string.help_email_walkthrough_info,
                            (dialog, which) -> {
                                // Open the official McGill Guide
                                Utils.openURL(this,
                                        "http://kb.mcgill.ca/kb/article?ArticleId=4774");

                            });
                })
                .build();

        // Help
        fg.text(R.string.help_walkthrough)
                .onClick(item -> startActivity(new Intent(this, WalkthroughActivity.class)))
                .build();

        // McGill App
        fg.text(R.string.help_download)
                .onClick(item -> Utils.openPlayStoreApp(this, "com.mcgill"))
                .build();

        // Become Beta Tester
        fg.text(R.string.help_beta_tester)
                .onClick(item -> Utils.openURL(this, "https://betas.to/iRinaygk"))
                .build();
        
        // FAQ
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new FAQAdapter());
    }

    /**
     * Adapter used to display the FAQs
     */
    class FAQAdapter extends RecyclerViewBaseAdapter {
        /**
         * List of FAQs
         */
        private final List<Pair<Integer, Integer>> faqs;

        /**
         * Default Constructor
         */
        private FAQAdapter() {
            super(null);
            faqs = new ArrayList<>();
            faqs.add(new Pair<>(R.string.help_question1, R.string.help_answer1));
            faqs.add(new Pair<>(R.string.help_question2, R.string.help_answer2));
            faqs.add(new Pair<>(R.string.help_question3, R.string.help_answer3));
        }

        @Override
        public int getItemCount() {
            return faqs.size();
        }

        @Override
        public FAQHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new FAQHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_faq, viewGroup, false));
        }

        class FAQHolder extends BaseHolder {
            /**
             * FAQ question
             */
            @BindView(R.id.question)
            TextView question;
            /**
             * FAQ answer
             */
            @BindView(R.id.answer)
            TextView answer;

            FAQHolder(View itemView) {
                super(itemView);
            }

            @Override
            public void bind(int position) {
                Pair<Integer, Integer> faq = faqs.get(position);
                question.setText(faq.first);
                answer.setText(faq.second);
            }
        }
    }
}

