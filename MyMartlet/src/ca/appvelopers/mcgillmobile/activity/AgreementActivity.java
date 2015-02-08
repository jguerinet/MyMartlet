package ca.appvelopers.mcgillmobile.activity;

import android.os.Bundle;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.main.BaseActivity;

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
    }
}