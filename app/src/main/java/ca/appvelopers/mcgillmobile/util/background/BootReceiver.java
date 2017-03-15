/*
 * Copyright 2014-2017 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.util.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.guerinet.utils.prefs.BooleanPreference;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Named;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PasswordPreference;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.UsernamePreference;
import ca.appvelopers.mcgillmobile.util.service.GradeCheckerService;

/**
 * Automatically (re)starts the alarm if needed when the device is rebooted or the user opts in.
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 2.0.0
 */
public class BootReceiver extends BroadcastReceiver {
    /**
     * {@link UsernamePreference}
     */
    @Inject
    protected UsernamePreference usernamePref;
    /**
     * {@link PasswordPreference} instance
     */
    @Inject
    protected PasswordPreference passwordPref;
    /**
     * Seat checker {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.SEAT_CHECKER)
    protected BooleanPreference seatCheckerPref;
    /**
     * Grade checker {@link BooleanPreference}
     */
    @Inject
    @Named(PrefsModule.GRADE_CHECKER)
    protected BooleanPreference gradeCheckerPref;

	@Override
	public void onReceive(Context context, Intent intent) {
        App.component(context).inject(this);
        setAlarm(context, usernamePref.get(), passwordPref.get(), seatCheckerPref.get(),
                gradeCheckerPref.get());
	}

	/**
	 * Starts the alarm receiver if needed
	 *
	 * @param context The app context
	 */
	public static void setAlarm(Context context, String username, String password,
            boolean seatChecker, boolean gradeChecker) {
		//If we don't need it, don't start it
		if (username == null || password == null || (!seatChecker && !gradeChecker)) {
			//Make sure it's cancelled
			cancelAlarm(context);
			return;
		}

		//Get the alarm manager
		AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		//Set the alarm to fire at approximately 8:30 AM  according to the device's clock,
		//  and to repeat once a day.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 30);

		manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, getPendingIntent(context));
	}

	/**
	 * Cancels the alarm
	 *
	 * @param context The app context
	 */
	public static void cancelAlarm(Context context) {
		//Get the alarm manager
		AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		//Cancel the pending intent
		manager.cancel(getPendingIntent(context));
	}

	/**
	 * Gets the pending intent for the checker alarm
	 *
	 * @param context The app context
	 * @return The pending intent
	 */
	private static PendingIntent getPendingIntent(Context context){
		Intent intent = new Intent(context, GradeCheckerService.class);
		return PendingIntent.getService(context, 0, intent, 0);
	}
}