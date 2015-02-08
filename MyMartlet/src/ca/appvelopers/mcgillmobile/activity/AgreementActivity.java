package ca.appvelopers.mcgillmobile.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.BaseActivity;
import ca.appvelopers.mcgillmobile.util.Constants;

/**
 * Author: Julien Guerinet
 * Date: 2015-02-08 11:47
 * Copyright (c) 2015 Sigvaria Mobile Technologies Inc. All rights reserved.
 * Contains the EULA Agreement that the user has to first accept before using the app
 */
public class AgreementActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Check if we need to display the buttons
        if(getIntent().getBooleanExtra(Constants.EULA_REQUIRED, false)){
            LinearLayout layout = (LinearLayout)findViewById(R.id.buttons_container);
            layout.setVisibility(View.VISIBLE);

            //Accept
            Button agree = (Button)findViewById(R.id.button_agree);
            agree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK);
                    finish();
                }
            });

            Button decline = (Button)findViewById(R.id.button_decline);
            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
        }
    }
}