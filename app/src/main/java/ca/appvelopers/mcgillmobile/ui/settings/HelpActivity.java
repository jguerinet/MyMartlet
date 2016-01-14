/*
 * Copyright 2014-2016 Appvelopers
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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guerinet.formgenerator.FormGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.FAQItem;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Displays useful information to the user
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class HelpActivity extends BaseActivity {
    /**
     * {@link FormGenerator} container
     */
    @Bind(R.id.container)
    protected LinearLayout mContainer;
    /**
     * FAQ List
     */
    @Bind(android.R.id.list)
    protected RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        setUpToolbar(true);

        FormGenerator fg = FormGenerator.bind(this, mContainer);

        //EULA
        fg.text(R.string.title_agreement)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(HelpActivity.this, AgreementActivity.class));
                    }
                });

        //Email
        fg.text(R.string.help_email_walkthrough)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HelpActivity.this, WalkthroughActivity.class)
                                .putExtra(Constants.EMAIL, true);
                        startActivity(intent);
                    }
                });

        //Help
        fg.text(R.string.help_walkthrough)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(HelpActivity.this, WalkthroughActivity.class));
                    }
                });

        //McGill App
        fg.text(R.string.help_download)
                .onClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=com.mcgill")));
                    }
                });
        
        //FAQ
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(new FAQAdapter());
    }

    /**
     * Adapter used to display the FAQs
     */
    class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQHolder> {
        /**
         * The list of FAQs
         */
        private List<FAQItem> mFAQs;

        /**
         * Default Constructor
         */
        public FAQAdapter() {
            mFAQs = new ArrayList<>();
            mFAQs.add(new FAQItem(R.string.help_question1, R.string.help_answer1));
            mFAQs.add(new FAQItem(R.string.help_question2, R.string.help_answer2));
            mFAQs.add(new FAQItem(R.string.help_question3, R.string.help_answer3));
        }

        @Override
        public int getItemCount() {
            return mFAQs.size();
        }

        @Override
        public FAQHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new FAQHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_faq, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(FAQHolder faqHolder, int i) {
            faqHolder.bind(mFAQs.get(i));
        }

        class FAQHolder extends RecyclerView.ViewHolder {
            /**
             * FAQ question
             */
            @Bind(R.id.question)
            protected TextView mQuestion;
            /**
             * FAQ answer
             */
            @Bind(R.id.answer)
            protected TextView mAnswer;

            public FAQHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(FAQItem item) {
                mQuestion.setText(item.getQuestion());
                mAnswer.setText(item.getAnswer());
            }
        }
    }
}

