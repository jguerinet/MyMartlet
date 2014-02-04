package ca.mcgill.mymcgill.util;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.util.List;

import ca.mcgill.mymcgill.object.CourseSched;
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

    @Override
    public void onCreate(){
        super.onCreate();

        //Set the static context (used in loading the font)
        context = this;

        //Load the transcript
        transcript = Load.loadTranscript(this);
    }

    /* GETTER METHODS */
    public static Typeface getIconFont(){
        if(iconFont == null){
            iconFont = Typeface.createFromAsset(context.getAssets(), "icon-font.ttf");
        }

        return iconFont;
    }

    public static Transcript getTranscript(String rawTranscript){
        //If the transcript does not already exist in user's local storage, create new one
        if(transcript == null){
            transcript = new Transcript(rawTranscript);
        }

        return transcript;
    }

    public static List<CourseSched> getSchedule(){
        return schedule;
    }
}
