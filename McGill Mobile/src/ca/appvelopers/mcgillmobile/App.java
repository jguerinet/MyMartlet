package ca.appvelopers.mcgillmobile;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.object.ClassItem;
import ca.appvelopers.mcgillmobile.object.EbillItem;
import ca.appvelopers.mcgillmobile.object.Faculty;
import ca.appvelopers.mcgillmobile.object.HomePage;
import ca.appvelopers.mcgillmobile.object.Language;
import ca.appvelopers.mcgillmobile.object.Place;
import ca.appvelopers.mcgillmobile.object.Season;
import ca.appvelopers.mcgillmobile.object.Term;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.object.UserInfo;
import ca.appvelopers.mcgillmobile.util.Constants;
import ca.appvelopers.mcgillmobile.util.Load;
import ca.appvelopers.mcgillmobile.util.Save;
import ca.appvelopers.mcgillmobile.util.Test;
import ca.appvelopers.mcgillmobile.util.Update;
import ca.appvelopers.mcgillmobile.util.downloader.ConfigDownloader;
import ca.appvelopers.mcgillmobile.background.*;

/**
 * Author: Julien
 * Date: 31/01/14, 5:42 PM
 * Class that extends the Android application and is therefore the first thing that is called when app is opened.
 * Will contain relevant objects that were loaded from the storage, and will be updated upon sign-in.
 */
public class App extends Application {
    public static boolean forceReload = false;

    private static Context context;

    private static Typeface iconFont;

    private static Language language;
    private static HomePage homePage;
    private static Faculty faculty;
    private static Transcript transcript;
    private static List<ClassItem> classes;
    private static Term defaultTerm;
    private static List<EbillItem> ebill;
    private static UserInfo userInfo;
    private static List<ClassItem> wishlist;
    private static List<Place> places;

    //List of semesters you can currently register in
    //TODO Find a way to make this dynamic
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
        //Load the user's faculty
        faculty = Load.loadFaculty(this);
        //Load the default term for the schedule
        defaultTerm = Load.loadDefaultTerm(this);
        //Load the course wishlist
        wishlist = Load.loadClassWishlist(this);
        //Load the places
        places = Load.loadPlaces(this);

        //Set up the register terms
        registerTerms = new ArrayList<Term>();
        registerTerms.add(new Term(Season.SUMMER, 2014));
        registerTerms.add(new Term(Season.FALL, 2014));
        registerTerms.add(new Term(Season.WINTER, 2015));

        //Download the new config
        new ConfigDownloader(this, forceReload).start();

        /* TESTING CODE */
        if(Test.LOCAL_TRANSCRIPT){
            Test.testTranscript(this);
        }
    }

    /* GETTER METHODS */
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

    public static List<ClassItem> getClasses(){
        return classes;
    }

    public static List<EbillItem> getEbill(){
        return ebill;
    }

    public static UserInfo getUserInfo(){
        return userInfo;
    }

    public static Language getLanguage(){
        return language;
    }

    public static HomePage getHomePage(){
        return homePage;
    }

    public static Faculty getFaculty(){
        return faculty;
    }

    public static Term getDefaultTerm(){
        return defaultTerm;
    }

    public static List<ClassItem> getClassWishlist() {
        return wishlist;
    }

    public static List<Place> getPlaces(){
        return places;
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

    public static void setClasses(List<ClassItem> classes){
        App.classes = classes;

        //Save it to internal storage when this is set
        Save.saveClasses(context);
    }

    public static void setEbill(List<EbillItem> ebill){
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

    public static void setHomePage(HomePage homePage){
        App.homePage = homePage;

        //Save it to internal storage when this is set
        Save.saveHomePage(context);
    }

    public static void setFaculty(Faculty faculty){
        App.faculty = faculty;

        Save.saveFaculty(context);
    }

    public static void setDefaultTerm(Term term){
        App.defaultTerm = term;

        //Save it to internal storage when this is set
        Save.saveDefaultTerm(context);
    }

    public static void setClassWishlist(List<ClassItem> list) {
        App.wishlist = list;
        //Save it to internal storage when this is set
        Save.saveClassWishlist(context);
    }

    public static void setPlaces(List<Place> places){
        App.places = places;
        //Save it to internal storage
        Save.savePlaces(context);
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
