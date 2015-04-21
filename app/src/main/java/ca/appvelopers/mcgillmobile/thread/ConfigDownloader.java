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

package ca.appvelopers.mcgillmobile.thread;

import android.content.Context;
import android.os.AsyncTask;
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
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceCategory;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Date;
import ca.appvelopers.mcgillmobile.util.Help;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;

/**
 * Downloads the config variables and the list of places from the web server
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public abstract class ConfigDownloader extends AsyncTask<Void, Void, Void>{
    private static final String TAG = "ConfigDownloader";
    /**
     * The app context
     */
    private Context mContext;
    /**
     * True if we need to force the reloading of the data in the app, false otherwise
     */
    private boolean mForceReload;
    /**
     * The URL to download the places from
     */
    private String mPlacesURL;
    /**
     * The minimum required version of the app that the client should be running
     */
    private int mMinVersion;
    /**
     * Keeps track fo the eventual section that the error is in
     */
    private String mCurrentSection;

    /**
     * Default Constructor
     *
     * @param context The app context
     */
    public ConfigDownloader(Context context){
        this.mContext = context;
        this.mForceReload = App.forceReload;
    }

    @Override
    public Void doInBackground(Void... params){
        //Check if we are connected to the internet
        if(Help.isConnected()){
            //Load the If-Modified-Since date
            String date = Load.loadIfModifiedSinceDate(mContext);

            try {
                /* CONFIG */
                mCurrentSection = "CONFIG";

                //Initialize the OkHttp client
                OkHttpClient client = new OkHttpClient();

                //Add authentication
                client.setAuthenticator(new Authenticator() {
                    @Override
                    public Request authenticate(Proxy proxy, Response response) throws
                            IOException{
                        //Set up the credentials
                        String credentials = Credentials.basic(Constants.CONFIG_USERNAME,
                                Constants.CONFIG_PASSWORD);
                        //Add it to the passed response's request object
                        return response.request().newBuilder()
                                .header("Authorization", credentials)
                                .build();
                    }
                    @Override
                    public Request authenticateProxy(Proxy proxy, Response response)
                            throws IOException{
                        return null;
                    }
                });

                //Build the config request
                Request.Builder requestBuilder = new Request.Builder()
                        .get()
                        .url(Constants.CONFIG_URL);

                //No IfModifiedSince stuff if we are forcing the reload
                if (!mForceReload && date != null) {
                    requestBuilder.header("If-Modified-Since", date);
                }

                //Make the request and get the response
                Response response = client.newCall(requestBuilder.build()).execute();

                //Get the response code
                int responseCode = response.code();
                Log.d(TAG, "Config Response Code: " + String.valueOf(responseCode));
                if (responseCode == 200) {
                    //Set up the Gson parser by adding our custom deserializers
                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(Place.class, new PlaceDeserializer());
                    builder.registerTypeAdapter(PlaceCategory.class,
                            new PlaceCategoryDeserializer());
                    Gson gson = builder.create();
                    JsonParser parser = new JsonParser();

                    //Parse the downloaded String
                    parseConfig(gson, parser, response.body().string());

                    /* PLACES */
                    mCurrentSection = "PLACES";

                    if(mPlacesURL != null){
                        //Use the same builder, just change the URL
                        requestBuilder.url(mPlacesURL);

                        //Make the request and get the response
                        response = client.newCall(requestBuilder.build()).execute();

                        responseCode = response.code();
                        Log.d(TAG, "Places Response Code: " + String.valueOf(responseCode));
                        if (responseCode == 200) {
                            //Parse the downloaded String
                            parsePlaces(gson, parser, response.body().string());

                            //Update the If-Modified-Since date
                            Save.saveIfModifiedSinceDate(mContext, Date.getRFC1123String(
                                    DateTime.now().withZone(DateTimeZone.forID("UCT"))));
                        }
                    }
                }
            }
            catch (Exception e) {
                //Catch any possible exceptions
                Log.e(TAG, "Section Error: " + mCurrentSection, e);
            }
        }

        Log.d(TAG, "Finished");
        return null;
    }

    @Override
    protected abstract void onPostExecute(Void param);

    /* GETTERS */

    /**
     * @return The minimum version required
     */
    public int getMinVersion(){
        return this.mMinVersion;
    }

    /* HELPERS */

    /**
     * Parses the config String into the different sections of the config
     * @param gson         The GSON instance
     * @param parser       The JSON parser
     * @param configString The config String
     * @throws IOException
     */
    private void parseConfig(Gson gson, JsonParser parser, String configString) throws IOException{
        //Create the JSON object from the String
        JsonObject configJSON = parser.parse(configString).getAsJsonObject();

        //Get the min version
        mMinVersion = configJSON.get("MinAndroidVersion").getAsInt();

        //Get the Places URL
        mPlacesURL = configJSON.get("PlacesURL").getAsString();

        //Get the registration terms
        List<Term> registrationTerms = new ArrayList<>();
        JsonArray registrationTermsJSON = configJSON.getAsJsonArray("RegistrationSemesters");
        for(int i = 0; i < registrationTermsJSON.size(); i ++){
            registrationTerms.add(Term.parseTerm(registrationTermsJSON.get(i).getAsString()));
        }

        //Save the registration terms
        App.setRegisterTerms(registrationTerms);

        //Get the place categories
        List<PlaceCategory> categories = new ArrayList<>();
        JsonArray categoriesJSON = configJSON.getAsJsonArray("Place Categories");
        for(int i = 0; i < categoriesJSON.size(); i ++){
            categories.add(gson.fromJson(categoriesJSON.get(i), PlaceCategory.class));
        }

        //Save the place categories
        App.setPlaceCategories(categories);
    }

    /**
     * Parses the list of places
     *
     * @param gson         The GSON instance
     * @param parser       The JSON parser
     * @param placesString The places String
     * @throws IOException
     */
    private void parsePlaces(Gson gson, JsonParser parser, String placesString) throws IOException{
        //Convert the String into a JSON array
        JsonArray placesJSON = parser.parse(placesString).getAsJsonArray();

        //Convert the JsonArray into a list of places
        List<Place> places = gson.fromJson(placesJSON, new TypeToken<List<Place>>(){}.getType());

        //Save it if it isn't null
        if(places != null) {
            App.setPlaces(places);
        }
    }

    /* DESERIALIZERS */

    /**
     * Deserializer used to deserialize the PlaceCategory object
     */
    private static class PlaceCategoryDeserializer implements JsonDeserializer<PlaceCategory>{
        @Override
        public PlaceCategory deserialize(JsonElement json, Type type,
                                     JsonDeserializationContext context) throws JsonParseException{
            JsonObject object = json.getAsJsonObject();
            return new PlaceCategory(object.get("Name").getAsString(),
                    object.get("En").getAsString(),
                    object.get("Fr").getAsString());
        }
    }

    /**
     * Deserializer used to deserialize the Place object
     */
    private static class PlaceDeserializer implements JsonDeserializer<Place>{
        @Override
        public Place deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException{
            JsonObject object = json.getAsJsonObject();
            return new Place(object.get("Name").getAsString(),
                    (String[])context.deserialize(object.get("Categories"),
                            new TypeToken<String[]>(){}.getType()),
                    object.get("Address").getAsString(),
                    object.get("Latitude").getAsDouble(),
                    object.get("Longitude").getAsDouble());
        }
    }
}

