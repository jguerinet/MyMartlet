package ca.appvelopers.mcgillmobile.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.DrawerItem;
import ca.appvelopers.mcgillmobile.object.EbillItem;
import ca.appvelopers.mcgillmobile.object.Place;

/**
 * Author: Julien
 * Date: 06/02/14, 12:19 PM
 * Class that clears objects from internal storage or SharedPreferences
 */
public class Clear {
    public static void clearAllInfo(Context context){
        if(!Load.loadRememberUsername(context)){
            clearUsername(context);
        }
        clearPassword(context);
        clearSchedule();
        clearTranscript();
        clearEbill();
        clearUserInfo();
        clearHomepage();
        clearDefaultTerm();
        clearWishlist();
        clearFavoritePlaces();
    }

    private static void clearUsername(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .remove(Constants.USERNAME)
                .apply();
    }

    private static void clearPassword(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefs.edit()
                .remove(Constants.PASSWORD)
                .apply();
    }

    private static void clearTranscript(){
        App.setTranscript(null);
    }

    private static void clearSchedule(){
        App.setClasses(new ArrayList<ClassItem>());
    }

    private static void clearEbill(){
        //Reset the static instance in Application Class
        App.setEbill(new ArrayList<EbillItem>());
    }

    private static void clearUserInfo(){
        //Reset the static instance in Application Class
        App.setUserInfo(null);
    }

    private static void clearHomepage(){
        App.setHomePage(DrawerItem.SCHEDULE);
    }

    private static void clearDefaultTerm(){
        App.setDefaultTerm(null);
    }

    private static void clearWishlist(){
        App.setClassWishlist(new ArrayList<ClassItem>());
    }

    private static void clearFavoritePlaces(){
        App.setFavoritePlaces(new ArrayList<Place>());
    }
}
