package ca.appvelopers.mcgillmobile.activity.drawer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.activity.DesktopActivity;
import ca.appvelopers.mcgillmobile.activity.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.activity.RegistrationActivity;
import ca.appvelopers.mcgillmobile.activity.SettingsActivity;
import ca.appvelopers.mcgillmobile.activity.base.BaseActivity;
import ca.appvelopers.mcgillmobile.activity.courseslist.CoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.ebill.EbillActivity;
import ca.appvelopers.mcgillmobile.activity.mycourseslist.MyCoursesListActivity;
import ca.appvelopers.mcgillmobile.activity.transcript.TranscriptActivity;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.GoogleAnalytics;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

public class DrawerActivity extends BaseActivity {

    public DrawerLayout drawerLayout;
    public ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private static final String TWITTER_CALLBACK_URL = "oauth://mymartlet";
    private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    private static Twitter twitter;
    private static RequestToken requestToken;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        assert (getActionBar() != null);

        // R.id.drawer_layout should be in every activity with exactly the same id.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setFocusableInTouchMode(false);

        //Set up the drawer toggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0);
        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Set up the adapter
        DrawerAdapter drawerAdapter;
        if(this instanceof TranscriptActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.TRANSCRIPT_POSITION);
        }
        else if(this instanceof RegistrationActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.SEARCH_COURSES_POSITION);
        }
        else if(this instanceof EbillActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.EBILL_POSITION);
        }
        else if(this instanceof DesktopActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.DESKTOP_POSITION);
        }
        else if(this instanceof SettingsActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.SETTINGS_POSITION);
        }
        else if(this instanceof MyCoursesActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.MYCOURSES_POSITION);
        }
        else if(this instanceof CoursesListActivity){
            //Wishlist
            if(((CoursesListActivity)this).wishlist){
                drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.WISHLIST_POSITION);
            }
            //Course search
            else{
                drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.SEARCH_COURSES_POSITION);
            }
        }
        else if(this instanceof MyCoursesListActivity){
            drawerAdapter = new DrawerAdapter(this, drawerLayout, DrawerAdapter.COURSES_POSITION);
        }
        else{
            Log.e("Drawer Adapter", "not well initialized");
            drawerAdapter = new DrawerAdapter(this, drawerLayout, -1);
        }

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(drawerAdapter);

        //Twitter Callback
        new AsyncTask<Uri,Void,Void>(){
            protected Void doInBackground(Uri... args){
                Uri uri = args[0];
                if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
                    // oAuth verifier
                    String verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
                    try {
                        twitter.getOAuthAccessToken(requestToken, verifier);
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postOnTwitter();
                        }
                    });
                }
                return null;
            }
        }.execute(getIntent().getData());
    }

    @Override
    public void onBackPressed(){
        //Open the menu if it is not open
        if(!drawerLayout.isDrawerOpen(drawerList)){
            drawerLayout.openDrawer(drawerList);
        }
        //If it is open, ask the user if he wants to exit
        else{
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.drawer_exit))
                    .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DrawerActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public static void loginTwitter(final Activity activity){
        GoogleAnalytics.sendEvent(activity, "twitter", "attempt_post", null, null);

        //Login using AsyncTask, using the keys stored in Constants.
        new AsyncTask<Void,Void,Void>(){

            protected Void doInBackground(Void... args){
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
                builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
                twitter4j.conf.Configuration config = builder.build();

                //Prepare the Twitter Object
                TwitterFactory twitterFactory = new TwitterFactory(config);
                twitter = twitterFactory.getInstance();

                try{
                    requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
                } catch (TwitterException e) {
                    final String detailMessage;

                    //Choose the correct error message
                    if(e.getMessage().contains("Received authentication challenge is null")){
                        //If this is the case, it's because the time of the device might be wrong
                        detailMessage = activity.getString(R.string.twitter_post_failure_time);
                    }
                    else{
                        detailMessage = activity.getString(R.string.social_post_failure);
                    }

                    //Display error message
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(activity)
                                    .setTitle(activity.getString(R.string.error))
                                    .setMessage(detailMessage)
                                    .setNeutralButton(activity.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });
                    GoogleAnalytics.sendEvent(activity, "twitter", "failed_post", null, null);

                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void postOnTwitter(){
        //Show dialog where user can edit his message (pre-defined message given)
        //Inflate the view
        View dialogView = View.inflate(DrawerActivity.this, R.layout.dialog_edittext, null);

        //Set the title
        ((TextView)dialogView.findViewById(R.id.dialog_title)).setText(getString(R.string.title_twitter));

        //Create the Builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DrawerActivity.this);
        //Set up the view
        alertDialogBuilder.setView(dialogView);
        //EditText
        final EditText userInput = (EditText) dialogView.findViewById(R.id.dialog_input);
        userInput.setText(getString(R.string.social_twitter_message_android));

        //Set up the dialog
        alertDialogBuilder.setCancelable(false)
                .setNegativeButton(getResources().getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(getResources().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Get the user input
                                String statusMessage = userInput.getText().toString();
                                //If statusMessage = null, user cancelled so do nothing
                                if (statusMessage != null) {
                                    //Add the link at the end of his message
                                    statusMessage += " " + getResources().getString(R.string.social_link_android);

                                    //Post using an AsyncTask
                                    new AsyncTask<String, Void, Void>() {
                                        protected Void doInBackground(String... args) {
                                            try {
                                                //Retrieve the message
                                                String message = args[0];

                                                //Prepare StatusUpdate object
                                                StatusUpdate status = new StatusUpdate(message);
                                                twitter.updateStatus(status);

                                                DrawerActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        GoogleAnalytics.sendEvent(DrawerActivity.this, "twitter", "successful_post", null, null);
                                                        Toast.makeText(DrawerActivity.this, DrawerActivity.this.getResources().getString(R.string.social_post_success), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } catch (TwitterException e) {
                                                GoogleAnalytics.sendEvent(DrawerActivity.this, "twitter", "failed_post", null, null);
                                                Log.e("Twitter Status", "Error:" + e.getMessage());
                                                DrawerActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(DrawerActivity.this, DrawerActivity.this.getResources().getString(R.string.social_post_failure), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                e.printStackTrace();
                                            }

                                            return null;
                                        }
                                    }.execute(statusMessage);
                                }
                            }
                        })
                .create().show();
    }
}
