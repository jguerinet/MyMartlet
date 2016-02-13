/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.util.thread;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.guerinet.utils.DateUtils;
import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.DatePreference;
import com.guerinet.utils.prefs.IntPreference;

import org.threeten.bp.ZonedDateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.model.retrofit.ConfigService;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Downloads the config variables and the list of places from the web server
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class ConfigDownloader extends Thread {
    /**
     * {@link ConnectivityManager} instance
     */
    @Inject
    protected ConnectivityManager connectivityManager;
    /**
     * Retrofit {@link ConfigService} instance
     */
    @Inject
    protected ConfigService configService;
    /**
     * The Config If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_CONFIG)
    protected DatePreference imsConfigPref;
    /**
     * The Places If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_PLACES)
    protected DatePreference imsPlacesPref;
    /**
     * The Categories If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_CATEGORIES)
    protected DatePreference imsCategoriesPref;
    /**
     * The Registration Semesters If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_REGISTRATION)
    protected DatePreference imsRegistrationPref;
    /**
     * The min version {@link IntPreference}
     */
    @Inject
    @Named(PrefsModule.MIN_VERSION)
    protected IntPreference minVersionPref;
    /**
     * The URL to download the places from
     */
    private String mPlacesURL;
    /**
     * Keeps track fo the eventual section that the error is in
     */
    private String mCurrentSection;

    /**
     * Default Constructor
     */
    public ConfigDownloader(Context context) {
        App.component(context).inject(this);
    }

    @Override
    public void run() {
        //If we're not connected to the internet, don't continue
        if (!Utils.isConnected(connectivityManager)) {
            return;
        }

        //Config
        try {
            Response<Config> response = configService
                    .config(DateUtils.getRFC1123String(imsConfigPref.getDate()))
                    .execute();

            if (response.isSuccess()) {
                minVersionPref.set(response.body().androidMinVersion);
                imsConfigPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            Timber.e(e, "Error downloading config");
        }

        //Places
        try {
            Response<List<Place>> response = configService
                    .places(DateUtils.getRFC1123String(imsPlacesPref.getDate()))
                    .execute();

            if (response.isSuccess()) {
                App.setPlaces(response.body());
                imsPlacesPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            Timber.e(e, "Error downloading places");
        }

        //Place Categories
        try {
            Response<List<PlaceType>> response = configService
                    .categories(DateUtils.getRFC1123String(imsCategoriesPref.getDate()))
                    .execute();

            if (response.isSuccess()) {
                App.setPlaceTypes(response.body());
                imsCategoriesPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            Timber.e(e, "Error downloading place categories");
        }

        //Registration Semesters
        try {
            Response<List<Term>> response = configService
                    .registrationTerms(DateUtils.getRFC1123String(imsRegistrationPref.getDate()))
                    .execute();

            if (response.isSuccess()) {
                App.setRegisterTerms(response.body());
                imsRegistrationPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            Timber.e(e, "Error downloading registration terms");
        }
    }
//    @Override
//    public Void doInBackground(Void... params){
//        //Check if we are connected to the internet
//        if(Utils.isConnected((ConnectivityManager)
//                App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE))){
//            try {
//                /* CONFIG */
//                mCurrentSection = "CONFIG";
//
//                //Initialize the OkHttp client
//                OkHttpClient client = new OkHttpClient();
//
//                //Build the config request
//                Request.Builder requestBuilder = new Request.Builder()
//                        .get()
//                        .url(Constants.CONFIG_URL);
//
//                //Make the request and get the response
//                Response response = client.newCall(requestBuilder.build()).execute();
//
//                //Get the response code
//                int responseCode = response.code();
//                Timber.i("Config Response Code: %d", responseCode);
//                if (responseCode == 200) {
//                    //Set up the Gson parser by adding our custom deserializers
//                    GsonBuilder builder = new GsonBuilder();
//                    builder.registerTypeAdapter(Place.class, new PlaceDeserializer());
//                    builder.registerTypeAdapter(PlaceType.class,
//                            new PlaceCategoryDeserializer());
//                    Gson gson = builder.create();
//                    JsonParser parser = new JsonParser();
//
//                    //Parse the downloaded String
//                    parseConfig(gson, parser, response.body().string());
//
//                    /* PLACES */
//                    mCurrentSection = "PLACES";
//
//                    if(mPlacesURL != null){
//                        //Use the same builder, just change the URL
//                        requestBuilder.url(mPlacesURL);
//
//                        //Make the request and get the response
//                        response = client.newCall(requestBuilder.build()).execute();
//
//                        responseCode = response.code();
//                        Timber.i("Places Response Code: %d", responseCode);
//                        if (responseCode == 200) {
//                            //Parse the downloaded String
//                            parsePlaces(gson, parser, response.body().string());
//                        }
//                    }
//                }
//            } catch (SocketTimeoutException e) {
//                Timber.i("Error: Socket timeout");
//            } catch (Exception e) {
//                //Catch any possible exceptions
//                Timber.e(e, "Section Error: %s", mCurrentSection);
//            }
//        }
//
//        Timber.i("Finished");
//        return null;
//    }

//    @Override
//    protected abstract void onPostExecute(Void param);

    /* HELPERS */

    /**
     * Parses the config String into the different sections of the config
     * @param gson         The GSON instance
     * @param parser       The JSON parser
     * @param configString The config String
     * @throws Exception
     */
    private void parseConfig(Gson gson, JsonParser parser, String configString) throws Exception {
        //Create the JSON object from the String
        JsonObject configJSON = parser.parse(configString).getAsJsonObject();

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
        List<PlaceType> categories = new ArrayList<>();
        JsonArray categoriesJSON = configJSON.getAsJsonArray("Place Categories");
        for(int i = 0; i < categoriesJSON.size(); i ++){
            categories.add(gson.fromJson(categoriesJSON.get(i), PlaceType.class));
        }

        //Save the place categories
        App.setPlaceTypes(categories);
    }

    /**
     * Parses the list of places
     *
     * @param gson         The GSON instance
     * @param parser       The JSON parser
     * @param placesString The places String
     * @throws Exception
     */
    private void parsePlaces(Gson gson, JsonParser parser, String placesString) throws Exception {
        //Convert the String into a JSON array
        JsonArray placesJSON = parser.parse(placesString).getAsJsonArray();

        //Convert the JsonArray into a list of places
        List<Place> places = null;
//        List<Place> places = gson.fromJson(placesJSON, new TypeToken<List<Place>>(){}.getType());

        //Save it if it isn't null
        if(places != null) {
            App.setPlaces(places);
        }
    }

    /* DESERIALIZERS */

    /**
     * Deserializer used to deserialize the Place object
     */
    private static class PlaceDeserializer implements JsonDeserializer<Place>{
        @Override
        public Place deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException{
            JsonObject object = json.getAsJsonObject();
            return null;
//            return new Place(object.get("Name").getAsString(),
//                    (String[])context.deserialize(object.get("Categories"),
//                            new TypeToken<String[]>(){}.getType()),
//                    object.get("Address").getAsString(),
//                    object.get("Latitude").getAsDouble(),
//                    object.get("Longitude").getAsDouble());
        }
    }

    /**
     * Config skeleton class
     */
    public static class Config {
        /**
         * Minimum version of the app that the user needs
         */
        protected int androidMinVersion = -1;
    }
}

