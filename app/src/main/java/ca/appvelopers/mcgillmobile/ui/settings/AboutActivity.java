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

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guerinet.utils.RecyclerViewBaseAdapter;
import com.guerinet.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;

/**
 * Displays information about the team
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class AboutActivity extends BaseActivity {
    /**
     * Main view
     */
    @BindView(android.R.id.list)
    RecyclerView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setUpToolbar(true);
        analytics.sendScreen("About");

        // Set up the list
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new PersonAdapter());
    }

    @OnClick(R.id.github)
    void gitHub() {
        Utils.openURL(this, "https://github.com/jguerinet/MyMartlet/");
    }

    /**
     * Displays the developers in the About page
     */
    class PersonAdapter extends RecyclerViewBaseAdapter {
        /**
         * Person view type
         */
        private static final int PERSON = 0;
        /**
         * List of items
         */
        private final List<Object> items;

        /**
         * Default Constructors
         */
        private PersonAdapter() {
            super(null);
            items = new ArrayList<>();

            // Current Contributors
            items.add(R.string.contributors_current);

            // Julien
            items.add(new Person(R.string.about_julien, R.drawable.about_julien,
                    R.string.about_julien_role, R.string.about_julien_description,
                    R.string.about_julien_email, R.string.about_julien_linkedin));

            // Past Contributors
            items.add(R.string.contributors_past);

            // Adnan
            items.add(new Person(R.string.about_adnan, R.drawable.about_adnan,
                    R.string.about_adnan_role, R.string.about_adnan_description,
                    R.string.about_adnan_email, R.string.about_adnan_linkedin));

            // Hernan
            items.add(new Person(R.string.about_hernan, R.drawable.about_hernan,
                    R.string.about_hernan_role, R.string.about_hernan_description,
                    R.string.about_hernan_email, R.string.about_hernan_linkedin));

            // Josh
            items.add(new Person(R.string.about_joshua, R.drawable.about_josh,
                    R.string.about_joshua_role, R.string.about_joshua_description,
                    R.string.about_joshua_email, R.string.about_joshua_linkedin));

            // Julia
            items.add(new Person(R.string.about_julia, R.drawable.about_julia,
                    R.string.about_julia_role, R.string.about_julia_description,
                    R.string.about_julia_email, R.string.about_julia_linkedin));

            // Quang
            items.add(new Person(R.string.about_quang, R.drawable.about_quang,
                    R.string.about_quang_role, R.string.about_quang_description,
                    R.string.about_quang_email, R.string.about_quang_linkedin));

            // Ryan
            items.add(new Person(R.string.about_ryan, R.drawable.about_ryan,
                    R.string.about_ryan_role, R.string.about_ryan_description,
                    R.string.about_ryan_email, R.string.about_ryan_linkedin));

            // Selim
            items.add(new Person(R.string.about_selim, R.drawable.about_selim,
                    R.string.about_selim_role, R.string.about_selim_description,
                    R.string.about_selim_email, R.string.about_selim_linkedin));

            // Shabbir
            items.add(new Person(R.string.about_shabbir, R.drawable.about_shabbir,
                    R.string.about_shabbir_role, R.string.about_shabbir_description,
                    R.string.about_shabbir_email, R.string.about_shabbir_linkedin));

            // Xavier
            items.add(new Person(R.string.about_xavier, R.drawable.about_xavier,
                    R.string.about_xavier_role, R.string.about_xavier_description,
                    R.string.about_xavier_email, R.string.about_xavier_linkedin));

            // Yulric
            items.add(new Person(R.string.about_yulric, R.drawable.about_yulric,
                    R.string.about_yulric_role, R.string.about_yulric_description,
                    R.string.about_yulric_email, R.string.about_yulric_linkedin));
        }

        @Override
        public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        class HeaderHolder extends BaseHolder {

            HeaderHolder(TextView itemView) {
                super(itemView);
            }

            public void bind(int position) {
                ((TextView) itemView).setText((int) items.get(position));
            }
        }

        /**
         * Person item in the list
         */
        class PersonHolder extends BaseHolder {
            /**
             * Person's name
             */
            @BindView(R.id.name)
            TextView name;
            /**
             * Person's picture
             */
            @BindView(R.id.picture)
            ImageView picture;
            /**
             * Person's role
             */
            @BindView(R.id.role)
            TextView role;
            /**
             * Person's description
             */
            @BindView(R.id.description)
            TextView description;
            /**
             * URL to person's LinkedIn
             */
            @BindView(R.id.linkedin)
            ImageView linkedIn;
            /**
             * Person's email
             */
            @BindView(R.id.email)
            ImageView email;

            PersonHolder(View itemView) {
                super(itemView);
            }

            public void bind(int position) {
                Person person = (Person) items.get(position);
                Context context = itemView.getContext();

                name.setText(person.name);

                Picasso.with(context)
                        .load(person.pictureId)
                        .into(picture);

                role.setText(person.role);
                description.setText(person.description);
                linkedIn.setOnClickListener(view -> {
                    analytics.sendEvent("About", "Linkedin", getString(person.name));
                    Utils.openURL(context, getString(person.linkedIn));
                });
                email.setOnClickListener(view -> {
                    analytics.sendEvent("About", "Email", getString(person.name));

                    // Send an email
                    Intent intent = new Intent(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_EMAIL, new String[] {getString(person.email)})
                            .setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, null));
                });
            }
        }

        /**
         * One person for the About page
         * @author Julien Guerinet
         * @since 2.0.0
         */
        class Person {
            /**
             * Person's name
             */
            @StringRes
            private final int name;
            /**
             * Person's picture
             */
            @DrawableRes
            private final int pictureId;
            /**
             * Person's role
             */
            @StringRes
            private final int role;
            /**
             * A short description about the person
             */
            @StringRes
            private final int description;
            /**
             * Person's email
             */
            @StringRes
            private final int email;
            /**
             * URL to the person's LinkedIn
             */
            @StringRes
            private final int linkedIn;

            /**
             * Default Constructor
             *
             * @param name        Person's name
             * @param pictureId   Person's picture
             * @param role        Person's role
             * @param description A short description about the person
             * @param email       Person's email
             * @param linkedIn    URL to the person's LinkedIn
             */
            private Person(@StringRes int name, @DrawableRes int pictureId, @StringRes int role,
                    @StringRes int description, @StringRes int email, @StringRes int linkedIn) {
                this.name = name;
                this.pictureId = pictureId;
                this.role = role;
                this.description = description;
                this.email = email;
                this.linkedIn = linkedIn;
            }
        }
    }
}