package ca.mcgill.mymcgill.util;

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

import ca.mcgill.mymcgill.R;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.EbillItem;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.object.Inbox;
import ca.mcgill.mymcgill.object.Language;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.object.UserInfo;

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
        return HomePage.values()[sharedPrefs.getInt(Constants.HOMEPAGE, 0)];
    }

    public static String loadFullUsername(Context context){
        return loadUsername(context) + context.getResources().getString(R.string.login_email);
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
        return sharedPrefs.getBoolean(Constants.REMEMBER_USERNAME, false);
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

    public static List<CourseSched> loadSchedule(Context context){
        List<CourseSched> courses = new ArrayList<CourseSched>();

        try{
            FileInputStream fis = context.openFileInput(Constants.SCHEDULE_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            courses = (List<CourseSched>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Schedule Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courses;
        } catch (OptionalDataException e) {
            Log.e("Load Schedule Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courses;
        } catch (FileNotFoundException e) {
            Log.e("Load Schedule Failure", "File not found");
            e.printStackTrace();
            return courses;
        } catch (StreamCorruptedException e) {
            Log.e("Load Schedule Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courses;
        } catch (IOException e) {
            Log.e("Load Schedule Failure", e.getMessage() == null ? "" : e.getMessage());
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

    public static Inbox loadInbox(Context context){
        Inbox inbox = null;

        try{
            FileInputStream fis = context.openFileInput(Constants.INBOX_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            inbox = (Inbox) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Inbox Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (OptionalDataException e) {
            Log.e("Load Inbox Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("Load Inbox Failure", "File not found");
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.e("Load Inbox Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Load Inbox Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        }

        return inbox;
    }

    public static Semester loadDefaultSemester(Context context){
        Semester defaultSemester = null;

        try{
            FileInputStream fis = context.openFileInput(Constants.DEFAULT_SEMESTER_FILE);
            ObjectInputStream in = new ObjectInputStream(fis);
            defaultSemester = (Semester) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Default Semester Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (OptionalDataException e) {
            Log.e("Load Default Semester Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e("Load Default Semester Failure", "File not found");
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            Log.e("Load Default Semester Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Load Default Semester Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
        }

        return defaultSemester;
    }

    public static List<Course> loadCourseWishlist(Context context){
        List<Course> courseWishlist = new ArrayList<Course>();

        try{
            FileInputStream fis = context.openFileInput(Constants.COURSE_WISHLIST_FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fis);
            courseWishlist = (List<Course>) in.readObject();
        } catch (ClassNotFoundException e) {
            Log.e("Load Course Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courseWishlist;
        } catch (OptionalDataException e) {
            Log.e("Load Course Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courseWishlist;
        } catch (FileNotFoundException e) {
            Log.e("Load Course Wishlist Failure", "File not found");
            e.printStackTrace();
            return courseWishlist;
        } catch (StreamCorruptedException e) {
            Log.e("Load Course Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courseWishlist;
        } catch (IOException e) {
            Log.e("LoadCourse Wishlist Failure", e.getMessage() == null ? "" : e.getMessage());
            e.printStackTrace();
            return courseWishlist;
        }

        return courseWishlist;
    }
}
