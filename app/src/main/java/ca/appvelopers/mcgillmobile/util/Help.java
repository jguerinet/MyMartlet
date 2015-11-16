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
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.ColorRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.TextView;

import java.io.IOException;

import ca.appvelopers.mcgillmobile.App;
import okio.BufferedSource;
import okio.Okio;
import timber.log.Timber;

/**
 * Contains various useful static help methods
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class Help {

    /**
     * Tints the given drawable a given color
     *
     * @param drawable The {@link Drawable}
     * @param color    The resource Id of the color to tint the drawable with
     */
    public static void setTint(Drawable drawable, @ColorRes int color) {
        //Wrap the drawable in the DrawableCompat library first to ensure backwards compatibility
        drawable = DrawableCompat.wrap(drawable);
        //Tint the drawable with the compat library
        DrawableCompat.setTint(drawable, ContextCompat.getColor(App.getContext(), color));
    }

    /**
     * Sets the tint on the given compound drawable of the TextView
     *
     * @param textView         The {@link TextView}
     * @param drawablePosition The drawable position (0 = left, 1 = top, 2 = right, 3 = bottom)
     * @param color            The resource Id of the color to tint the drawable with
     */
    public static void setTint(TextView textView, int drawablePosition, @ColorRes int color) {
        setTint(textView.getCompoundDrawables()[drawablePosition], color);
    }

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
     * @return The app version code
     */
    public static int getVersionCode(){
        PackageInfo packageInfo = getPackageInfo();

        return packageInfo != null ? packageInfo.versionCode : -1;
    }

    /**
     * @return The app version name
     */
    public static String getVersionName(){
        PackageInfo packageInfo = getPackageInfo();

        return packageInfo != null ? packageInfo.versionName : "";
    }

    /**
     * @return The app package info
     */
    private static PackageInfo getPackageInfo(){
        try{
            return App.getContext().getPackageManager().getPackageInfo(
                    App.getContext().getPackageName(), 0);
        } catch(PackageManager.NameNotFoundException e){
            Timber.e(e, "Name not found on PackageInfo");
            return null;
        }
    }

    /**
     * @return True if the user is connected to the internet, false otherwise
     */
    public static boolean isConnected() {
        ConnectivityManager connectManager = (ConnectivityManager)
                App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * Checks if the given permission has been granted, and asks for it if it hasn't
     *
     * @param activity    The calling activity
     * @param permission  The permission needed
     * @param requestCode The request code to use if we need to ask for the permission
     * @return True if the permission has already been granted, false otherwise
     */
    public static boolean checkPermission(Activity activity, String permission, int requestCode){
        //Check that we have the permission
        if(ContextCompat.checkSelfPermission(activity, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            //Request the permission
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            return false;
        }
        //If we already have the permission, return true
        return true;
    }

}
