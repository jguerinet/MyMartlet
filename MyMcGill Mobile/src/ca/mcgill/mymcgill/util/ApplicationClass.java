package ca.mcgill.mymcgill.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.util.List;

import ca.mcgill.mymcgill.object.CourseSched;
import ca.mcgill.mymcgill.object.EbillItem;
import ca.mcgill.mymcgill.object.Transcript;

/**
 * Author: Julien
 * Date: 31/01/14, 5:42 PM
 * Class that extends the Android application and is therefore the first thing that is called when app is opened.
 * Will contain relevant objects that were loaded from the storage, and will be updated upon sign-in.
 */
public class ApplicationClass extends Application {
    private static Context context;

    private static Typeface iconFont;

    private static Transcript transcript;
    private static List<CourseSched> schedule;
    private static List<EbillItem> ebill;

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

    public static List<CourseSched> getSchedule(){
        return schedule;
    }

    public static List<EbillItem> getEbill(){
        return ebill;
    }

    /* SETTERS */
    public static void setTranscript(Transcript transcript){
        ApplicationClass.transcript = transcript;

        //Save it to internal storage when this is set
        Save.saveTranscript(context);
    }

    public static void setSchedule(List<CourseSched> schedule){
        ApplicationClass.schedule = schedule;

        //Save it to internal storage when this is set
        Save.saveSchedule(context);
    }

    public static void setEbill(List<EbillItem> ebill){
        ApplicationClass.ebill = ebill;

        //Save it to internal storage when this is set
        Save.saveEbill(context);
    }
}
