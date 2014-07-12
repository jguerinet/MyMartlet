package ca.appvelopers.mcgillmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.InputStream;
import java.io.StringWriter;

import ca.appvelopers.mcgillmobile.R;

/**
 * Class that contains various useful static help methods
 * Author: Julien
 * Date: 04/02/14, 7:45 PM
 */
public class Help {

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

    /**
     * Get the height of the display
     * @param display The display to measure
     * @return The height of the given display
     */
    public static int getDisplayHeight(Display display){
        Point size = new Point();

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            size.set(display.getWidth(), display.getHeight());
        }
        else{
            display.getSize(size);
        }

        return size.y;
    }

    /**
     * Get the width of the display
     * @param display The display to measure
     * @return The width of the given display
     */
    public static int getDisplayWidth(Display display){
        Point size = new Point();

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            size.set(display.getWidth(), display.getHeight());
        }
        else{
            display.getSize(size);
        }

        return size.x;
    }

    /**
     * Method to open URLs
     * @param activity The activity to open this URL from
     * @param url The URL
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
     * Method to read a String from a local file
     * @param context The app context
     * @param fileResource The resource of the file to read
     * @return The file in String format
     */
    public static String readFromFile(Context context, int fileResource) {
        InputStream is = context.getResources().openRawResource(fileResource);
        StringWriter writer = new StringWriter();
        try{
            IOUtils.copy(is, writer, "UTF-8");
        } catch(Exception e){
            Log.e("Error Reading from Local File", e.getMessage());
            e.printStackTrace();
        }
        return writer.toString();
    }

    /**
     * Get the String for the "If Modified Since" part of the URL
     * @param date The date to use
     * @return The date in the correct String format
     */
    public static String getIfModifiedSinceString(DateTime date){
        return date.dayOfWeek().getAsShortText() + ", " + date.getDayOfMonth() + " " + date.monthOfYear().getAsShortText() + " " + date.getYear() + " " + date.getHourOfDay() + ":" + date.getMinuteOfHour() + ":" + date.getSecondOfMinute() + " GMT";
    }
}
