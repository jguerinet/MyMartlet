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

package ca.appvelopers.mcgillmobile;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.instabug.library.Instabug;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.List;

import ca.appvelopers.mcgillmobile.background.AlarmReceiver;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.DrawerItem;
import ca.appvelopers.mcgillmobile.model.Language;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceCategory;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.UserInfo;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.util.Update;
import io.fabric.sdk.android.Fabric;

/**
 * Author: Julien
 * Date: 31/01/14, 5:42 PM
 * Class that extends the Android application and is therefore the first thing that is called when app is opened.
 * Will contain relevant objects that were loaded from the storage, and will be updated upon sign-in.
 */
public class App extends Application {
    //TODO Change these to Shared Prefs
    public static boolean forceReload = false;
    public static boolean forceUserReload = false;

    private static Context context;

    private static Typeface iconFont;

    private static Language language;
    private static DrawerItem homePage;
    private static Transcript transcript;
    private static List<Course> classes;
    private static Term defaultTerm;
    private static List<Statement> ebill;
    private static UserInfo userInfo;
    private static List<Course> wishlist;
    private static List<Place> places;
    private static List<Place> favoritePlaces;
    private static List<PlaceCategory> placeCategories;
    //List of semesters you can currently register in
    private static List<Term> registerTerms;

    //object to catch event starting background activity
    private static AlarmReceiver webFetcherReceiver = new AlarmReceiver();
    
    @Override
    public void onCreate(){
        super.onCreate();

        //Set the static context
        context = this;
        
        //Run the update code, if any
        Update.update(this);

        //Load the transcript
        transcript = Load.loadTranscript(this);
        //Load the schedule
        classes = Load.loadClasses(this);
        //Load the ebill
        ebill = Load.loadEbill(this);
        //Load the user info
        userInfo = Load.loadUserInfo(this);
        //Load the user's chosen language and update the locale
        language = Load.loadLanguage(this);
        //Load the user's chosen homepage
        homePage = Load.loadHomePage(this);
        //Load the default term for the schedule
        defaultTerm = Load.loadDefaultTerm(this);
        //Load the course wishlist
        wishlist = Load.loadClassWishlist(this);
        //Load the places
        places = Load.loadPlaces(this);
        //Load the favorite places
        favoritePlaces = Load.loadFavoritePlaces(this);
        //Load the place categories
        placeCategories = Load.loadPlaceCategories(this);
        //Load the register terms
        registerTerms = Load.loadRegisterTerms(this);

        //Set up The Fabric stuff: Twitter, Crashlytics
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Constants.TWITTER_KEY,
                Constants.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new TweetComposer(), new Crashlytics());

        //Set up Instabug
        Instabug.initialize(this, Constants.INSTABUG_KEY)
                .enableEmailField(true, false)
                .setCommentPlaceholder(getString(R.string.bug_prompt))
                .setDefaultEmail(Load.loadFullUsername(this))
                .setEmailPlaceholder(getString(R.string.bug_email_prompt))
                .setInvalidCommentAlertText(getString(R.string.bug_comment_invalid))
                .setSubmitButtonText(getString(R.string.submit))
                .setCommentIsRequired(true)
                .setDebugEnabled(BuildConfig.DEBUG)
                .setInvocationEvent(Instabug.IBGInvocationEvent.IBGInvocationEventNone)
                .setIsTrackingCrashes(false)
                .setIsTrackingUserSteps(false)
                .setShowIntroDialog(false)
                .setPostFeedbackMessage(getString(R.string.success))
                .setWillShowFeedbackSentAlert(true)
                .setUserData("Email: " + Load.loadFullUsername(this) + "\n" +
                        "App Language: " + App.getLanguage().getLanguageString());
    }

    /* GETTER METHODS */
    public static Context getContext(){
        return context;
    }

    public static Typeface getIconFont(){
        if(iconFont == null){
            iconFont = Typeface.createFromAsset(context.getAssets(), "icon-font.ttf");
        }

        return iconFont;
    }

    public static Transcript getTranscript(){
        synchronized(Constants.TRANSCRIPT_LOCK){
            return transcript;
        }
    }

    public static List<Course> getClasses(){
        return classes;
    }

    public static boolean isAlarmActive(){
    	return webFetcherReceiver.isActive();
    }

    public static List<Statement> getEbill(){
        return ebill;
    }

    public static UserInfo getUserInfo(){
        return userInfo;
    }

    public static Language getLanguage(){
        return language;
    }

    public static DrawerItem getHomePage(){
        return homePage;
    }

    public static Term getDefaultTerm(){
        return defaultTerm;
    }

    public static List<Course> getClassWishlist() {
        return wishlist;
    }

    public static List<Place> getPlaces(){
        return places;
    }

    public static List<Place> getFavoritePlaces(){
        return favoritePlaces;
    }

    public static List<PlaceCategory> getPlaceCategories(){
        return placeCategories;
    }

    public static List<Term> getRegisterTerms(){
        return registerTerms;
    }

    /* SETTERS */
    public static void setTranscript(Transcript transcript){
        synchronized (Constants.TRANSCRIPT_LOCK){
            App.transcript = transcript;

            //Save it to internal storage when this is set
            Save.saveTranscript(context);
        }
    }

    public static void setClasses(List<Course> classes){
        App.classes = classes;

        //Save it to internal storage when this is set
        Save.saveClasses(context);
    }

    public static void setEbill(List<Statement> ebill){
        App.ebill = ebill;

        //Save it to internal storage when this is set
        Save.saveEbill(context);
    }

    public static void setUserInfo(UserInfo userInfo){
        App.userInfo = userInfo;

        //Save it to internal storage when this is set
        Save.saveUserInfo(context);
    }

    public static void setLanguage(Language language){
        App.language = language;

        //Save it to internal storage when this is set
        Save.saveLanguage(context);
    }

    public static void setHomePage(DrawerItem drawerItem){
        App.homePage = drawerItem;

        //Save it to internal storage when this is set
        Save.saveHomePage(context);
    }

    public static void setDefaultTerm(Term term){
        App.defaultTerm = term;

        //Save it to internal storage when this is set
        Save.saveDefaultTerm(context);
    }

    public static void setClassWishlist(List<Course> list) {
        App.wishlist = list;
        //Save it to internal storage when this is set
        Save.saveClassWishlist(context);
    }

    public static void setPlaces(List<Place> places){
        App.places = places;
        //Save it to internal storage
        Save.savePlaces(context);
    }

    public static void setFavoritePlaces(List<Place> places){
        App.favoritePlaces = places;
        //Save it to internal storage
        Save.saveFavoritePlaces(context);
    }

    public static void setPlaceCategories(List<PlaceCategory> placeCategories){
        App.placeCategories = placeCategories;
        //Save it to internal storage
        Save.savePlaceCategories(context);
    }

    public static void setRegisterTerms(List<Term> terms){
        App.registerTerms = terms;
        //Save it to internal storage
        Save.saveRegisterTerms(context);
    }

    /* HELPER METHODS */
    
    //to be set after successful login
    public static void SetAlarm(Context context){
    	
        webFetcherReceiver.setAlarm(context);
    }
    public static void UnsetAlarm(Context context){

        webFetcherReceiver.cancelAlarm(context);
    }
}
