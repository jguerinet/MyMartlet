package ca.mcgill.mymcgill.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.util.List;

import ca.mcgill.mymcgill.object.Class;
import ca.mcgill.mymcgill.object.EbillItem;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.object.Inbox;
import ca.mcgill.mymcgill.object.Language;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.object.UserInfo;

/**
 * Author: Julien
 * Date: 31/01/14, 5:42 PM
 * Class that extends the Android application and is therefore the first thing that is called when app is opened.
 * Will contain relevant objects that were loaded from the storage, and will be updated upon sign-in.
 */
public class ApplicationClass extends Application {
    private static Context context;

    private static Typeface iconFont;

    private static Language language;
    private static HomePage homePage;
    private static Transcript transcript;
    
    private static List<Class> schedule;
    private static Semester defaultSemester;
    
    private static List<EbillItem> ebill;
    private static UserInfo userInfo;
    private static Inbox inbox;

    @Override
    public void onCreate(){
        super.onCreate();

        //Set the static context
        context = this;

        //Load the transcript
        transcript = Load.loadTranscript(this);
        //Load the schedule
        schedule = Load.loadSchedule(this);
        //Load the ebill
        ebill = Load.loadEbill(this);
        //Load the user info
        userInfo = Load.loadUserInfo(this);
        //Load the user's emails
        inbox = Load.loadInbox(this);
        //Load the user's chosen language and update the locale
        language = Load.loadLanguage(this);
        //Load the user's chosen homepage
        homePage = Load.loadHomePage(this);
        //Load the default semester for the schedule
        defaultSemester = Load.loadDefaultSemester(this);
    }

    /* GETTER METHODS */
    public static Typeface getIconFont(){
        if(iconFont == null){
            iconFont = Typeface.createFromAsset(context.getAssets(), "icon-font.ttf");
        }

        return iconFont;
    }

    public static Transcript getTranscript(){
        return transcript;
    }

    public static List<Class> getSchedule(){
        return schedule;
    }

    public static List<EbillItem> getEbill(){
        return ebill;
    }

    public static UserInfo getUserInfo(){
        return userInfo;
    }

    public static Inbox getInbox() {
        return inbox;
    }

    public static Language getLanguage(){
        return language;
    }

    public static HomePage getHomePage(){
        return homePage;
    }

    public static Semester getDefaultSemester(){
        return defaultSemester;
    }

    public static int getUnreadEmails(){
        if(inbox != null){
            return inbox.getNumNewEmails();
        }
        return 0;
    }

    /* SETTERS */
    public static void setTranscript(Transcript transcript){
        ApplicationClass.transcript = transcript;

        //Save it to internal storage when this is set
        Save.saveTranscript(context);
    }

    public static void setSchedule(List<Class> schedule){
        ApplicationClass.schedule = schedule;

        //Save it to internal storage when this is set
        Save.saveSchedule(context);
    }

    public static void setEbill(List<EbillItem> ebill){
        ApplicationClass.ebill = ebill;

        //Save it to internal storage when this is set
        Save.saveEbill(context);
    }

    public static void setUserInfo(UserInfo userInfo){
        ApplicationClass.userInfo = userInfo;

        //Save it to internal storage when this is set
        Save.saveUserInfo(context);
    }

    public static void setInbox(Inbox inbox){
        ApplicationClass.inbox = inbox;

        //Save it to internal storage when this is set
        Save.saveInbox(context);
    }

    public static void setLanguage(Language language){
        ApplicationClass.language = language;

        //Save it to internal storage when this is set
        Save.saveLanguage(context);
    }

    public static void setHomePage(HomePage homePage){
        ApplicationClass.homePage = homePage;

        //Save it to internal storage when this is set
        Save.saveHomePage(context);
    }

    public static void setDefaultSemester(Semester semester){
        ApplicationClass.defaultSemester = semester;

        //Save it to internal storage when this is set
        Save.saveDefaultSemester(context);
    }

    /* HELPER METHODS */
}
