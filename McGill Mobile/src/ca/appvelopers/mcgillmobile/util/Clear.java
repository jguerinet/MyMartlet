package ca.appvelopers.mcgillmobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.EbillItem;

/**
 * Author: Julien
 * Date: 06/02/14, 12:19 PM
 * Class that clears objects from internal storage or SharedPreferences
 */
public class Clear {
    public static void clearAllInfo(Context context){
        clearSchedule(context);
        clearTranscript(context);
        clearEbill(context);
        clearUserInfo(context);
        clearPassword(context);
        if(!Load.loadRememberUsername(context)){
            clearUsername(context);
        }
    }

    public static void clearUsername(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .remove(Constants.USERNAME)
                .apply();
    }

    public static void clearPassword(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .remove(Constants.PASSWORD)
                .apply();
    }

    public static void clearTranscript(Context context){
        context.deleteFile(Constants.TRANSCRIPT_FILE);
        //Reset the static instance in Application Class
        App.setTranscript(null);
    }

    public static void clearSchedule(Context context){
        context.deleteFile(Constants.CLASSES_FILE);
        //Reset the static instance in Application Class
        App.setClasses(new ArrayList<ClassItem>());
    }

    public static void clearEbill(Context context){
        context.deleteFile(Constants.EBILL_FILE);
        //Reset the static instance in Application Class
        App.setEbill(new ArrayList<EbillItem>());
    }

    public static void clearUserInfo(Context context){
        context.deleteFile(Constants.USER_INFO_FILE);
        //Reset the static instance in Application Class
        App.setUserInfo(null);
    }
}
