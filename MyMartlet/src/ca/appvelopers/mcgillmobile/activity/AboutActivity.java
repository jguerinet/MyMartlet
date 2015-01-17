package ca.appvelopers.mcgillmobile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.BaseActivity;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import ca.appvelopers.mcgillmobile.util.Help;

/**
 * Created by Adnan2
 */
public class AboutActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about);
        super.onCreate(savedInstanceState);
        GoogleAnalytics.sendScreen(this, "About");
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        //Set up the info for all of the different people

        //Adnan
        setUpInfo(findViewById(R.id.adnan), R.drawable.mymcgill_about_adnan, getResources().getString(R.string.about_adnan),
                getResources().getString(R.string.about_adnan_role), getResources().getString(R.string.about_adnan_description),
                getResources().getString(R.string.about_adnan_linkedin), getResources().getString(R.string.about_adnan_email));

        //Gabriel
        setUpInfo(findViewById(R.id.gabriel), R.drawable.mymcgill_about_gabe, getResources().getString(R.string.about_gabriel),
                getResources().getString(R.string.about_gabriel_role), getResources().getString(R.string.about_gabriel_description),
                getResources().getString(R.string.about_gabriel_linkedin), getResources().getString(R.string.about_gabriel_email));

        //Hernan
        setUpInfo(findViewById(R.id.hernan), R.drawable.mymcgill_about_hernan, getResources().getString(R.string.about_hernan),
                getResources().getString(R.string.about_hernan_role), getResources().getString(R.string.about_hernan_description),
                getResources().getString(R.string.about_hernan_linkedin), getResources().getString(R.string.about_hernan_email));

        //Josh
        setUpInfo(findViewById(R.id.josh), R.drawable.mymcgill_about_josh, getResources().getString(R.string.about_joshua),
                getResources().getString(R.string.about_joshua_role), getResources().getString(R.string.about_joshua_description),
                getResources().getString(R.string.about_joshua_linkedin), getResources().getString(R.string.about_joshua_email));

        //Julien
        setUpInfo(findViewById(R.id.julien), R.drawable.mymcgill_about_julien, getResources().getString(R.string.about_julien),
                getResources().getString(R.string.about_julien_role), getResources().getString(R.string.about_julien_description),
                getResources().getString(R.string.about_julien_linkedin), getResources().getString(R.string.about_julien_email));

        //Omar
        setUpInfo(findViewById(R.id.omar), R.drawable.mymcgill_about_omar, getResources().getString(R.string.about_omar),
                getResources().getString(R.string.about_omar_role), getResources().getString(R.string.about_omar_description),
                getResources().getString(R.string.about_omar_linkedin), getResources().getString(R.string.about_omar_email));

        //Quang
        setUpInfo(findViewById(R.id.quang), R.drawable.mymcgill_about_quang, getResources().getString(R.string.about_quang),
                getResources().getString(R.string.about_quang_role), getResources().getString(R.string.about_quang_description),
                getResources().getString(R.string.about_quang_linkedin), getResources().getString(R.string.about_quang_email));

        //Ryan
        setUpInfo(findViewById(R.id.ryan), R.drawable.mymcgill_about_ryan, getResources().getString(R.string.about_ryan),
                getResources().getString(R.string.about_ryan_role), getResources().getString(R.string.about_ryan_description),
                getResources().getString(R.string.about_ryan_linkedin), getResources().getString(R.string.about_ryan_email));

        //Shabbir
        setUpInfo(findViewById(R.id.shabbir), R.drawable.mymcgill_about_shabbir, getResources().getString(R.string.about_shabbir),
                getResources().getString(R.string.about_shabbir_role), getResources().getString(R.string.about_shabbir_description),
                getResources().getString(R.string.about_shabbir_linkedin), getResources().getString(R.string.about_shabbir_email));

        //Xavier
        setUpInfo(findViewById(R.id.xavier), R.drawable.mymcgill_about_xavier, getResources().getString(R.string.about_xavier),
                getResources().getString(R.string.about_xavier_role), getResources().getString(R.string.about_xavier_description),
                getResources().getString(R.string.about_xavier_linkedin), getResources().getString(R.string.about_xavier_email));

        //Yulric
        setUpInfo(findViewById(R.id.yulric), R.drawable.mymcgill_about_yulric, getResources().getString(R.string.about_yulric),
                getResources().getString(R.string.about_yulric_role), getResources().getString(R.string.about_yulric_description),
                getResources().getString(R.string.about_yulric_linkedin), getResources().getString(R.string.about_yulric_email));
    }

    private void setUpInfo(View view, int pictureResource, final String name, String role, String description,
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
                GoogleAnalytics.sendEvent(AboutActivity.this, "About", "Linkedin", name, null);
                Help.openURL(AboutActivity.this, linkedin);
            }
        });

        //Email
        TextView emailView = (TextView)view.findViewById(R.id.person_email);
        emailView.setTypeface(App.getIconFont());
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleAnalytics.sendEvent(AboutActivity.this, "About", "Email", name, null);

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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}