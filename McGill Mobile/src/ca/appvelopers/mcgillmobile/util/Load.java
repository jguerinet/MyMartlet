package ca.appvelopers.mcgillmobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.EbillItem;
import ca.appvelopers.mcgillmobile.object.Faculty;
import ca.appvelopers.mcgillmobile.object.HomePage;
import ca.appvelopers.mcgillmobile.object.Language;
import ca.appvelopers.mcgillmobile.object.Place;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.object.UserInfo;

/**
 * Author: Julien
 * Date: 31/01/14, 5:50 PM
 * Class that loads objects from internal storage or SharedPreferences
 */
public class Load {
    /**
     * Loads the app version number from the shared preferences
     * @param context The app context
     * @return the version number stored, -1 if no version stored
     */
    public static int loadVersionNumber(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getInt(Constants.VERSION, -1);
    }

    /**
     * Checks to see if the app has been previously opened
     * @param context The app context
     * @return If the app has been previously opened
     */
    public static boolean isFirstOpen(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(Constants.FIRST_OPEN, true);
    }

    public static Language loadLanguage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Language.values()[(sharedPrefs.getInt(Constants.LANGUAGE, 0))];
    }

    public static HomePage loadHomePage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int homePage = sharedPrefs.getInt(Constants.HOMEPAGE, -1);
        //Return schedule by default
        if(homePage == -1){
            return HomePage.SCHEDULE;
        }
        return HomePage.values()[homePage];
    }

    public static boolean loadStatistics(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(Constants.STATISTICS, true);
    }

    public static Faculty loadFaculty(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Faculty.values()[sharedPrefs.getInt(Constants.FACULTY, 0)];
    }

    public static String loadFullUsername(Context context){
        return loadUsername(context) + context.getString(R.string.login_email);
    }

    public static String loadUsername(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(Constants.USERNAME, null);
    }

    public static String loadPassword(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String encryptedPassword = sharedPrefs.getString(Constants.PASSWORD, null);
        if(encryptedPassword != null){
            return Encryption.decode(encryptedPassword);
        }
        return null;
    }

    public static boolean loadRememberUsername(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(Constants.REMEMBER_USERNAME, true);
    }

    public static Transcript loadTranscript(Context context){
        Transcript transcript = null;

        try{
            FileInputStream fis = context.openFileInput(Constants.TRANSCRIPT_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            transcript= (Transcript) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (OptionalDataException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("Load Transcript Failure", "File not found");
        } catch (StreamCorruptedException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Load Transcript Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        }

        return transcript;
    }

    public static List<ClassItem> loadClasses(Context context){
        List<ClassItem> courses = new ArrayList<ClassItem>();

        try{
            FileInputStream fis = context.openFileInput(Constants.CLASSES_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            courses = (List<ClassItem>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Classes Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courses;
        } catch (OptionalDataException e) {
            Log.e("Load Classes Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courses;
        } catch (FileNotFoundException e) {
            Log.e("Load Classes Failure", "File not found");
            e.printStackTrace();
            return courses;
        } catch (StreamCorruptedException e) {
            Log.e("Load Classes Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courses;
        } catch (IOException e) {
            Log.e("Load Classes Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courses;
        }

        return courses;
    }

    public static List<EbillItem> loadEbill(Context context){
        List<EbillItem> ebill = new ArrayList<EbillItem>();

        try{
            FileInputStream fis = context.openFileInput(Constants.EBILL_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            ebill = (List<EbillItem>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Ebill Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return ebill;
        } catch (OptionalDataException e) {
            Log.e("Load Ebill Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return ebill;
        } catch (FileNotFoundException e) {
            Log.e("Load Ebill Failure", "File not found");
            e.printStackTrace();
            return ebill;
        } catch (StreamCorruptedException e) {
            Log.e("Load Ebill Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return ebill;
        } catch (IOException e) {
            Log.e("Load Ebill Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return ebill;
        }

        return ebill;
    }

    public static UserInfo loadUserInfo(Context context){
        UserInfo userInfo = null;

        try{
            FileInputStream fis = context.openFileInput(Constants.USER_INFO_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            userInfo = (UserInfo) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load UserInfo Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (OptionalDataException e) {
            Log.e("Load UserInfo Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("Load UserInfo Failure", "File not found");
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.e("Load UserInfo Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Load UserInfo Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        }

        return userInfo;
    }

    public static Term loadDefaultTerm(Context context){
        Term defaultTerm = null;

        try{
            FileInputStream fis = context.openFileInput(Constants.DEFAULT_TERM_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            defaultTerm = (Term) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Default Term Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (OptionalDataException e) {
            Log.e("Load Default Term Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("Load Default Term Failure", "File not found");
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.e("Load Default Term Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Load Default Term Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        }

        return defaultTerm;
    }

    public static List<ClassItem> loadClassWishlist(Context context){
        List<ClassItem> classWishlist = new ArrayList<ClassItem>();

        try{
            FileInputStream fis = context.openFileInput(Constants.CLASS_WISHLIST_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            classWishlist = (List<ClassItem>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Class Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return classWishlist;
        } catch (OptionalDataException e) {
            Log.e("Load Class Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return classWishlist;
        } catch (FileNotFoundException e) {
            Log.e("Load Class Wishlist Failure", "File not found");
            e.printStackTrace();
            return classWishlist;
        } catch (StreamCorruptedException e) {
            Log.e("Load Class Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return classWishlist;
        } catch (IOException e) {
            Log.e("Load Class Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return classWishlist;
        }

        return classWishlist;
    }

    public static List<Place> loadPlaces(Context context){
        List<Place> places = new ArrayList<Place>();

        try{
            FileInputStream fis = context.openFileInput(Constants.PLACES_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            places = (List<Place>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        } catch (OptionalDataException e) {
            Log.e("Load Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        } catch (FileNotFoundException e) {
            Log.e("Load Places Failure", "File not found");
            e.printStackTrace();
            return places;
        } catch (StreamCorruptedException e) {
            Log.e("Load Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        } catch (IOException e) {
            Log.e("Load Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        }

        return places;
    }

    public static List<Place> loadFavoritePlaces(Context context){
        List<Place> places = new ArrayList<Place>();

        try{
            FileInputStream fis = context.openFileInput(Constants.FAVORITE_PLACES_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            places = (List<Place>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Favorite Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        } catch (OptionalDataException e) {
            Log.e("Load Favorite Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        } catch (FileNotFoundException e) {
            Log.e("Load Favorite Places Failure", "File not found");
            e.printStackTrace();
            return places;
        } catch (StreamCorruptedException e) {
            Log.e("Load Favorite Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        } catch (IOException e) {
            Log.e("Load Favorite Places Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return places;
        }

        return places;
    }

    public static List<Term> loadRegisterTerms(Context context){
        List<Term> terms = new ArrayList<Term>();

        try{
            FileInputStream fis = context.openFileInput(Constants.REGISTER_TERMS_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            terms = (List<Term>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Register Terms Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return terms;
        } catch (OptionalDataException e) {
            Log.e("Load Register Terms Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return terms;
        } catch (FileNotFoundException e) {
            Log.e("Load Register Terms Failure", "File not found");
            e.printStackTrace();
            return terms;
        } catch (StreamCorruptedException e) {
            Log.e("Load Register Terms Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return terms;
        } catch (IOException e) {
            Log.e("Load Register Terms Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return terms;
        }

        return terms;
    }

    //Last date the webservice was queried
    public static String loadIfModifiedSinceDate(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(Constants.IF_MODIFIED_SINCE, null);
    }
}
