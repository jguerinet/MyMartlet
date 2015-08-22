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
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Person;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Displays information about the Appvelopers team
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class AboutActivity extends BaseActivity {
    /**
     * The list of people
     */
    private List<Person> mPeople;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Analytics.getInstance().sendScreen("About");

        setUpToolbar(true);

        mPeople = new ArrayList<>();
        //Adnan
        mPeople.add(new Person(getString(R.string.about_adnan), R.drawable.about_adnan,
                getString(R.string.about_adnan_role), getString(R.string.about_adnan_description),
                getString(R.string.about_adnan_linkedin), getString(R.string.about_adnan_email)));

        //Hernan
        mPeople.add(new Person(getString(R.string.about_hernan), R.drawable.about_hernan,
                getString(R.string.about_hernan_role), getString(R.string.about_hernan_description),
                getString(R.string.about_hernan_linkedin), getString(R.string.about_hernan_email)));

        //Josh
        mPeople.add(new Person(getString(R.string.about_joshua), R.drawable.about_josh,
                getString(R.string.about_joshua_role), getString(R.string.about_joshua_description),
                getString(R.string.about_joshua_linkedin), getString(R.string.about_joshua_email)));

        //Julia
        mPeople.add(new Person(getString(R.string.about_julia), R.drawable.about_julia,
                getString(R.string.about_julia_role), getString(R.string.about_julia_description),
                getString(R.string.about_julia_linkedin), getString(R.string.about_julia_email)));

        //Julien
        mPeople.add(new Person(getString(R.string.about_julien), R.drawable.about_julien,
                getString(R.string.about_julien_role), getString(R.string.about_julien_description),
                getString(R.string.about_julien_linkedin), getString(R.string.about_julien_email)));

        //Quang
        mPeople.add(new Person(getString(R.string.about_quang), R.drawable.about_quang,
                getString(R.string.about_quang_role), getString(R.string.about_quang_description),
                getString(R.string.about_quang_linkedin), getString(R.string.about_quang_email)));

        //Ryan
        mPeople.add(new Person(getString(R.string.about_ryan), R.drawable.about_ryan,
                getString(R.string.about_ryan_role), getString(R.string.about_ryan_description),
                getString(R.string.about_ryan_linkedin), getString(R.string.about_ryan_email)));

        //Selim
        mPeople.add(new Person(getString(R.string.about_selim), R.drawable.about_selim,
                getString(R.string.about_selim_role), getString(R.string.about_selim_description),
                getString(R.string.about_selim_linkedin), getString(R.string.about_selim_email)));

        //Shabbir
        mPeople.add(new Person(getString(R.string.about_shabbir), R.drawable.about_shabbir,
                getString(R.string.about_shabbir_role),
                getString(R.string.about_shabbir_description),
                getString(R.string.about_shabbir_linkedin),
                getString(R.string.about_shabbir_email)));

        //Xavier
        mPeople.add(new Person(getString(R.string.about_xavier), R.drawable.about_xavier,
                getString(R.string.about_xavier_role), getString(R.string.about_xavier_description),
                getString(R.string.about_xavier_linkedin), getString(R.string.about_xavier_email)));

        //Yulric
        mPeople.add(new Person(getString(R.string.about_yulric), R.drawable.about_yulric,
                getString(R.string.about_yulric_role), getString(R.string.about_yulric_description),
                getString(R.string.about_yulric_linkedin), getString(R.string.about_yulric_email)));

        //Set up the list
        RecyclerView listView = (RecyclerView)findViewById(android.R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(new PersonAdapter());
    }

    /**
     * Displays the developers in the About page
     */
    public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonHolder> {
        class PersonHolder extends RecyclerView.ViewHolder {
            /**
             * Person's name
             */
            @Bind(R.id.person_name)
            TextView mName;
            /**
             * Person's picture
             */
            @Bind(R.id.person_picture)
            ImageView mPicture;
            /**
             * Person's role
             */
            @Bind(R.id.person_role)
            TextView mRole;
            /**
             * Person's description
             */
            @Bind(R.id.person_description)
            TextView mDescription;
            /**
             * URL to person's LinkedIn
             */
            @Bind(R.id.person_linkedin)
            TextView mLinkedIn;
            /**
             * Person's email
             */
            @Bind(R.id.person_email)
            TextView mEmail;

            public PersonHolder(View itemView){
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final Person person){
                mName.setText(person.getName());
                Picasso.with(AboutActivity.this)
                        .load(person.getPictureId())
                        .into(mPicture);
                mRole.setText(person.getRole());
                mDescription.setText(person.getDescription());
                mLinkedIn.setTypeface(App.getIconFont());
                mLinkedIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Analytics.getInstance().sendEvent("About", "Linkedin", person.getName());
                        Help.openURL(AboutActivity.this, person.getLinkedIn());
                    }
                });
                mEmail.setTypeface(App.getIconFont());
                mEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Analytics.getInstance().sendEvent("About", "Email", person.getName());

                        //Send an email :
                        Intent emailIntent = new Intent(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_EMAIL, new String[]{person.getEmail()})
                                .setType("message/rfc822");
                        startActivity(Intent.createChooser(emailIntent,
                                getString(R.string.about_email_picker_title)));
                    }
                });
            }
        }

        @Override
        public PersonHolder onCreateViewHolder(ViewGroup parent, int viewType){
            return new PersonHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_person, parent, false));
        }

        @Override
        public void onBindViewHolder(PersonHolder holder, int position){
            holder.bind(mPeople.get(position));
        }

        @Override
        public int getItemCount(){
            return mPeople.size();
        }
    }
}