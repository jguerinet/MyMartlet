package ca.mcgill.mymcgill.util;

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

import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.Transcript;

/**
 * Author: Julien
 * Date: 04/02/14, 10:06 PM
 * Class that saves objects into internal storage or SharedPreferences
 */
public class Save {
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

    public static void saveSchedule(Context context){
        List<CourseSched> courses = ApplicationClass.getSchedule();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.SCHEDULE_FILE_NAME, Context.MODE_PRIVATE);
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

    public static void saveTranscript(Context context){
        Transcript transcript = ApplicationClass.getTranscript();

        try{
            FileOutputStream fos = context.openFileOutput(Constants.TRANSCRIPT_FILE_NAME, Context.MODE_PRIVATE);
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
}
