package ca.appvelopers.mcgillmobile.background;

import ca.appvelopers.mcgillmobile.App;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;



public class WebFetcherService extends IntentService {

    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
	
	public WebFetcherService() {
		super("SchedulingService");
		
	}

	/**
	 * Here is where the actual logic goes when the alarm is called
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		sendNotification("Background Task started");
		
		//release the wake lock of the phone
		AlarmReceiver.completeWakefulIntent(intent);
	}
	
    // Post a notification indicating the service was started.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
               this.getSystemService(Context.NOTIFICATION_SERVICE);
    
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
            new Intent(this, App.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
