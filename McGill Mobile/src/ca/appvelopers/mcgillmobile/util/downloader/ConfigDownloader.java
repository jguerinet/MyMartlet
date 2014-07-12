package ca.appvelopers.mcgillmobile.util.downloader;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.object.Place;
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
public class ConfigDownloader extends Thread{
    private Context mContext;
    private boolean mForceReload;

    //This is to keep track of which section the eventual error is in
    private String mCurrentSection;

    public ConfigDownloader(Context context, boolean forceReload){
        this.mContext = context;
        this.mForceReload = forceReload;
    }

    @Override
    public void run(){
        //Check if we are connected to the internet
        if(!Connection.isNetworkAvailable(mContext)){
            //Check if we should download the local data
            if(mForceReload){
                //TODO Load local config
            }
        }
        else {
            //If-Modified-Since
            String date = Load.loadIfModifiedSinceDate(mContext);

            try {
                //This wil trust all certificates
                TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        @Override
                        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {}
                        @Override
                        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {}
                    }
                };

                HostnameVerifier allHostsValid = new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };

                //Set up the SSL encryption
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

                /* CONFIG */
                mCurrentSection = "PLACES";

                //Connect to the server
                URL url = new URL(Constants.PLACES_URL);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

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
                Log.e("Places Response Code", String.valueOf(responseCode));
                if (responseCode == 200) {
                    //Read from the input stream
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder placesString = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        placesString.append(line);
                    }

                    //Parse the downloaded String
                    parsePlaces(placesString.toString());

                    //Terminate the connection
                    connection.disconnect();

                    //Update the If-Modified-Since date
                    Save.saveIfModifiedSinceDate(mContext, Help.getIfModifiedSinceString(DateTime.now().withZone(DateTimeZone.forID("UCT"))));

                    //Close the connection with the content
                    connection.disconnect();

                }
            }
            catch (Exception e) {
                Log.e("Section Error:", mCurrentSection);
                e.printStackTrace();

                //For any exception, if the places are null reload from local data
                if(mForceReload || App.getPlaces() == null){
                    downloadLocalCities();
                }
            }
        }
        //Double check that there is a copy of the content in the app
        if(App.getPlaces() == null){
            downloadLocalCities();

            //Remove the If-Modified-Since to force the download next time
            Save.saveIfModifiedSinceDate(mContext, null);
        }


        Log.e("Data Download", "Finished");
    }

    //Method that downloads the places from the resources
    private void downloadLocalCities(){
        //TODO
        try{
            String fileContents = ""; //Help.readFromFile(mContext, R.raw.places);

            //Parse the data
            parsePlaces(fileContents);
        }
        catch (IOException e) {
            Log.e("IOException Section Error:", "Local Places");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.e("JSON Exception Section Error:", "Local Places");
            e.printStackTrace();
        }
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

