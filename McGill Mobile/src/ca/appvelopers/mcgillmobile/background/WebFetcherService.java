package ca.appvelopers.mcgillmobile.background;


import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ca.appvelopers.mcgillmobile.activity.transcript.TranscriptActivity;
import ca.appvelopers.mcgillmobile.object.Semester;
import ca.appvelopers.mcgillmobile.object.Transcript;
import ca.appvelopers.mcgillmobile.util.*;
import ca.appvelopers.mcgillmobile.util.downloader.TranscriptDownloader;

/**
 * Author: Shabbir
 * Date: 20/08/14, 5:42 PM
 * This class defines the tasks that will run in the background
 */

public class WebFetcherService extends IntentService {

	protected NotificationManager mNotificationManager;
	protected int NOTIFICATION_ID = 1720;
	
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
			Transcript oldTranscript;
            @Override
            protected void onPreExecute() {
                //get previous grades
            	oldTranscript = App.getTranscript();
            }

            @Override
            protected void onPostExecute(Boolean loadInfo) {
                //compare old vs new for grade changes
            	if(loadInfo){
            		Transcript newTranscript = App.getTranscript();
            		CompareTranscripts(oldTranscript,newTranscript);
            		
            	}
            }
        }.execute();
	}
	
	/**
	 * This method compares multiple aspects of the transcript and alerts the user
	 * @param oldTrans
	 * @param newTrans
	 */
	protected void CompareTranscripts(Transcript oldTrans, Transcript newTrans){
		
		//check for error
		if(oldTrans == null || newTrans == null){
			return;
		}
		
		//check if cgpa is changed
		if(Math.abs(oldTrans.getCgpa() - newTrans.getCgpa()) >= 0.01){
			LocalToast("Your new CGPA is "+newTrans.getCgpa(), TranscriptActivity.class);
			return;
		}
		
		//error check semesters
		if(oldTrans.getSemesters()==null||newTrans.getSemesters()==null)
			return;
		
		int oldIndex = 0;
		int newIndex = 0;
		//start first first
		Semester oldSem = oldTrans.getSemesters().get(oldIndex);
		Semester newSem = newTrans.getSemesters().get(newIndex);
		
		
		//TODO: compare each semesters grade
		if(oldSem == null || newSem==null ||!oldSem.getTerm().equals(newSem.getTerm())){
			//don't compare different semesters
			return;
		}
		
	}
	
	protected void LocalToast(String message,Class<?> cls){
		NotificationManager mNotificationManager = (NotificationManager)
	               this.getSystemService(Context.NOTIFICATION_SERVICE);
	    
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	            new Intent(this, cls), 0);

	        NotificationCompat.Builder mBuilder =
	                new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentTitle(getString(R.string.app_name))
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(message))
	        .setContentText(message);

	        mBuilder.setContentIntent(contentIntent);
	        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}
	
	/**
	 * This method queries minerva to check for new seat openings
	 */
	protected void CheckSeats(){
		
	}


}
