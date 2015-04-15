package ca.appvelopers.mcgillmobile.util;

import android.content.Context;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

import java.util.Map;


/**
 * Author : Julien Guerinet
 * Date : 07/06/14, 11:41 AM
 */
public class GoogleAnalytics {
    public static void sendEvent(Context context, String category, String action, String label, Long value){
        if(!Constants.dev){
            EasyTracker easyTracker = EasyTracker.getInstance(context);

            Map<String, String> event =
                    MapBuilder.createEvent(
                            category,
                            action,
                            label,
                            value
                    ).build();

            easyTracker.send(event);

            Log.e("GA Event", category + ", " + action + ", " + label + ", " + value);
        }
    }

    public static void sendScreen(Context context, String screenName){
        if(!Constants.dev){
            EasyTracker easyTracker = EasyTracker.getInstance(context);

            easyTracker.set(Fields.SCREEN_NAME, screenName);

            easyTracker.send(MapBuilder
                    .createAppView()
                    .build());

            Log.e("GA Screen", screenName);
        }
    }
}
