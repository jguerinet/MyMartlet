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

package ca.appvelopers.mcgillmobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.User;

/**
 * TODO
 * Saves objects into internal storage or SharedPreferences
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class Save {
    /**
     * Save the version number to the Shared prefs
     * @param context The app context
     * @param versionNumber The version number to save
     */
    public static void saveVersionNumber(Context context, int versionNumber){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putInt(Constants.VERSION, versionNumber)
                .apply();
    }

    /**
     * Save that the app has been used at least once
     * @param context The app context
     */
    public static void saveFirstOpen(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.FIRST_OPEN, false)
                .apply();
    }

    public static void saveLanguage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putInt(Constants.LANGUAGE, App.getLanguage().ordinal())
                .apply();
    }

    public static void saveHomePage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putInt(Constants.HOMEPAGE, App.getHomePage().ordinal())
                .apply();
    }

    public static void saveParserErrorDoNotShow(Context context, boolean doNotShow){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.PARSER_ERROR_DO_NOT_SHOW, doNotShow)
                .apply();
    }

    public static void saveLoadingDoNotShow(Context context, boolean doNotShow){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.LOADING_DO_NOT_SHOW, doNotShow)
                .apply();
    }

    public static void saveStatistics(Context context, boolean statistics){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.STATISTICS, statistics)
                .apply();
    }

    public static void saveUsername(Context context, String username){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putString(Constants.USERNAME, username)
                .apply();
    }

    public static void savePassword(Context context, String password){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String encryptedPassword = Encryption.encode(password);
        sharedPrefs.edit()
                .putString(Constants.PASSWORD, encryptedPassword)
                .apply();
    }

    public static void saveRememberUsername(Context context, boolean rememberUsername){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.REMEMBER_USERNAME, rememberUsername)
                .apply();
    }

    public static void saveTranscript(Context context){
        Transcript transcript = App.getTranscript();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.TRANSCRIPT_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(transcript);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveClasses(Context context){
        List<Course> courses = App.getClasses();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.COURSES_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(courses);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveEbill(Context context){
        List<Statement> ebill = App.getEbill();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.EBILL_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(ebill);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserInfo(Context context){
        User userInfo = App.getUserInfo();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.USER_INFO_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(userInfo);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveDefaultTerm(Context context){
        Term defaultTerm = App.getDefaultTerm();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.DEFAULT_TERM_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(defaultTerm);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveClassWishlist(Context context) {
        List<Course> classWishlist = App.getClassWishlist();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.WISHLIST_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(classWishlist);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePlaces(Context context) {
        List<Place> places = App.getPlaces();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.PLACES_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(places);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFavoritePlaces(Context context) {
        List<Place> places = App.getFavoritePlaces();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.FAVORITE_PLACES_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(places);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePlaceCategories(Context context) {
        List<PlaceType> places = App.getPlaceTypes();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.PLACE_TYPES_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(places);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveRegisterTerms(Context context){
        List<Term> terms = App.getRegisterTerms();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.REGISTER_TERMS_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(terms);
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveIfModifiedSinceDate(Context context, String date){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putString(Constants.IF_MODIFIED_SINCE, date)
                .apply();
    }

    /**
     * Save if the user agreement has been accepted or not
     *
     * @param context  The app context
     * @param accepted True if it has been accepted, false otherwise
     */
    public static void saveUserAgreement(Context context, boolean accepted){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.USER_AGREEMENT, accepted)
                .apply();
    }
}
