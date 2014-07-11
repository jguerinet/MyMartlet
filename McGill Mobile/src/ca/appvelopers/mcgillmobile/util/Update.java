package ca.appvelopers.mcgillmobile.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Author : Julien
 * Date :  2014-05-26 8:41 PM
 * This class will have relevant code for each update.
 */
public class Update {
    public static void update(Context context){
        //Get the version number
        int versionNumber = -1;
        try {
            ComponentName comp = new ComponentName(context, context.getClass());
            PackageInfo info = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
            versionNumber = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Load the current version number from the preferences.
        int storedVersion = Load.loadVersionNumber(context);

        //Stored version is smaller than version number
        if(storedVersion < versionNumber){
            /* This will be where the version updates would go */

            //Store the new version in the SharedPrefs
            Save.saveVersionNumber(context, versionNumber);
        }
    }
}
