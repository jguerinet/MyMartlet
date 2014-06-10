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
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.EbillItem;
import ca.appvelopers.mcgillmobile.object.Inbox;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.object.UserInfo;

/**
 * Author: Julien
 * Date: 04/02/14, 10:06 PM
 * Class that saves objects into internal storage or SharedPreferences
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
                .commit();
    }

    /**
     * Save that the app has been used at least once
     * @param context The app context
     */
    public static void saveFirstOpen(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.FIRST_OPEN, false)
                .commit();
    }

    public static void saveLanguage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putInt(Constants.LANGUAGE, App.getLanguage().ordinal())
                .commit();
    }

    public static void saveHomePage(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putInt(Constants.HOMEPAGE, App.getHomePage().ordinal())
                .commit();
    }

    public static void saveFaculty(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putInt(Constants.FACULTY, App.getFaculty().ordinal())
                .commit();
    }

    public static void saveUsername(Context context, String username){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putString(Constants.USERNAME, username)
                .commit();
    }

    public static void savePassword(Context context, String password){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String encryptedPassword = Encryption.encode(password);
        sharedPrefs.edit()
                .putString(Constants.PASSWORD, encryptedPassword)
                .commit();
    }

    public static void saveRememberUsername(Context context, boolean rememberUsername){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .putBoolean(Constants.REMEMBER_USERNAME, rememberUsername)
                .commit();
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

    public static void saveSchedule(Context context){
        List<ClassItem> courses = App.getClasses();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.SCHEDULE_FILE, Context.MODE_PRIVATE);
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
        List<EbillItem> ebill = App.getEbill();

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
        UserInfo userInfo = App.getUserInfo();

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

    public static void saveInbox(Context context){
        Inbox inbox = App.getInbox();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.INBOX_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(inbox);
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
        List<ClassItem> classWishlist = App.getClassWishlist();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.CLASS_WISHLIST_FILE, Context.MODE_PRIVATE);
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
}
