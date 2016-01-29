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

package ca.appvelopers.mcgillmobile.util.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Encryption;
import timber.log.Timber;

/**
 * Saves objects to internal storage or {@link SharedPreferences}
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Save {
    /* SHARED PREFS */

    /**
     * Saves an integer to the {@link SharedPreferences}
     *
     * @param key   Key to save the int under
     * @param value Value to save
     */
    private static void putInt(String key, int value) {
        App.getSharedPrefs().edit()
                .putInt(key, value)
                .apply();
    }

    /**
     * Saves a boolean to the {@link SharedPreferences}
     *
     * @param key   Key to save the boolean under
     * @param value Value to save
     */
    private static void putBoolean(String key, boolean value) {
        App.getSharedPrefs().edit()
                .putBoolean(key, value)
                .apply();
    }

    /**
     * Saves a String to the {@link SharedPreferences}
     *
     * @param key   Key to save the String under
     * @param value Value to save
     */
    private static void putString(String key, String value) {
        App.getSharedPrefs().edit()
                .putString(key, value)
                .apply();
    }

    /**
     * Saves the user's chosen language
     */
    public static void language() {
        putInt(Constants.LANGUAGE, App.getLanguage());
    }

    /**
     * Saves the user's homepage
     */
    public static void homepage() {
        putInt(Constants.HOMEPAGE, App.getHomepage());
    }

    /**
     * @param doNotShow True if we should not show the loading screen, false otherwise
     */
    public static void loadingDoNotShow(boolean doNotShow) {
        putBoolean(Constants.LOADING_DO_NOT_SHOW, doNotShow);
    }

    /**
     * @param statistics True if we can collect anonymous usage statistics, false otherwise
     */
    public static void statistics(boolean statistics) {
        putBoolean(Constants.STATISTICS, statistics);
    }

    /**
     * @param username The user's username
     */
    public static void username(String username) {
        putString(Constants.USERNAME, username);
    }

    /**
     * @param password The user's password
     */
    public static void password(String password) {
        putString(Constants.PASSWORD, Encryption.encode(password));
    }

    /**
     * @param rememberUsername True if we should remember the user's username, false otherwise
     */
    public static void rememberUsername(boolean rememberUsername) {
        putBoolean(Constants.REMEMBER_USERNAME, rememberUsername);
    }

    /**
     * @param date The last date the web service was queries
     */
    public static void ifModifiedSince(String date) {
        putString(Constants.IF_MODIFIED_SINCE, date);
    }

    /**
     * @param accepted True if the user has accepted the EULA, false otherwise
     */
    public static void eula(boolean accepted) {
        putBoolean(Constants.EULA, accepted);
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
            Timber.e(e, "Failure: %s", tag);
        }
    }

    /**
     * Saves the places
     */
    public static void places(){
        saveObject("Places", Constants.PLACES_FILE, App.getPlaces());
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
        saveObject("Courses", Constants.COURSES_FILE, App.getCourses());
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
        saveObject("User", Constants.USER_FILE, App.getUser());
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
        saveObject("Wishlist", Constants.WISHLIST_FILE, App.getWishlist());
    }

    /**
     * Saves the user's favorite places
     */
    public static void favoritePlaces(){
        saveObject("Favorite Places", Constants.FAVORITE_PLACES_FILE, App.getFavoritePlaces());
    }
}
