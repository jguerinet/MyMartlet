/*
 * Copyright 2014-2016 Julien Guerinet
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
import com.instabug.library.Instabug;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.net.SocketTimeoutException;
import java.util.List;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.prefs.UsernamePreference;
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
     * {@link UsernamePreference} instance
     */
    @Inject
    protected UsernamePreference usernamePref;
    /**
     * The app {@link Context}
     */
    private static Context context;
    /**
     * List of {@link Term}s that the user can currently register in
     */
    private static List<Term> registerTerms;
    /**
     * User's ebill {@link Statement}s
     */
    private static List<Statement> ebill;
    /**
     * User's chosen default {@link Term}
     */
    private static Term defaultTerm;
    /**
     * User's wishlist
     */
    private static List<CourseResult> wishlist;

    @Override
    public void onCreate(){
        super.onCreate();

        //Set the static context
        context = this;

        // Timber
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

        // Fabric: Twitter, Crashlytics
        TwitterAuthConfig authConfig = new TwitterAuthConfig(Passwords.TWITTER_KEY,
                Passwords.TWITTER_SECRET);
        @SuppressWarnings("PointlessBooleanExpression")
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder()
                .disabled(!BuildConfig.REPORT_CRASHES).build()).build();
        Fabric.with(this, new Twitter(authConfig), new TweetComposer(), crashlytics);

        // Dagger
        component = DaggerBaseComponent.builder()
                .appModule(new AppModule(this))
                .build();

        component.inject(this);

        // Android ThreeTen
        AndroidThreeTen.init(this);

        // Instabug
        Instabug.initialize(this, Passwords.INSTABUG_KEY)
                .enableEmailField(true, false)
                .setDefaultEmail(usernamePref.full())
                .setCommentIsRequired(true)
                .setDebugEnabled(false)
                .setInvocationEvent(Instabug.IBGInvocationEvent.IBGInvocationEventNone)
                .setIsTrackingCrashes(false)
                .setIsTrackingUserSteps(false)
                .setShowIntroDialog(false)
                .setWillShowFeedbackSentAlert(true);

        // FormGenerator
        int padding = getResources().getDimensionPixelOffset(R.dimen.padding_small);
        FormGenerator.set(new FormGenerator.Builder()
                .setDefaultBackground(R.drawable.transparent_redpressed)
                .setDefaultDrawablePaddingSize(padding)
                .setDefaultPaddingSize(padding)
                .setDefaultIconColor(ContextCompat.getColor(this, R.color.red)));
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
     * @return The list of {@link Term} the user can currently register in
     */
    public static List<Term> getRegisterTerms(){
        if(registerTerms == null){
            registerTerms = Load.registerTerms();
        }
        return registerTerms;
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
     * @return The user's chosen default {@link Term}
     */
    public static Term getDefaultTerm(){
        if(defaultTerm == null){
            defaultTerm = Load.defaultTerm();

            //If the default term is still null, use the current term
            if (defaultTerm == null) {
                defaultTerm = Term.currentTerm();
            }
        }
        return defaultTerm;
    }

    /**
     * @return The user's wishlist
     */
    public static List<CourseResult> getWishlist(){
        if(wishlist == null){
            wishlist = Load.wishlist();
        }
        return wishlist;
    }

    /* SETTERS */

    /**
     * @param terms The list of {@link Term}s the user can currently register in
     */
    public static void setRegisterTerms(List<Term> terms) {
        App.registerTerms = terms;
        Save.registerTerms();
    }

    /**
     * @param ebill The user's ebill {@link Statement}s
     */
    public static void setEbill(List<Statement> ebill){
        App.ebill = ebill;
        Save.ebill();
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
    public static void setWishlist(List<CourseResult> wishlist) {
        App.wishlist = wishlist;
        Save.wishlist();
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
