package ca.mcgill.mymcgill;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.util.List;

import ca.mcgill.mymcgill.object.Class;
import ca.mcgill.mymcgill.object.Course;
import ca.mcgill.mymcgill.object.EbillItem;
import ca.mcgill.mymcgill.object.Faculty;
import ca.mcgill.mymcgill.object.HomePage;
import ca.mcgill.mymcgill.object.Inbox;
import ca.mcgill.mymcgill.object.Language;
import ca.mcgill.mymcgill.object.Semester;
import ca.mcgill.mymcgill.object.Transcript;
import ca.mcgill.mymcgill.object.UserInfo;
import ca.mcgill.mymcgill.util.Load;
import ca.mcgill.mymcgill.util.Save;
import ca.mcgill.mymcgill.util.Update;

/**
 * Author: Julien
 * Date: 31/01/14, 5:42 PM
 * Class that extends the Android application and is therefore the first thing that is called when app is opened.
 * Will contain relevant objects that were loaded from the storage, and will be updated upon sign-in.
 */
public class App extends Application {
    private static Context context;

    private static Typeface iconFont;

    private static Language language;
    private static HomePage homePage;
    private static Faculty faculty;
    private static Transcript transcript;
    
    private static List<Class> schedule;
    private static Semester defaultSemester;
    
    private static List<EbillItem> ebill;
    private static UserInfo userInfo;
    private static Inbox inbox;

    private static List<Course> courseWishlist;

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
        //Load the user's faculty
        faculty = Load.loadFaculty(this);
        //Load the default semester for the schedule
        defaultSemester = Load.loadDefaultSemester(this);
        //Load the course wishlist
        courseWishlist = Load.loadCourseWishlist(this);
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

    public static Faculty getFaculty(){
        return faculty;
    }

    public static Semester getDefaultSemester(){
        return defaultSemester;
    }

    public static List<Course> getCourseWishlist() {
        return courseWishlist;
    }

    public static int getUnreadEmails(){
        if(inbox != null){
            return inbox.getNumNewEmails();
        }
        return 0;
    }

    /* SETTERS */
    public static void setTranscript(Transcript transcript){
        App.transcript = transcript;

        //Save it to internal storage when this is set
        Save.saveTranscript(context);
    }

    public static void setSchedule(List<Class> schedule){
        App.schedule = schedule;

        //Save it to internal storage when this is set
        Save.saveSchedule(context);
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

    public static void setInbox(Inbox inbox){
        App.inbox = inbox;

        //Save it to internal storage when this is set
        Save.saveInbox(context);
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

    public static void setDefaultSemester(Semester semester){
        App.defaultSemester = semester;

        //Save it to internal storage when this is set
        Save.saveDefaultSemester(context);
    }

    public static void setCourseWishlist(List<Course> list) {
        App.courseWishlist = list;
        //Save it to internal storage when this is set
        Save.saveCourseWishlist(context);
    }

    /* HELPER METHODS */
}
