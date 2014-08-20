package ca.appvelopers.mcgillmobile.background;

import ca.appvelopers.mcgillmobile.App;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.util.*;
import ca.appvelopers.mcgillmobile.util.downloader.TranscriptDownloader;

/**
 * Author: Shabbir
 * Date: 20/08/14, 5:42 PM
 * This class defines the tasks that will run in the background
 */

public class WebFetcherService extends IntentService {

	
	public WebFetcherService() {
		super("SchedulingService");
		
	}

	/**
	 * Here is where the actual logic goes when the alarm is called
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.v("Background: ","service started");
		
		//check seats int the background
		CheckSeats();
		
		//compare grades in transcript
		CheckGrade();
		
		//release the wake lock of the phone
		AlarmReceiver.completeWakefulIntent(intent);
	}
	
	/**
	 * This method queries the transcript to check for new or changed grades
	 */
	protected void CheckGrade(){
		new TranscriptDownloader(this) {
            @Override
            protected void onPreExecute() {
                //get previous grades
            	Transcript oldTranscript = App.getTranscript();
            }

            @Override
            protected void onPostExecute(Boolean loadInfo) {
                //compare old vs new for grade changes
            }
        }.execute();
	}
	
	/**
	 * This method queries minerva to check for new seat openings
	 */
	protected void CheckSeats(){
		
	}


}
