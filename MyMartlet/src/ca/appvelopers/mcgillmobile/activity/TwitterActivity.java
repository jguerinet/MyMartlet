package ca.appvelopers.mcgillmobile.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Help;
import twitter4j.TwitterException;

/**
 * Author: Julien Guerinet
 * Date: 2014-09-05 5:21 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */

public class TwitterActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Twitter Callback
        new AsyncTask<Uri,Void,Void>(){
            protected Void doInBackground(Uri... args){
                Uri uri = args[0];
                if (uri != null && uri.toString().startsWith(Constants.TWITTER_CALLBACK_URL)) {
                    // oAuth verifier
                    String verifier = uri.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);
                    try {
                        Constants.twitter.getOAuthAccessToken(Constants.requestToken, verifier);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Help.postOnTwitter(TwitterActivity.this);
                        }
                    });
                }
                return null;
            }
        }.execute(getIntent().getData());
    }
}