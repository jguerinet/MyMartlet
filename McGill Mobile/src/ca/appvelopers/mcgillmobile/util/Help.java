package ca.appvelopers.mcgillmobile.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

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

    /**
     * Get the Docuum link for a course
     * @param courseName The 4-letter name of the code
     * @param courseCode The course code number
     * @return The Docuum URL
     */
    public static String getDocuumLink(String courseName, String courseCode){
        return "http://www.docuum.com/mcgill/" + courseName.toLowerCase() + "/" + courseCode;
    }

    /**
     * Post on Facebook
     * @param activity The calling activity
     */
    public static void postOnFacebook(final Activity activity){
        GoogleAnalytics.sendEvent(activity, "facebook", "attempt_post", null, null);

        //Start Facebook Login
        Session.openActiveSession(activity, true, new Session.StatusCallback() {
            @Override
            public void call(final Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    //Make request to the /me API (Access to user's profile)
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {

                            //The bundle with the post params
                            Bundle postParams = new Bundle();
                            //All of the params are in the strings
                            String title = activity.getResources().getString(R.string.social_facebook_title, "Android");
                            String link = activity.getResources().getString(R.string.social_link_android);
                            String description = activity.getResources().getString(R.string.social_facebook_description_android);
                            postParams.putString("name", title);
                            postParams.putString("caption", activity.getResources().getString(R.string.social_facebook_caption));
                            postParams.putString("description", description);
                            postParams.putString("link", link);
                            postParams.putString("picture", activity.getResources().getString(R.string.social_facebook_image));

                            //Create a new FeedDialogBuilder so the user can configure the post on his wall
                            com.facebook.widget.WebDialog.FeedDialogBuilder feedDialogBuilder = new com.facebook.widget.WebDialog.FeedDialogBuilder(activity, Session.getActiveSession(), postParams);
                            feedDialogBuilder.setOnCompleteListener(new com.facebook.widget.WebDialog.OnCompleteListener() {

                                @Override
                                public void onComplete(Bundle values, FacebookException error) {
                                    if (error == null) {
                                        final String postId = values.getString("post_id");
                                        //Success
                                        if (postId != null) {
                                            //Let the user know he posted successfully
                                            Toast.makeText(activity, activity.getResources().getString(R.string.social_post_success), Toast.LENGTH_SHORT).show();
                                            GoogleAnalytics.sendEvent(activity, "facebook", "successful_post", null, null);
                                        }
                                        //Cancelled
                                        else {
                                            // User clicked the Cancel button
                                            Log.e("Facebook Post", "Cancelled");
                                        }
                                    } else if (error instanceof FacebookOperationCanceledException) {
                                        // User clicked the "x" button
                                        Log.e("Facebook Post", "Cancelled");
                                    }
                                    //Tell the user an error occurred
                                    else {
                                        Toast.makeText(activity, activity.getResources().getString(R.string.social_post_failure), Toast.LENGTH_SHORT).show();
                                        error.printStackTrace();
                                        GoogleAnalytics.sendEvent(activity, "facebook", "failed_post", null, null);
                                    }
                                }
                            });
                            com.facebook.widget.WebDialog feedDialog = feedDialogBuilder.build();
                            feedDialog.show();
                        }
                    }).executeAsync();
                } else if (exception != null) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.social_post_failure), Toast.LENGTH_SHORT).show();
                    GoogleAnalytics.sendEvent(activity, "facebook", "failed_post", null, null);
                    exception.printStackTrace();
                }
            }
        });
    }
}
