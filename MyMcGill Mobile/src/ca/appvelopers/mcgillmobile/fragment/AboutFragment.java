package ca.appvelopers.mcgillmobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Author : Julien
 * Date :  2014-06-03 9:24 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_about, null);

        //Set up the info for all of the different people

        //Adnan
        setUpInfo(view.findViewById(R.id.adnan), R.drawable.adnan, getResources().getString(R.string.about_adnan),
                getResources().getString(R.string.about_adnan_role), getResources().getString(R.string.about_adnan_description),
                getResources().getString(R.string.about_adnan_linkedin), getResources().getString(R.string.about_adnan_email));

        //Gabriel
        setUpInfo(view.findViewById(R.id.gabriel), R.drawable.gabe, getResources().getString(R.string.about_gabriel),
                getResources().getString(R.string.about_gabriel_role), getResources().getString(R.string.about_gabriel_description),
                getResources().getString(R.string.about_gabriel_linkedin), getResources().getString(R.string.about_gabriel_email));

        //Hernan
        setUpInfo(view.findViewById(R.id.hernan), R.drawable.hernan, getResources().getString(R.string.about_hernan),
                getResources().getString(R.string.about_hernan_role), getResources().getString(R.string.about_hernan_description),
                getResources().getString(R.string.about_hernan_linkedin), getResources().getString(R.string.about_hernan_email));

        //Josh
        setUpInfo(view.findViewById(R.id.josh), R.drawable.josh, getResources().getString(R.string.about_joshua),
                getResources().getString(R.string.about_joshua_role), getResources().getString(R.string.about_joshua_description),
                getResources().getString(R.string.about_joshua_linkedin), getResources().getString(R.string.about_joshua_email));

        //Julien
        setUpInfo(view.findViewById(R.id.julien), R.drawable.julien, getResources().getString(R.string.about_julien),
                getResources().getString(R.string.about_julien_role), getResources().getString(R.string.about_julien_description),
                getResources().getString(R.string.about_julien_linkedin), getResources().getString(R.string.about_julien_email));

        //Omar
        setUpInfo(view.findViewById(R.id.omar), R.drawable.omar, getResources().getString(R.string.about_omar),
                getResources().getString(R.string.about_omar_role), getResources().getString(R.string.about_omar_description),
                getResources().getString(R.string.about_omar_linkedin), getResources().getString(R.string.about_omar_email));

        //Quang
        setUpInfo(view.findViewById(R.id.quang), R.drawable.quang, getResources().getString(R.string.about_quang),
                getResources().getString(R.string.about_quang_role), getResources().getString(R.string.about_quang_description),
                getResources().getString(R.string.about_quang_linkedin), getResources().getString(R.string.about_quang_email));

        //Ryan
        setUpInfo(view.findViewById(R.id.ryan), R.drawable.ryan, getResources().getString(R.string.about_ryan),
                getResources().getString(R.string.about_ryan_role), getResources().getString(R.string.about_ryan_description),
                getResources().getString(R.string.about_ryan_linkedin), getResources().getString(R.string.about_ryan_email));

        //Shabbir
        setUpInfo(view.findViewById(R.id.shabbir), R.drawable.shabbir, getResources().getString(R.string.about_shabbir),
                getResources().getString(R.string.about_shabbir_role), getResources().getString(R.string.about_shabbir_description),
                getResources().getString(R.string.about_shabbir_linkedin), getResources().getString(R.string.about_shabbir_email));

        //Xavier
        setUpInfo(view.findViewById(R.id.xavier), R.drawable.xavier, getResources().getString(R.string.about_xavier),
                getResources().getString(R.string.about_xavier_role), getResources().getString(R.string.about_xavier_description),
                getResources().getString(R.string.about_xavier_linkedin), getResources().getString(R.string.about_xavier_email));

        //Yulric
        setUpInfo(view.findViewById(R.id.yulric), R.drawable.yulric, getResources().getString(R.string.about_yulric),
                getResources().getString(R.string.about_yulric_role), getResources().getString(R.string.about_yulric_description),
                getResources().getString(R.string.about_yulric_linkedin), getResources().getString(R.string.about_yulric_email));

        return view;
    }

    private void setUpInfo(View view, int pictureResource, String name, String role, String description,
                          final String linkedin, final String email){
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
                Help.openURL(getActivity(), linkedin);
            }
        });

        //Email
        TextView emailView = (TextView)view.findViewById(R.id.person_email);
        emailView.setTypeface(App.getIconFont());
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send an email :
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                //Recipient
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                //Type (Email)
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.about_email_picker_title)));
            }
        });
    }
}