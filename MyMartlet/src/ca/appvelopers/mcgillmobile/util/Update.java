package ca.appvelopers.mcgillmobile.util;

import android.content.Context;

import ca.appvelopers.mcgillmobile.App;

/**
 * Author : Julien
 * Date :  2014-05-26 8:41 PM
 * This class will have relevant code for each update.
 */
public class Update {
    public static void update(Context context){
        //Get the version number
        int versionNumber = Help.getVersionNumber(context);

        //Load the current version number from the preferences.
        int storedVersion = Load.loadVersionNumber(context);

        //Stored version is smaller than version number
        while(storedVersion < versionNumber){
            //First time opening the app
            if(storedVersion == -1){
                init();
                //Break out of the loop
                break;
            }
            if(storedVersion == 6){
                update7();
            }

            /* This will be where the version updates would go */

            storedVersion ++;
        }

        //Store the new version in the SharedPrefs
        Save.saveVersionNumber(context, versionNumber);
    }

    /**
     * Run anything that needs to be run the first time the app is opened
     */
    private static void init(){
        //Force the config downloader
        App.forceReload = true;
    }

    private static void update7(){
        //Force the user to re-update all of the information in the app
        App.forceUserReload = true;
    }
}
