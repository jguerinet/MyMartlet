/*
 * Copyright 2014-2016 Appvelopers
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
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.guerinet.formgenerator.FormGenerator;
import com.guerinet.utils.ProductionTree;
import com.instabug.library.Feature;
import com.instabug.library.IBGInvocationEvent;
import com.instabug.library.IBGInvocationMode;
import com.instabug.library.Instabug;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.SocketTimeoutException;
import java.util.List;

import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.User;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Passwords;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.storage.Save;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Application implementation
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class App extends Application {
    /**
     * Dagger {@link BaseComponent}
     */
    private BaseComponent component;
    /**
     * The app {@link Context}
     */
    private static Context context;
    /**
     * List of {@link Place}s
     */
    private static List<Place> places;
    /**
     * List of {@link PlaceType}s
     */
    private static List<PlaceType> placeTypes;
    /**
     * List of {@link Term}s that the user can currently register in
     */
    private static List<Term> registerTerms;
    /**
     * User's {@link Transcript}
     */
    private static Transcript transcript;
    /**
     * User's {@link Course}s
     */
    private static List<Course> courses;
    /**
     * User's ebill {@link Statement}s
     */
    private static List<Statement> ebill;
    /**
     * {@link User} instance
     */
    private static User user;
    /**
     * User's chosen default {@link Term}
     */
    private static Term defaultTerm;
    /**
     * User's wishlist
     */
    private static List<Course> wishlist;
    /**
     * User's list of favorite {@link Place}s
     */
    private static List<Place> favoritePlaces;

    @Override
    public void onCreate(){
        super.onCreate();

        //Set the static context
        context = this;

        //Set up Timber
        if (BuildConfig.DEBUG) {
           Timber.plant(new Timber.DebugTree());
        }
        if (BuildConfig.REPORT_CRASHES) {
            Timber.plant(new ProductionTree() {
                @Override
                protected void log(String message) {
                    Crashlytics.log(message);
                }

                @Override
                protected void logException(Throwable t) {
                    //Don't log SocketTimeoutExceptions
                    if (!(t instanceof SocketTimeoutException)) {
                        Crashlytics.logException(t);
                    }
                }
            });
        }

        //Set up The Fabric stuff: Twitter, Crashlytics
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Passwords.TWITTER_KEY,
                Passwords.TWITTER_SECRET);
        @SuppressWarnings("PointlessBooleanExpression")
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
                .disabled(!BuildConfig.REPORT_CRASHES).build()).build();
        Fabric.with(this, new Twitter(authConfig), new TweetComposer(), crashlytics);

        //Initialize the Dagger component
        component = DaggerBaseComponent.builder()
                .appModule(new AppModule(this))
                .build();

        //Initialize ATT
        AndroidThreeTen.init(this);

        //Set up Instabug
        new Instabug.Builder(this, BuildConfig.DEBUG ?
                Passwords.INSTABUG_DEBUG_KEY : Passwords.INSTABUG_KEY)
                .setInvocationEvent(IBGInvocationEvent.IBGInvocationEventNone)
                .setDefaultInvocationMode(IBGInvocationMode.IBGInvocationModeFeedbackSender)
                .setEmailFieldRequired(true)
                .setCommentFieldRequired(true)
                .setDebugEnabled(false)
                .setConsoleLogState(Feature.State.ENABLED)
                .setCrashReportingState(Feature.State.DISABLED)
                .setInAppMessagingState(Feature.State.DISABLED)
                .setInstabugLogState(Feature.State.DISABLED)
                .setPushNotificationState(Feature.State.DISABLED)
                .setTrackingUserStepsState(Feature.State.DISABLED)
                .setUserDataState(Feature.State.ENABLED)
                .build();
        Instabug.setPrimaryColor(ContextCompat.getColor(this, R.color.red));

        //Set up the FormGenerator
        FormGenerator.set(new FormGenerator.Builder()
                .setDefaultIconColorId(R.color.red)
                .setDefaultBackground(R.drawable.transparent_redpressed)
                .setDefaultPaddingSize(R.dimen.padding_small));
    }

    /* GETTERS */

    /**
     * @param context App context
     * @return The {@link BaseComponent} instance
     */
    public static BaseComponent component(Context context) {
        return ((App) context.getApplicationContext()).component;
    }

    /**
     * @return The app {@link Context}
     */
    public static Context getContext() {
        return context;
    }

    /**
     * @return The list of {@link Place}s
     */
    public static List<Place> getPlaces(){
        if(places == null){
            places = Load.places();
        }
        return places;
    }

    /**
     * @return The list of {@link PlaceType}s
     */
    public static List<PlaceType> getPlaceTypes(){
        if(placeTypes == null){
            placeTypes = Load.placeTypes();
        }
        return placeTypes;
    }

    /**
     * @return The list of {@link Term} the user can currently register in
     */
    public static List<Term> getRegisterTerms(){
        if(registerTerms == null){
            registerTerms = Load.registerTerms();
        }
        return registerTerms;
    }

    /**
     * @return The user's {@link Transcript}
     */
    public static Transcript getTranscript(){
        synchronized(Constants.TRANSCRIPT_LOCK){
            if(transcript == null){
                transcript = Load.transcript();
            }
            return transcript;
        }
    }

    /**
     * @return The user's list of {@link Course}s
     */
    public static List<Course> getCourses(){
        if(courses == null){
            courses = Load.classes();
        }
        return courses;
    }

    /**
     * @return The user's ebill {@link Statement}s
     */
    public static List<Statement> getEbill(){
        if(ebill == null){
            ebill = Load.ebill();
        }
        return ebill;
    }

    /**
     * @return The {@link User} info
     */
    public static User getUser(){
        if(user == null){
            user = Load.user();
        }
        return user;
    }

    /**
     * @return The user's chosen default {@link Term}
     */
    public static Term getDefaultTerm(){
        if(defaultTerm == null){
            defaultTerm = Load.defaultTerm();
        }
        return defaultTerm;
    }

    /**
     * @return The user's wishlist
     */
    public static List<Course> getWishlist(){
        if(wishlist == null){
            wishlist = Load.wishlist();
        }
        return wishlist;
    }

    /**
     * @return The user's list of favorite {@link Place}s
     */
    public static List<Place> getFavoritePlaces(){
        if(favoritePlaces == null){
            favoritePlaces = Load.favoritePlaces();
        }
        return favoritePlaces;
    }

    /* SETTERS */

    /**
     * @param places The list of {@link Place}s
     */
    public static void setPlaces(List<Place> places) {
        App.places = places;
        Save.places();
    }

    /**
     * @param placeTypes The list of {@link PlaceType}s
     */
    public static void setPlaceTypes(List<PlaceType> placeTypes) {
        App.placeTypes = placeTypes;
        Save.placeTypes();
    }

    /**
     * @param terms The list of {@link Term}s the user can currently register in
     */
    public static void setRegisterTerms(List<Term> terms) {
        App.registerTerms = terms;
        Save.registerTerms();
    }

    /**
     * @param transcript The user's {@link Transcript}
     */
    public static void setTranscript(Transcript transcript) {
        synchronized (Constants.TRANSCRIPT_LOCK){
            App.transcript = transcript;
            Save.transcript();
        }
    }

    /**
     * @param courses The user's {@link Course}s
     */
    public static void setCourses(List<Course> courses) {
        App.courses = courses;
        Save.courses();
    }

    /**
     * @param ebill The user's ebill {@link Statement}s
     */
    public static void setEbill(List<Statement> ebill){
        App.ebill = ebill;
        Save.ebill();
    }

    /**
     * @param user The {@link User} info
     */
    public static void setUser(User user){
        App.user = user;
        Save.user();
    }

    /**
     * @param term The user's chosen default {@link Term}
     */
    public static void setDefaultTerm(Term term){
        App.defaultTerm = term;
        Save.defaultTerm();
    }

    /**
     * @param wishlist The user's wishlist
     */
    public static void setWishlist(List<Course> wishlist) {
        App.wishlist = wishlist;
        Save.wishlist();
    }

    /**
     * @param places The user's list of favorite {@link Place}s
     */
    public static void setFavoritePlaces(List<Place> places) {
        App.favoritePlaces = places;
        Save.favoritePlaces();
    }

    /* HELPER METHODS */

//    //to be set after successful login
//    public static void SetAlarm(Context context){
////        BootReceiver.setAlarm(context);
//    }
//    public static void UnsetAlarm(Context context){
//        BootReceiver.cancelAlarm(context);
//    }
}
