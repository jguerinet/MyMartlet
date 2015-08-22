/*
 * Copyright 2014-2015 Appvelopers
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.FAQItem;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Displays useful information to the user
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class HelpActivity extends BaseActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
        setUpToolbar(true);
        
        //FAQ ListView
        RecyclerView faq = (RecyclerView) findViewById(R.id.help_faq);
        faq.setLayoutManager(new LinearLayoutManager(this));
        faq.setAdapter(new FAQAdapter());
    }

    @OnClick(R.id.help_eula)
    public void seeEULA(){
        startActivity(new Intent(this, AgreementActivity.class));
    }

    @OnClick(R.id.help_email)
    public void seeEmailWalkthrough(){
        Intent intent = new Intent(this, WalkthroughActivity.class)
                .putExtra(Constants.EMAIL, true);
        startActivity(intent);
    }

    @OnClick(R.id.help_walkthrough)
    public void seeWalkthrough(){
        startActivity(new Intent(this, WalkthroughActivity.class));
    }

    @OnClick(R.id.help_download)
    public void downloadOfficialApp(){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mcgill")));
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
        public FAQAdapter(){
            mFAQs = new ArrayList<>();
            mFAQs.add(new FAQItem(getString(R.string.help_question1),
                    getString(R.string.help_answer1)));
            mFAQs.add(new FAQItem(getString(R.string.help_question2),
                    getString(R.string.help_answer2)));
            mFAQs.add(new FAQItem(getString(R.string.help_question3),
                    getString(R.string.help_answer3)));
        }

        @Override
        public int getItemCount(){
            return mFAQs.size();
        }

        @Override
        public FAQHolder onCreateViewHolder(ViewGroup viewGroup, int i){
            return new FAQHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_faq, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(FAQHolder faqHolder, int i){
            faqHolder.bind(mFAQs.get(i));
        }

        class FAQHolder extends RecyclerView.ViewHolder {
            /**
             * The FAQ question
             */
            @Bind(R.id.faq_question)
            TextView mQuestion;
            /**
             * The FAQ answer
             */
            @Bind(R.id.faq_answer)
            TextView mAnswer;

            public FAQHolder(View itemView){
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemView.setClickable(false);
            }

            public void bind(FAQItem item){
                mQuestion.setText(item.getQuestion());
                mAnswer.setText(item.getAnswer());
            }
        }
    }
}

