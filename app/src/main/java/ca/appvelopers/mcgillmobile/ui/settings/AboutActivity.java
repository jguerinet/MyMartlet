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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.ui.base.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Displays information about the Appvelopers team
 * @author Rafi Uddin
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class AboutActivity extends BaseActivity {
    /**
     * The container for all of the about sections
     */
    private LinearLayout mContainer;

    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        Analytics.getInstance().sendScreen("About");

        setUpToolbar(true);
        
        //Set up the info for all of the different people
        mContainer = (LinearLayout)findViewById(R.id.about_container);

        //Adnan
        setUpInfo(R.drawable.about_adnan, getString(R.string.about_adnan),
                getString(R.string.about_adnan_role), getString(R.string.about_adnan_description),
                getString(R.string.about_adnan_linkedin), getString(R.string.about_adnan_email));

        //Hernan
        setUpInfo(R.drawable.about_hernan, getString(R.string.about_hernan),
                getString(R.string.about_hernan_role), getString(R.string.about_hernan_description),
                getString(R.string.about_hernan_linkedin), getString(R.string.about_hernan_email));

        //Josh
        setUpInfo(R.drawable.about_josh, getString(R.string.about_joshua),
                getString(R.string.about_joshua_role), getString(R.string.about_joshua_description),
                getString(R.string.about_joshua_linkedin), getString(R.string.about_joshua_email));

        //Julia
        setUpInfo(R.drawable.about_julia, getString(R.string.about_julia),
                getString(R.string.about_julia_role), getString(R.string.about_julia_description),
                getString(R.string.about_julia_linkedin), getString(R.string.about_julia_email));

        //Julien
        setUpInfo(R.drawable.about_julien, getString(R.string.about_julien),
                getString(R.string.about_julien_role), getString(R.string.about_julien_description),
                getString(R.string.about_julien_linkedin), getString(R.string.about_julien_email));

        //Quang
        setUpInfo(R.drawable.about_quang, getString(R.string.about_quang),
                getString(R.string.about_quang_role), getString(R.string.about_quang_description),
                getString(R.string.about_quang_linkedin), getString(R.string.about_quang_email));

        //Ryan
        setUpInfo(R.drawable.about_ryan, getString(R.string.about_ryan),
                getString(R.string.about_ryan_role), getString(R.string.about_ryan_description),
                getString(R.string.about_ryan_linkedin), getString(R.string.about_ryan_email));

        //Selim
        setUpInfo(R.drawable.about_selim, getString(R.string.about_selim),
                getString(R.string.about_selim_role), getString(R.string.about_selim_description),
                getString(R.string.about_selim_linkedin), getString(R.string.about_selim_email));


        //Shabbir
        setUpInfo(R.drawable.about_shabbir, getString(R.string.about_shabbir),
                getString(R.string.about_shabbir_role),
                getString(R.string.about_shabbir_description),
                getString(R.string.about_shabbir_linkedin),
                getString(R.string.about_shabbir_email));

        //Xavier
        setUpInfo(R.drawable.about_xavier, getString(R.string.about_xavier),
                getString(R.string.about_xavier_role), getString(R.string.about_xavier_description),
                getString(R.string.about_xavier_linkedin), getString(R.string.about_xavier_email));

        //Yulric
        setUpInfo(R.drawable.about_yulric, getString(R.string.about_yulric),
                getString(R.string.about_yulric_role), getString(R.string.about_yulric_description),
                getString(R.string.about_yulric_linkedin), getString(R.string.about_yulric_email));
    }

    /**
     * Sets up the information for a given person and adds the view to the cntainer
     *
     * @param pictureResource The resource Id of the person's picture
     * @param name            The person's name
     * @param role            The person's role
     * @param description     The person's personal description
     * @param linkedin        The person's LinkedIn URL
     * @param email           The person's email address
     */
    private void setUpInfo(int pictureResource, final String name, String role, String description,
                           final String linkedin, final String email){
        View view = View.inflate(this, R.layout.item_person, null);

        //Picture
        ImageView picture = (ImageView)view.findViewById(R.id.person_image);
        picture.setImageResource(pictureResource);

        //Name
        TextView nameView = (TextView)view.findViewById(R.id.person_name);
        nameView.setText(name);

        //Role
        TextView roleView = (TextView)view.findViewById(R.id.person_role);
        roleView.setText(role);

        //Description
        TextView descriptionView = (TextView)view.findViewById(R.id.person_description);
        descriptionView.setText(description);

        //Linkedin
        TextView linkedinView = (TextView)view.findViewById(R.id.person_linkedin);
        linkedinView.setTypeface(App.getIconFont());
        linkedinView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics.getInstance().sendEvent("About", "Linkedin", name);
                Help.openURL(AboutActivity.this, linkedin);
            }
        });

        //Email
        TextView emailView = (TextView)view.findViewById(R.id.person_email);
        emailView.setTypeface(App.getIconFont());
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Analytics.getInstance().sendEvent("About", "Email", name);

                //Send an email :
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                //Recipient
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                //Type (Email)
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent,
                        getString(R.string.about_email_picker_title)));
            }
        });

        //Add it to the container
        mContainer.addView(view);
    }
}