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

package ca.appvelopers.mcgillmobile.util.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Encryption;

/**
 * Saves objects to internal storage or {@link SharedPreferences}
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class Save {
    private static final String TAG = "Save";

    /* SHARED PREFS */

    /**
     * @param code The version code to save
     */
    public static void versionCode(int code){
        Constants.PREFS.edit()
                .putInt(Constants.VERSION, code)
                .apply();
    }

    /**
     * Saves that the walkthrough has been viewed at least once
     */
    public static void firstOpen(){
        Constants.PREFS.edit()
                .putBoolean(Constants.FIRST_OPEN, false)
                .apply();
    }

    /**
     * Saves the user's chosen language
     */
    public static void language(){
        Constants.PREFS.edit()
                .putInt(Constants.LANGUAGE, App.getLanguage().ordinal())
                .apply();
    }

    /**
     * Saves the user's homepage
     */
    public static void homepage(){
        Constants.PREFS.edit()
                .putInt(Constants.HOMEPAGE, App.getHomePage().ordinal())
                .apply();
    }

    /**
     * @param doNotShow True if we should not show parser errors, false otherwise
     */
    public static void parserErrorDoNotShow(boolean doNotShow){
        Constants.PREFS.edit()
                .putBoolean(Constants.PARSER_ERROR_DO_NOT_SHOW, doNotShow)
                .apply();
    }

    /**
     * @param doNotShow True if we should not show the loading screen, false otherwise
     */
    public static void loadingDoNotShow(boolean doNotShow){
        Constants.PREFS.edit()
                .putBoolean(Constants.LOADING_DO_NOT_SHOW, doNotShow)
                .apply();
    }

    /**
     * @param statistics True if we can collect anonymous usage statistics, false otherwise
     */
    public static void statistics(boolean statistics){
        Constants.PREFS.edit()
                .putBoolean(Constants.STATISTICS, statistics)
                .apply();
    }

    /**
     * @param username The user's username
     */
    public static void username(String username){
        Constants.PREFS.edit()
                .putString(Constants.USERNAME, username)
                .apply();
    }

    /**
     * @param password The user's password
     */
    public static void password(String password){
        Constants.PREFS.edit()
                .putString(Constants.PASSWORD, Encryption.encode(password))
                .apply();
    }

    /**
     * @param rememberUsername True if we should remember the user's username, false otherwise
     */
    public static void rememberUsername(boolean rememberUsername){
        Constants.PREFS.edit()
                .putBoolean(Constants.REMEMBER_USERNAME, rememberUsername)
                .apply();
    }

    /**
     * @param date The last date the web service was queries
     */
    public static void ifModifiedSince(String date){
        Constants.PREFS.edit()
                .putString(Constants.IF_MODIFIED_SINCE, date)
                .apply();
    }

    /**
     * @param accepted True if the user has accepted the EULA, false otherwise
     */
    public static void eula(boolean accepted){
        Constants.PREFS.edit()
                .putBoolean(Constants.EULA, accepted)
                .apply();
    }

    /* INTERNAL STORAGE */

    /**
     * Saves an object to internal storage
     *
     * @param tag      The tag to use in case of an error
     * @param fileName The file name to save the object under
     * @param object   The object to save
     */
    private static void saveObject(String tag, String fileName, Object object){
        try{
            FileOutputStream fos = App.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(object);
        } catch(Exception e) {
            Log.e(TAG, "Failure: " + tag, e);
        }
    }

    /**
     * Saves the user's transcript
     */
    public static void transcript(){
        saveObject("Transcript", Constants.TRANSCRIPT_FILE, App.getTranscript());
    }

    /**
     * Saves the user's courses
     */
    public static void courses(){
        saveObject("Courses", Constants.COURSES_FILE, App.getClasses());
    }

    /**
     * Saves the user's ebill statements
     */
    public static void ebill(){
        saveObject("Ebill", Constants.EBILL_FILE, App.getEbill());
    }

    /**
     * Saves the user's info
     */
    public static void user(){
        saveObject("User", Constants.USER_FILE, App.getUserInfo());
    }

    /**
     * Saves the user's default term
     */
    public static void defaultTerm(){
        saveObject("Default Term", Constants.DEFAULT_TERM_FILE, App.getDefaultTerm());
    }

    /**
     * Saves the user's wishlist
     */
    public static void wishlist(){
        saveObject("Wishlist", Constants.WISHLIST_FILE, App.getClassWishlist());
    }

    /**
     * Saves the places
     */
    public static void places(){
        saveObject("Places", Constants.PLACES_FILE, App.getPlaces());
    }

    /**
     * Saves the user's favorite places
     */
    public static void favoritePlaces(){
        saveObject("Favorite Places", Constants.FAVORITE_PLACES_FILE, App.getFavoritePlaces());
    }

    /**
     * Saves the place types
     */
    public static void placeTypes() {
        saveObject("Place Types", Constants.PLACE_TYPES_FILE, App.getPlaceTypes());
    }

    /**
     * Saves the terms the user can currently register in
     */
    public static void registerTerms(){
        saveObject("Register Terms", Constants.REGISTER_TERMS_FILE, App.getRegisterTerms());
    }
}
