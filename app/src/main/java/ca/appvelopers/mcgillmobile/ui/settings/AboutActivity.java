/*
 * Copyright 2014-2016 Julien Guerinet
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guerinet.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.model.Person;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;

/**
 * Displays information about the Appvelopers team
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class AboutActivity extends BaseActivity {
    /**
     * The list view
     */
    @Bind(android.R.id.list)
    protected RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setUpToolbar(true);
        analytics.sendScreen("About");

        //Set up the list
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new PersonAdapter());
    }

    @OnClick(R.id.github)
    protected void gitHub() {
        Utils.openURL(this, "https://github.com/jguerinet/MyMartlet/");
    }

    /**
     * Displays the developers in the About page
     */
    public class PersonAdapter extends RecyclerView.Adapter {
        /**
         * Person view type
         */
        private static final int PERSON = 0;
        /**
         * The list of items
         */
        private List<Object> items;

        /**
         * Default Constructors
         */
        public PersonAdapter() {
            items = new ArrayList<>();

            //Current Contributors
            items.add(R.string.contributors_current);

            //Julien
            items.add(new Person(R.string.about_julien, R.drawable.about_julien,
                    R.string.about_julien_role, R.string.about_julien_description,
                    R.string.about_julien_email, R.string.about_julien_linkedin));

            //Shabbir
            items.add(new Person(R.string.about_shabbir, R.drawable.about_shabbir,
                    R.string.about_shabbir_role, R.string.about_shabbir_description,
                    R.string.about_shabbir_email, R.string.about_shabbir_linkedin));

            //Past Contributors
            items.add(R.string.contributors_past);

            //Adnan
            items.add(new Person(R.string.about_adnan, R.drawable.about_adnan,
                    R.string.about_adnan_role, R.string.about_adnan_description,
                    R.string.about_adnan_email, R.string.about_adnan_linkedin));

            //Hernan
            items.add(new Person(R.string.about_hernan, R.drawable.about_hernan,
                    R.string.about_hernan_role, R.string.about_hernan_description,
                    R.string.about_hernan_email, R.string.about_hernan_linkedin));

            //Josh
            items.add(new Person(R.string.about_joshua, R.drawable.about_josh,
                    R.string.about_joshua_role, R.string.about_joshua_description,
                    R.string.about_joshua_email, R.string.about_joshua_linkedin));

            //Julia
            items.add(new Person(R.string.about_julia, R.drawable.about_julia,
                    R.string.about_julia_role, R.string.about_julia_description,
                    R.string.about_julia_email, R.string.about_julia_linkedin));

            //Quang
            items.add(new Person(R.string.about_quang, R.drawable.about_quang,
                    R.string.about_quang_role, R.string.about_quang_description,
                    R.string.about_quang_email, R.string.about_quang_linkedin));

            //Ryan
            items.add(new Person(R.string.about_ryan, R.drawable.about_ryan,
                    R.string.about_ryan_role, R.string.about_ryan_description,
                    R.string.about_ryan_email, R.string.about_ryan_linkedin));

            //Selim
            items.add(new Person(R.string.about_selim, R.drawable.about_selim,
                    R.string.about_selim_role, R.string.about_selim_description,
                    R.string.about_selim_email, R.string.about_selim_linkedin));

            //Xavier
            items.add(new Person(R.string.about_xavier, R.drawable.about_xavier,
                    R.string.about_xavier_role, R.string.about_xavier_description,
                    R.string.about_xavier_email, R.string.about_xavier_linkedin));

            //Yulric
            items.add(new Person(R.string.about_yulric, R.drawable.about_yulric,
                    R.string.about_yulric_role, R.string.about_yulric_description,
                    R.string.about_yulric_email, R.string.about_yulric_linkedin));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == PERSON) {
                return new PersonHolder(inflater.inflate(R.layout.item_person, parent, false));
            }
            int padding = getResources().getDimensionPixelOffset(R.dimen.padding_small);
            TextView textView = new TextView(parent.getContext());
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.text_large));
            textView.setPadding(padding, padding, padding, padding);
            return new HeaderHolder(textView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == PERSON) {
                ((PersonHolder) holder).bind((Person) items.get(position));
            } else {
                ((HeaderHolder) holder).bind((Integer) items.get(position));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return (items.get(position) instanceof Person) ? PERSON : -1;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         * Header in the list
         */
        class HeaderHolder extends RecyclerView.ViewHolder {
            public HeaderHolder(TextView itemView) {
                super(itemView);
            }

            public void bind(@StringRes int title) {
                ((TextView) itemView).setText(title);
            }
        }

        /**
         * Person item in the list
         */
        class PersonHolder extends RecyclerView.ViewHolder {
            /**
             * Person's name
             */
            @Bind(R.id.name)
            protected TextView name;
            /**
             * Person's picture
             */
            @Bind(R.id.picture)
            protected ImageView picture;
            /**
             * Person's role
             */
            @Bind(R.id.role)
            protected TextView role;
            /**
             * Person's description
             */
            @Bind(R.id.description)
            protected TextView description;
            /**
             * URL to person's LinkedIn
             */
            @Bind(R.id.linkedin)
            protected ImageView linkedIn;
            /**
             * Person's email
             */
            @Bind(R.id.email)
            protected ImageView email;

            public PersonHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(final Person person) {
                name.setText(person.getName());

                Picasso.with(AboutActivity.this)
                        .load(person.getPictureId())
                        .into(picture);

                role.setText(person.getRole());
                description.setText(person.getDescription());
                linkedIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        analytics.sendEvent("About", "Linkedin", getString(person.getName()));
                        Utils.openURL(AboutActivity.this, getString(person.getLinkedIn()));
                    }
                });
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        analytics.sendEvent("About", "Email", getString(person.getName()));

                        //Send an email :
                        Intent emailIntent = new Intent(Intent.ACTION_SEND)
                                .putExtra(Intent.EXTRA_EMAIL,
                                        new String[]{getString(person.getEmail())})
                                .setType("message/rfc822");
                        startActivity(Intent.createChooser(emailIntent,
                                getString(R.string.about_email_picker_title)));
                    }
                });
            }
        }
    }
}