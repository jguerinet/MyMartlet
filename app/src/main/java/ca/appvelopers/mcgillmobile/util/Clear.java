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

import java.util.ArrayList;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.model.EbillItem;
import ca.appvelopers.mcgillmobile.model.Place;

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
        App.setClasses(new ArrayList<Course>());
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
        App.setClassWishlist(new ArrayList<Course>());
    }

    private static void clearFavoritePlaces(){
        App.setFavoritePlaces(new ArrayList<Place>());
    }
}
