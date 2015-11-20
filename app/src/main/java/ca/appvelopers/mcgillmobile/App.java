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
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.instabug.library.Instabug;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.List;

import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.Homepage;
import ca.appvelopers.mcgillmobile.model.Language;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import ca.appvelopers.mcgillmobile.model.User;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Passwords;
import ca.appvelopers.mcgillmobile.util.Update;
import ca.appvelopers.mcgillmobile.util.background.BootReceiver;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.storage.Save;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Application implementation
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class App extends Application {
    //TODO Change these to Shared Prefs
    public static boolean forceReload = false;
    public static boolean forceUserReload = false;

    /**
     * The app {@link Context}
     */
    private static Context context;
    /**
     * App language
     */
    private static Language language;
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
     * User's chosen homepage
     */
    private static Homepage homepage;
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
        Timber.plant(new CrashlyticsTree());

        //Set up The Fabric stuff: Twitter, Crashlytics
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Passwords.TWITTER_KEY,
                Passwords.TWITTER_SECRET);
        @SuppressWarnings("PointlessBooleanExpression")
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
                .disabled(!BuildConfig.REPORT_CRASHES).build()).build();
        Fabric.with(this, new Twitter(authConfig), new TweetComposer(), crashlytics);

        //Run the update code, if any
        Update.update();

        //Set up Instabug
        Instabug.initialize(this, Passwords.INSTABUG_KEY)
                .enableEmailField(true, false)
                .setCommentPlaceholder(getString(R.string.bug_prompt))
                .setDefaultEmail(Load.fullUsername())
                .setEmailPlaceholder(getString(R.string.bug_email_prompt))
                .setInvalidCommentAlertText(getString(R.string.bug_comment_invalid))
                .setSubmitButtonText(getString(R.string.submit))
                .setCommentIsRequired(true)
                .setDebugEnabled(false)
                .setInvocationEvent(Instabug.IBGInvocationEvent.IBGInvocationEventNone)
                .setIsTrackingCrashes(false)
                .setIsTrackingUserSteps(false)
                .setShowIntroDialog(false)
                .setPostFeedbackMessage(getString(R.string.success))
                .setWillShowFeedbackSentAlert(true)
                .setUserData("Email: " + Load.fullUsername() + "\n" +
                        "App Language: " + App.getLanguage().toString());
    }

    /* GETTERS */

    /**
     * @return The app {@link Context}
     */
    public static Context getContext(){
        return context;
    }

    /**
     * @return The app language
     */
    public static Language getLanguage(){
        if(language == null){
            language = Load.language();
        }
        return language;
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
     * @return The user's chosen homepage
     */
    public static Homepage getHomepage(){
        if(homepage == null){
            homepage = Load.homepage();
        }
        return homepage;
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
     * @param language The app language
     */
    public static void setLanguage(Language language){
        App.language = language;
        Save.language();
    }

    /**
     * @param places The list of {@link Place}s
     */
    public static void setPlaces(List<Place> places){
        App.places = places;
        Save.places();
    }

    /**
     * @param placeTypes The list of {@link PlaceType}s
     */
    public static void setPlaceTypes(List<PlaceType> placeTypes){
        App.placeTypes = placeTypes;
        Save.placeTypes();
    }

    /**
     * @param terms The list of {@link Term}s the user can currently register in
     */
    public static void setRegisterTerms(List<Term> terms){
        App.registerTerms = terms;
        Save.registerTerms();
    }

    /**
     * @param transcript The user's {@link Transcript}
     */
    public static void setTranscript(Transcript transcript){
        synchronized (Constants.TRANSCRIPT_LOCK){
            App.transcript = transcript;
            Save.transcript();
        }
    }

    /**
     * @param courses The user's {@link Course}s
     */
    public static void setCourses(List<Course> courses){
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
     * @param homepage The user's chosen homepage
     */
    public static void setHomepage(Homepage homepage){
        App.homepage = homepage;
        Save.homepage();
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
    public static void setFavoritePlaces(List<Place> places){
        App.favoritePlaces = places;
        Save.favoritePlaces();
    }

    /* HELPER METHODS */
    
    //to be set after successful login
    public static void SetAlarm(Context context){
        BootReceiver.setAlarm(context);
    }
    public static void UnsetAlarm(Context context){
        BootReceiver.cancelAlarm(context);
    }

    /**
     * Timber tree that is used during crash reporting in production
     */
    private class CrashlyticsTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            //We don't want to log verbose and debug logs
            if(priority == Log.VERBOSE || priority == Log.DEBUG){
                return;
            }

            Crashlytics.log(message);
            //If there's a crash, log it too
            if(t != null){
                Crashlytics.logException(t);
            }
        }
    }
}
