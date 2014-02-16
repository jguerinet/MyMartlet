package ca.mcgill.mymcgill.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.EbillItem;

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
        clearPassword(context);
        if(!Load.loadRememberUsername(context)){
            clearUsername(context);
        }
    }

    public static void clearUsername(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .remove(Constants.USERNAME)
                .commit();
    }

    public static void clearPassword(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .remove(Constants.PASSWORD)
                .commit();
    }

    public static void clearTranscript(Context context){
        context.deleteFile(Constants.TRANSCRIPT_FILE_NAME);
        //Reset the static instance in Application Class
        ApplicationClass.setTranscript(null);
    }

    public static void clearSchedule(Context context){
        context.deleteFile(Constants.SCHEDULE_FILE_NAME);
        //Reset the static instance in Application Class
        ApplicationClass.setSchedule(new ArrayList<CourseSched>());
    }

    public static void clearEbill(Context context){
        context.deleteFile(Constants.EBILL_FILE_NAME);
        //Reset the static instance in Application Class
        ApplicationClass.setEbill(new ArrayList<EbillItem>());
    }
}
