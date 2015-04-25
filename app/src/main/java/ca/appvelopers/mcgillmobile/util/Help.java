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

package ca.appvelopers.mcgillmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import ca.appvelopers.mcgillmobile.App;
import okio.BufferedSource;
import okio.Okio;

/**
 * Contains various useful static help methods
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class Help {
    private static final String TAG = "Help";
    /* URLS */

    /**
     * Opens a given URL
     *
     * @param activity The calling activity
     * @param url      The URL
     */
    public static void openURL(Activity activity, String url){
        //Check that the URL starts with HTTP or HTTPS, add it if it is not the case.
        if(!url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://" + url;
        }

        Intent urlIntent = new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url));
        activity.startActivity(urlIntent);
    }

    /**
     * Gets the Docuum link for a course
     *
     * @param courseName The 4-letter name of the code
     * @param courseCode The course code number
     * @return The Docuum URL
     */
    public static String getDocuumLink(String courseName, String courseCode){
        return "http://www.docuum.com/mcgill/" + courseName.toLowerCase() + "/" + courseCode;
    }

    /* OTHER */

    /**
     * Reads a String from a local file
     *
     * @param context      The app context
     * @param fileResource The resource of the file to read
     * @return The file in String format
     */
    public static String readFromFile(Context context, int fileResource) {
        try{
            BufferedSource fileSource =
                    Okio.buffer(Okio.source(context.getResources().openRawResource(fileResource)));

            return fileSource.readUtf8();
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the app version number
     *
     * @param context The app context
     * @return The version number
     */
    public static int getVersionNumber(Context context){
        PackageInfo packageInfo = getPackageInfo();

        return packageInfo != null ? packageInfo.versionCode : -1;
    }

    public static String getVersionName(){
        PackageInfo packageInfo = getPackageInfo();

        return packageInfo != null ? packageInfo.versionName : "";
    }

    private static PackageInfo getPackageInfo(){
        try{
            return App.getContext().getPackageManager().getPackageInfo(
                    App.getContext().getPackageName(), 0);
        } catch(PackageManager.NameNotFoundException e){
            Log.e(TAG, "Name not found on PackageInfo", e);
            return null;
        }
    }

    /**
     * Checks if the user is connected to the internet
     *
     * @return True if the user is connected to the internet, false otherwise
     */
    public static boolean isConnected() {
        ConnectivityManager connectManager = (ConnectivityManager)
                App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
