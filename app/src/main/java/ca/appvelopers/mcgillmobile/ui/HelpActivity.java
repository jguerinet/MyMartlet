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

package ca.appvelopers.mcgillmobile.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.HelpItem;
import ca.appvelopers.mcgillmobile.ui.main.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughActivity;
import ca.appvelopers.mcgillmobile.util.Constants;

public class HelpActivity extends BaseActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //EULA buttons
        TextView eula = (TextView)findViewById(R.id.help_eula);
        eula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelpActivity.this, AgreementActivity.class));
            }
        });

        //Set up the email walkthrough and walkthrough buttons
        TextView emailWalkthrough = (TextView)findViewById(R.id.help_email);
        emailWalkthrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, WalkthroughActivity.class);
                intent.putExtra(Constants.EMAIL, true);
                startActivity(intent);
            }
        });

        TextView walkthrough = (TextView)findViewById(R.id.help_walkthrough);
        walkthrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivity.this, WalkthroughActivity.class);
                startActivity(intent);
            }
        });

        //Official McGill App download button
        TextView download = (TextView)findViewById(R.id.help_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mcgill")));
            }
        });
        
        //FAQ ListView
        ListView helpListView = (ListView) findViewById(R.id.helpListView);
        HelpAdapter adapter = new HelpAdapter(this);
        helpListView.setAdapter(adapter);
    }

    public class HelpAdapter extends BaseAdapter {
        private List<HelpItem> mHelpList;
        private Context mContext;

        public HelpAdapter (Context context){
            this.mContext = context;
            populateList();
        }
        @Override
        public int getCount() {
            return mHelpList.size();
        }

        @Override
        public HelpItem getItem(int i) {
            return mHelpList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_faq,null);
            }

            assert (view != null);

            HelpItem helpItem = getItem(i);

            TextView question = (TextView) view.findViewById(R.id.about_question);
            question.setText(helpItem.getQuestion());

            TextView answer = (TextView) view.findViewById(R.id.about_answer);
            answer.setText(helpItem.getAnswer());

            return view;
        }

        private void populateList(){
            mHelpList = new ArrayList<HelpItem>();
            mHelpList.add(new HelpItem(getResources().getString(R.string.help_question1),getResources().getString(R.string.help_answer1)));
            mHelpList.add(new HelpItem(getResources().getString(R.string.help_question2), getResources().getString(R.string.help_answer2)));
            mHelpList.add(new HelpItem(getResources().getString(R.string.help_question3), getResources().getString(R.string.help_answer3)));
        }
    }
}

