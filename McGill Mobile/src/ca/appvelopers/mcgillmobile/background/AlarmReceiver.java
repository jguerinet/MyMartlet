package ca.appvelopers.mcgillmobile.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    
    private boolean Active = false;
    
    
	@Override
	public void onReceive(Context context, Intent intent) {
		//create itent to do on Receiving the alarm
		Intent service = new Intent(context, WebFetcherService.class);
        
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
		
	}
	/**
	 * Starts the alarm receiver
	 * @param context
	 */
    public void setAlarm(Context context) {
    	Active = true;
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
 
         /* Here are some examples of ELAPSED_REALTIME_WAKEUP:
         * 
         * // Wake up the device to fire a one-time alarm in one minute.
         * alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
         *         SystemClock.elapsedRealtime() +
         *         60*1000, alarmIntent);
         *        
         * // Wake up the device to fire the alarm in 30 minutes, and every 30 minutes
         * // after that.
         * alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
         *         AlarmManager.INTERVAL_HALF_HOUR, 
         *         AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
         */
        
        // Set the alarm to fire at approximately 8:30 a.m., according to the device's
        // clock, and to repeat once a day.
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,  
        		AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
        
        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);           
    }
    
    /**
     * Cancels the alarm. If needed.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
            Active = false;
        }
        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the 
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(cancel_alarm)
    
    public boolean isActive(){
    	return Active;
    }
}
