/*
 * Copyright 2014-2015 Appvelopers Inc.
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

package ca.appvelopers.mcgillmobile.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(Place.class, new PlaceDeserializer());
                    builder.registerTypeAdapter(PlaceCategory.class, new PlaceCategoryDeserializer());
                    Gson gson = builder.create();
                    JsonParser parser = new JsonParser();

                    //Parse the downloaded String
                    parseConfig(gson, parser, stringBuilder.toString());

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
                            parsePlaces(gson, parser, stringBuilder.toString());

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

    private void parseConfig(Gson gson, JsonParser parser, String configString) throws IOException {
        JsonObject configJSON = (JsonObject)parser.parse(configString);

        //Get the min version
        mMinVersion = configJSON.get(MIN_VERSION_KEY).getAsInt();

        //Get the Places URL
        mPlacesURL = configJSON.get(PLACES_URL_KEY).getAsString();

        //Get the registration terms
        List<Term> registrationTerms = new ArrayList<Term>();
        JsonArray registrationTermsJSON = configJSON.getAsJsonArray(REGISTRATION_SEMESTERS_KEY);
        for(int i = 0; i < registrationTermsJSON.size(); i ++){
            registrationTerms.add(Term.parseTerm(registrationTermsJSON.get(i).getAsString()));
        }

        //Save the registration terms
        App.setRegisterTerms(registrationTerms);

        //Get the place categories
        List<PlaceCategory> categories = new ArrayList<PlaceCategory>();
        JsonArray categoriesJSON = configJSON.getAsJsonArray(PLACE_CATEGORIES_KEY);
        for(int i = 0; i < categoriesJSON.size(); i ++){
            categories.add(gson.fromJson(categoriesJSON.get(i), PlaceCategory.class));
        }

        //Save the place categories
        App.setPlaceCategories(categories);
    }

    private void parsePlaces(Gson gson, JsonParser parser, String placesString) throws IOException, JSONException {
        JsonArray placesJSON = parser.parse(placesString).getAsJsonArray();

        List<Place> places = gson.fromJson(placesJSON, new TypeToken<List<Place>>(){}.getType());

        //Save it if it isn't null
        if(places != null) {
            App.setPlaces(places);
        }
    }

    private static class PlaceCategoryDeserializer implements JsonDeserializer<PlaceCategory>{
        @Override
        public PlaceCategory deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException{
            JsonObject object = json.getAsJsonObject();
            return new PlaceCategory(object.get("Name").getAsString(),
                    object.get("En").getAsString(),
                    object.get("Fr").getAsString());
        }
    }

    private static class PlaceDeserializer implements JsonDeserializer<Place>{
        @Override
        public Place deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException{

            JsonObject object = json.getAsJsonObject();
            return new Place(object.get("Name").getAsString(),
                    (String[])context.deserialize(object.get("Categories"), new TypeToken<String[]>(){}.getType()),
                    object.get("Address").getAsString(),
                    object.get("Latitude").getAsDouble(),
                    object.get("Longitude").getAsDouble());
        }
    }
}

