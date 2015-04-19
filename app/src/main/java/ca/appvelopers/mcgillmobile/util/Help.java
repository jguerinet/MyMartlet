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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Language;

/**
 * Contains various useful static help methods
 * @author Julien Guerinet
 * @version 2.0
 * @since 1.0
 */
public class Help {
    private static final String TAG = "Help";

    /* DATE STUFF  TODO: Move this to separate class */

    public static boolean timeIsAM(int hour){
        return hour / 12 == 0;
    }

    public static String getShortTimeString(Context context, int hour){
        //This is so that 12 does not become 0
        String hours = hour == 12 ? "12" : String.valueOf(hour % 12) ;

        if(timeIsAM(hour)){
            return context.getResources().getString(R.string.am, hours);
        }
        return context.getResources().getString(R.string.pm, hours);
    }

    public static String getLongTimeString(Context context, int hour, int minute){
        //This is so that 12 does not become 0
        String hours = (hour == 12) ? "12" : String.valueOf(hour % 12) ;

        //This is so minutes has 2 0's
        String minutes = String.format("%02d", minute);

        if(timeIsAM(hour)){
            return context.getResources().getString(R.string.am_long, hours, minutes);
        }
        return context.getResources().getString(R.string.pm_long, hours, minutes);
    }

    public static String getDateString(DateTime date){
        //Depending on the language chosen
        DateTimeFormatter fmt;
        if(App.getLanguage() == Language.ENGLISH){
            fmt = DateTimeFormat.forPattern("MMMM dd, yyyy");
        }
        else{
            fmt = DateTimeFormat.forPattern("dd MMMM yyyy");
        }

        return fmt.print(date);
    }

    /**
     * Gets the String for the "If Modified Since" part of the URL
     *
     *  @param date The date to use
     * @return The date in the correct String format
     */
    public static String getIfModifiedSinceString(DateTime date){
        return date.dayOfWeek().getAsShortText() + ", " + date.getDayOfMonth() + " " +
                date.monthOfYear().getAsShortText() + " " + date.getYear() + " " +
                date.getHourOfDay() + ":" + date.getMinuteOfHour() + ":" +
                date.getSecondOfMinute() + " GMT";
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
        //Get the input stream from the file
        InputStream inputStream = context.getResources().openRawResource(fileResource);
        //Get the reader from the input stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder builder = new StringBuilder();
        try{
            //Append the file's lines to the builder until there are none left
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line);
            }
        } catch(IOException e){
            Log.e(TAG, "Error reading from local file", e);
        } finally {
            //Close the stream and the reader
            try{
                inputStream.close();
                reader.close();
            } catch(IOException e){
                Log.e(TAG, "Error closing the stream and reader after reading from file", e);
            }
        }

        return builder.toString();
    }

    /**
     * Gets the app version number
     *
     * @param context The app context
     * @return The version number
     */
    public static int getVersionNumber(Context context){
        try {
            ComponentName comp = new ComponentName(context, context.getClass());
            PackageInfo info = context.getPackageManager().getPackageInfo(comp.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Check if the user is connected to the internet
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
