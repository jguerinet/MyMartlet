package ca.mcgill.mymcgill.util;

import android.app.Application;

import ca.mcgill.mymcgill.object.Transcript;

/**
 * Author: Julien
 * Date: 31/01/14, 5:42 PM
 * Class that extends the Android application and is therefore the first thing that is called when app is opened.
 * Will contain relevant objects that were loaded from the storage, and will be updated upon sign-in.
 */
public class ApplicationClass extends Application {
    private static Transcript transcript;

    @Override
    public void onCreate(){
        super.onCreate();

        //Load the transcript
        transcript = Load.loadTranscript(this);
    }

    /* GETTER METHODS */
    public static Transcript getTranscript(){
        return transcript;
    }
}
