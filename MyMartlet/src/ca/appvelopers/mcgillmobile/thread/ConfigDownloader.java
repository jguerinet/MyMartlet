package ca.appvelopers.mcgillmobile.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.object.Place;
import ca.appvelopers.mcgillmobile.object.PlaceCategory;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.util.Connection;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;

/**
 * Author: Julien Guerinet
 * Date: 2014-07-12 10:09 PM
 * Copyright (c) 2014 Julien Guerinet. All rights reserved.
 */
public abstract class ConfigDownloader extends AsyncTask<Void, Void, Void>{
    private Context mContext;
    private boolean mForceReload;
    private String mPlacesURL;
    private int mMinVersion;

    //This is to keep track of which section the eventual error is in
    private String mCurrentSection;

    //JSON Keys for the config
    private static final String REGISTRATION_SEMESTERS_KEY = "RegistrationSemesters";
    private static final String PLACES_URL_KEY = "PlacesURL";
    private static final String PLACE_CATEGORIES_KEY = "Place Categories";
    private static final String MIN_VERSION_KEY = "MinAndroidVersion";

    public ConfigDownloader(Context context){
        this.mContext = context;
        this.mForceReload = App.forceReload;
    }

    @Override
    public Void doInBackground(Void... params){
        //Check if we are connected to the internet
        if(Connection.isNetworkAvailable(mContext)){
            //If-Modified-Since
            String date = Load.loadIfModifiedSinceDate(mContext);

            try {
                /* CONFIG */
                mCurrentSection = "CONFIG";

                //Connect to the server
                URL url = new URL(Constants.CONFIG_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Set up the connection
                connection.setRequestMethod("GET");

                //Authentication
                String authentication = Constants.CONFIG_USERNAME + ":" + Constants.CONFIG_PASSWORD;
                String encoding = Base64.encodeToString(authentication.getBytes(), Base64.NO_WRAP);
                connection.setRequestProperty("Authorization", "Basic " + encoding);

                //No IfModifiedSince stuff if we are forcing the reload
                if (!mForceReload && date != null) {
                    connection.addRequestProperty("If-Modified-Since", date);
                }

                //Connect
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.e("Config Response Code", String.valueOf(responseCode));
                if (responseCode == 200) {
                    //Read from the input stream
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    //Parse the downloaded String
                    parseConfig(stringBuilder.toString());

                    //Terminate the connection
                    connection.disconnect();

                    /* PLACES */
                    mCurrentSection = "PLACES";

                    if(mPlacesURL != null){
                        //Connect to the server
                        url = new URL(mPlacesURL);
                        connection = (HttpURLConnection) url.openConnection();

                        //Set up the connection
                        connection.setRequestMethod("GET");
                        //Authentication
                        connection.setRequestProperty("Authorization", "Basic " + encoding);

                        //No IfModifiedSince stuff if we are forcing the reload
                        if (!mForceReload && date != null) {
                            connection.addRequestProperty("If-Modified-Since", date);
                        }

                        //Connect
                        connection.connect();

                        responseCode = connection.getResponseCode();
                        Log.e("Places Response Code", String.valueOf(responseCode));
                        if (responseCode == 200) {
                            //Read from the input stream
                            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            stringBuilder = new StringBuilder();

                            while ((line = in.readLine()) != null) {
                                stringBuilder.append(line);
                            }

                            //Parse the downloaded String
                            parsePlaces(stringBuilder.toString());

                            //Terminate the connection
                            connection.disconnect();

                            //Update the If-Modified-Since date
                            Save.saveIfModifiedSinceDate(mContext, Help.getIfModifiedSinceString(DateTime.now().withZone(DateTimeZone.forID("UCT"))));
                        }
                    }
                }
            }
            catch (Exception e) {
                Log.e("Section Error:", mCurrentSection);
                e.printStackTrace();
            }
        }
        Log.e("Data Download", "Finished");
        return null;
    }

    @Override
    protected abstract void onPostExecute(Void param);

    /**
     * @return The minimum version required
     */
    public int getMinVersion(){
        return this.mMinVersion;
    }

    private void parseConfig(String configString) throws IOException, JSONException {
        JSONObject configJSON = new JSONObject(configString);

        //Get the min version
        mMinVersion = configJSON.getInt(MIN_VERSION_KEY);

        //Get the Places URL
        mPlacesURL = configJSON.getString(PLACES_URL_KEY);

        //Get the registration terms
        List<Term> registrationTerms = new ArrayList<Term>();
        JSONArray registrationTermsJSON = configJSON.getJSONArray(REGISTRATION_SEMESTERS_KEY);
        for(int i = 0; i < registrationTermsJSON.length(); i ++){
            registrationTerms.add(Term.parseTerm(registrationTermsJSON.getString(i)));
        }

        //Save the registration terms
        App.setRegisterTerms(registrationTerms);

        //Instantiate the Jackson ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        //Get the place categories
        List<PlaceCategory> categories = new ArrayList<PlaceCategory>();
        JSONArray categoriesJSON = configJSON.getJSONArray(PLACE_CATEGORIES_KEY);
        for(int i = 0; i < categoriesJSON.length(); i ++){
            categories.add(mapper.readValue(categoriesJSON.getJSONObject(i).toString(), PlaceCategory.class));
        }

        //Save the place categories
        App.setPlaceCategories(categories);
    }

    private void parsePlaces(String placesString) throws IOException, JSONException {
        //Instantiate the Jackson ObjectMapper
        ObjectMapper mapper = new ObjectMapper();

        List<Place> places = mapper.readValue(placesString, new TypeReference<List<Place>>(){});

        //Save it if it isn't null
        if(places != null) {
            App.setPlaces(places);
        }
    }
}

