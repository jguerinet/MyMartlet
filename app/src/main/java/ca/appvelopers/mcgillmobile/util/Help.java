package ca.appvelopers.mcgillmobile.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import ca.appvelopers.mcgillmobile.object.Language;

/**
 * Class that contains various useful static help methods
 * Author: Julien
 * Date: 04/02/14, 7:45 PM
 */
public class Help {
    private static final String TAG = "Help";

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
     * Get the height of the display
     * @param display The display to measure
     * @return The height of the given display
     */
    public static int getDisplayHeight(Display display){
        return getDisplaySize(display).y;
    }

    /**
     * Get the width of the display
     * @param display The display to measure
     * @return The width of the given display
     */
    public static int getDisplayWidth(Display display){
        return getDisplaySize(display).x;
    }

    private static Point getDisplaySize(Display display){
        Point size = new Point();

        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            size.set(display.getWidth(), display.getHeight());
        }
        else{
            display.getSize(size);
        }

        return size;
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

    public static void sendBugReport(Context context, String title){
        //Get the necessary info
        //App Version Name & Number
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVersionName = "";
        int appVersionNumber = 0;
        if (packageInfo != null) {
            appVersionName = packageInfo.versionName;
            appVersionNumber = packageInfo.versionCode;
        }

        //OS Version
        String osVersion = Build.VERSION.RELEASE;
        //SDK Version Number
        int sdkVersionNumber = Build.VERSION.SDK_INT;

        //Manufacturer
        String manufacturer = Build.MANUFACTURER;
        //Model
        String model = Build.MODEL;

        String phoneModel;
        if (!model.startsWith(manufacturer)) {
            phoneModel = manufacturer + " " + model;
        } else {
            phoneModel = model;
        }

        //Prepare the email
        Intent bugEmail = new Intent(Intent.ACTION_SEND);
        //Recipient
        bugEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{Constants.REPORT_A_BUG_EMAIL});
        //Title
        bugEmail.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.help_bug_title,
                "Android") + " " + title);
        //Message
        bugEmail.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.help_bug_summary,
                "Android",
                App.getLanguage().getLanguageString(),
                appVersionName,
                appVersionNumber,
                osVersion,
                sdkVersionNumber,
                phoneModel,
                Load.loadFullUsername(context)));
        //Type(Email)
        bugEmail.setType("message/rfc822");
        context.startActivity(Intent.createChooser(bugEmail, context.getString(R.string.about_email_picker_title)));
    }

    /**
     * Post on Facebook
     * @param activity The calling activity
     */
    public static void postOnFacebook(final Activity activity,
                                      final CallbackManager callbackManager){
        Analytics.getInstance().sendEvent("facebook", "attempt_post", null);

        //Set up all of the info
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(activity.getString(
                        R.string.social_facebook_title, "Android"))
                .setContentDescription(activity.getString(
                        R.string.social_facebook_description_android))
                .setContentUrl(Uri.parse(activity.getString(
                        R.string.social_link_android)))
                .setImageUrl(Uri.parse(activity.getString(R
                        .string.social_facebook_image)))
                .build();

        //Show the dialog
        ShareDialog dialog = new ShareDialog(activity);
        dialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result){
                if(result.getPostId() != null){
                    //Let the user know he posted successfully
                    Toast.makeText(activity, activity.getString(R.string.social_post_success),
                            Toast.LENGTH_SHORT).show();
                    Analytics.getInstance().sendEvent("facebook", "successful_post", null);
                }
                else{
                    Log.d("Facebook Post", "Cancelled");
                }
            }

            @Override
            public void onCancel(){
                Log.d("Facebook Post", "Cancelled");
            }

            @Override
            public void onError(FacebookException e){
                Toast.makeText(activity, activity.getString(R.string.social_post_failure),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Analytics.getInstance().sendEvent("facebook", "failed_post", null);
            }
        });
        dialog.show(content);
    }

    /**
     * Method to post on Twitter
     * @param activity The calling activity
     */
    public static void postOnTwitter(final Activity activity){
        //Get the URL
        URL url = null;
        try{
            url = new URL(activity.getString(R.string.social_link_android));
        } catch(MalformedURLException e){
            e.printStackTrace();
        }

        //Set up the Tweet Composer
        TweetComposer.Builder builder = new TweetComposer.Builder(activity)
                .text(activity.getString(R.string.social_twitter_message_android, "Android"));

        //If there is a URL, add it
        if(url != null){
            builder.url(url);
        }

        //Show the TweetComposer
        builder.show();
    }

    public static int getDimensionInPixels(Context context, int dimensionId){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimensionId,
                resources.getDisplayMetrics());
    }

    /**
     * Get the app version number
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
}
